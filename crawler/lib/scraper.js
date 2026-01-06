// lib/scraper.js
const puppeteer = require('puppeteer');

// 가격 문자열에서 숫자만 추출하는 헬퍼 함수
function parsePrice(priceStr) {
    if (!priceStr) return 0;
    return parseInt(priceStr.replace(/[^0-9]/g, ''), 10);
}

// 브라우저 초기화
async function initBrowser() {
    const browser = await puppeteer.launch({
        headless: false,
        executablePath: '/usr/bin/google-chrome',
        args: ['--no-sandbox', '--disable-setuid-sandbox', '--disable-notifications', '--window-size=1280,960']
    });

    const context = browser.defaultBrowserContext();
    await context.overridePermissions('https://m.place.naver.com', ['geolocation']);

    const page = await browser.newPage();
    // 위치 설정 (홍은동)
    await page.setGeolocation({ latitude: 37.5985, longitude: 126.9453 });
    await page.setUserAgent('Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36');
    await page.setViewport({ width: 375, height: 812 });

    return { browser, page };
}

// 특정 키워드로 가게 목록 수집 (카테고리 정보 포함)
async function fetchStores(page, keyword, dbCategoryName) {
    const searchUrl = `https://m.place.naver.com/restaurant/list?query=${encodeURIComponent(keyword)}`;
    await page.goto(searchUrl, { waitUntil: 'networkidle2' });

    // 팝업 닫기 시도
    try {
        const closeBtn = await page.$('span._3h-N8');
        if (closeBtn) await closeBtn.click();
    } catch (e) {}

    try {
        await page.waitForSelector('.ww_F4', { timeout: 5000 });
    } catch (e) {
        return []; // 검색 결과 없음
    }

    return await page.evaluate((catName) => {
        const results = [];
        const nameElements = document.querySelectorAll('.ww_F4');
        
        nameElements.forEach(el => {
            const container = el.closest('div[id]');
            if (container && container.id) {
                results.push({
                    name: el.innerText,
                    category: catName, 
                    url: `https://m.place.naver.com/restaurant/${container.id}/menu`
                });
            }
        });
        return results.slice(0, 3); // 테스트용 3개 제한
    }, dbCategoryName);
}

// 메뉴 상세 정보 수집
async function fetchMenus(page, storeUrl) {
    await page.goto(storeUrl, { waitUntil: 'networkidle2' });

    try {
        await page.waitForSelector('li', { timeout: 3000 });
    } catch (e) {
        return []; // 메뉴 없음
    }

    return await page.evaluate(() => {
        const list = [];
        const items = document.querySelectorAll('li'); 
        
        items.forEach(el => {
            let name = null, price = null, img = el.querySelector('img')?.src, desc = '';

            // 1. 클래스 기반 탐색
            const nameEl = el.querySelector('.lPzHi'); 
            const priceEl = el.querySelector('.GXS1X');
            const descEl = el.querySelector('.kPogF');

            if (nameEl && priceEl) {
                name = nameEl.innerText;
                price = priceEl.innerText;
                if (descEl) desc = descEl.innerText;
            } else {
                // 2. 텍스트 구조 기반 탐색 (백업)
                const texts = el.querySelectorAll('div, span, strong');
                texts.forEach(t => {
                    const txt = t.innerText.trim();
                    if (!price && /^[0-9,]+원$/.test(txt)) price = txt;
                });
                if (price) {
                    for (let t of texts) {
                        const txt = t.innerText.trim();
                        if (txt !== price && txt.length > 0 && txt.length < 20 && !txt.includes('주문')) {
                            name = txt; break;
                        }
                    }
                }
            }

            if (name && price) list.push({ name, price, desc, img });
        });
        return list;
    });
}

module.exports = {
    initBrowser,
    fetchStores,
    fetchMenus,
    parsePrice
};