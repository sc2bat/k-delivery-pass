require('dotenv').config();
const puppeteer = require('puppeteer');

async function main() {
    console.log('🕵️ [Debug Mode] 메뉴판 HTML 구조 분석 중...');

    // 1. 브라우저 열기 (Linux 호환 옵션 포함)
    const browser = await puppeteer.launch({
        headless: false,
        executablePath: '/usr/bin/google-chrome', 
        args: [
            '--no-sandbox',
            '--disable-setuid-sandbox',
            '--disable-notifications',
            '--window-size=1280,960'
        ]
    });

    // 2. 위치 권한 허용 (팝업 방지)
    const context = browser.defaultBrowserContext();
    await context.overridePermissions('https://m.place.naver.com', ['geolocation']);

    const page = await browser.newPage();
    await page.setGeolocation({ latitude: 37.5985, longitude: 126.9453 }); // 홍은동 좌표
    await page.setViewport({ width: 375, height: 812 });

    // 3. 테스트를 위해 확실한 가게(예: 교촌치킨) 메뉴 페이지로 바로 접속
    // (아까 발견된 가게 ID가 있다면 그걸 써도 됩니다. 여기선 임의의 ID 사용)
    const sampleUrl = 'https://m.place.naver.com/restaurant/11802711/menu'; 
    console.log(` 접속 URL: ${sampleUrl}`);
    
    await page.goto(sampleUrl, { waitUntil: 'networkidle2' });

    console.log('⏳ 메뉴 리스트 로딩 대기...');

    try {
        // 메뉴 리스트(li)가 뜰 때까지 기다림
        await page.waitForSelector('ul > li', { timeout: 10000 });
    } catch (e) {
        console.log('⚠️ 메뉴 리스트(li)를 못 찾았습니다. (스크린샷 저장)');
        await page.screenshot({ path: 'menu_debug_error.png' });
        await browser.close();
        return;
    }

    // 4. [핵심] 첫 번째 메뉴 아이템의 HTML을 통째로 긁어오기
    const htmlDump = await page.evaluate(() => {
        // 메뉴가 들어있는 li 태그들 중 첫 번째 것을 찾음
        // 보통 메뉴는 이미지가 있는 것과 없는 것이 섞여 있으니, 텍스트가 좀 있는 놈으로 골라봄
        const items = document.querySelectorAll('li');
        
        for (let item of items) {
            // 가격 정보("원")가 들어있는 li를 찾으면 그게 메뉴일 확률 99%
            if (item.innerText.includes('원')) {
                return item.outerHTML;
            }
        }
        return items[0] ? items[0].outerHTML : "메뉴(li)를 찾았으나 내용이 비어있음";
    });

    console.log('\n============== [메뉴판 HTML 시작] ==============');
    console.log(htmlDump);
    console.log('============== [메뉴판 HTML 끝] ==============\n');
    console.log('👉 위 [메뉴판 HTML] 내용을 복사해서 알려주세요!');

    await browser.close();
}

main();