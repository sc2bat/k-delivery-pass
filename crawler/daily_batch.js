// daily_batch.js
require('dotenv').config();
const pool = require('./config/db');       // DB 모듈 임포트
const logger = require('./config/logger'); // 로거 모듈 임포트
const scraper = require('./lib/scraper');  // 크롤러 모듈 임포트

async function main() {
    logger.info(' [Baedal Mate] 배치 시작 (모듈화 버전)');

    // 1. DB에서 카테고리 가져오기
    let categoryRows = [];
    try {
        const [rows] = await pool.query("SELECT id, name FROM categories WHERE is_active = TRUE");
        categoryRows = rows;
        logger.info(` 수집 대상: ${categoryRows.length}개 카테고리`);
    } catch (e) {
        logger.error(' DB 연결 실패 또는 카테고리 테이블 없음');
        return;
    }

    const region = process.env.TARGET_REGION || "홍은동";
    
    // 2. 브라우저 실행 (lib/scraper.js 사용)
    const { browser, page } = await scraper.initBrowser();

    // 3. 카테고리별 순회
    for (const cat of categoryRows) {
        const keyword = `${region} ${cat.name}`;
        logger.info(`\n검색: "${keyword}" (ID: ${cat.id})`);

        try {
            // 스크래퍼 모듈을 통해 가게 목록 가져오기
            const storeLinks = await scraper.fetchStores(page, keyword, cat.name);
            logger.info(`    매장 ${storeLinks.length}개 발견`);

            // 가게별 상세 수집 및 DB 저장
            for (const store of storeLinks) {
                logger.info(`   Processing: ${store.name}`);
                
                // 스크래퍼 모듈을 통해 메뉴 가져오기
                const menuData = await scraper.fetchMenus(page, store.url);

                if (menuData.length > 0) {
                    await saveToDB(store, menuData, region);
                    logger.info(`      - 메뉴 ${menuData.length}개 저장 완료`);
                } else {
                    logger.warn(`      (메뉴 정보 없음)`);
                }
                
                await new Promise(r => setTimeout(r, 1000));
            }

        } catch (err) {
            logger.error(` [${cat.name}] 에러: ${err.message}`);
        }
        
        await new Promise(r => setTimeout(r, 3000)); // 카테고리 간 휴식
    }

    logger.info(' 배치 종료');
    await browser.close();
    pool.end();
}

// DB 저장 함수 (트랜잭션 관리 때문에 여기 두는 것이 좋음, 혹은 service 폴더로 빼도 됨)
async function saveToDB(store, menuData, region) {
    const connection = await pool.getConnection();
    try {
        await connection.beginTransaction();

        const homeUrl = store.url.replace('/menu', '/home');
        
        // 가게 저장
        const [storeRes] = await connection.query(`
            INSERT INTO stores (store_name, category_id, address, origin_url, is_open)
            VALUES (?, ?, ?, ?, 'Y')
            ON DUPLICATE KEY UPDATE 
                category_id = VALUES(category_id), 
                last_updated = NOW()
        `, [store.name, categoryId, region, homeUrl]);

        let storeId = storeRes.insertId;
        if (storeId === 0) {
            const [rows] = await connection.query('SELECT store_id FROM stores WHERE store_name = ?', [store.name]);
            if (rows.length > 0) {
                storeId = rows[0].store_id;
            } else {
                throw new Error(`가게 ID 조회 실패: ${store.name}`);
            }
        }

        // 메뉴 저장
        for (const m of menuData) {
            const priceInt = scraper.parsePrice(m.price);
            await connection.query(`
                INSERT INTO menus (store_id, menu_name_kr, price, image_url, description)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE price = VALUES(price), description = VALUES(description), image_url = VALUES(image_url)
            `, [storeId, m.name, priceInt, m.img, m.desc]);
        }
        await connection.commit();
    } catch (err) {
        await connection.rollback();
        throw err;
    } finally {
        connection.release();
    }
}

main();
