require('dotenv').config();
const mysql = require('mysql2/promise');

async function testDB() {
    console.log('DB 접속 테스트 시작...');
    console.log(`   - Host: ${process.env.DB_HOST}`);
    console.log(`   - User: ${process.env.DB_USER}`);
    console.log(`   - Port: ${process.env.DB_PORT || 3306}`);

    try {
        const connection = await mysql.createConnection({
            host: process.env.DB_HOST,
            user: process.env.DB_USER,
            password: process.env.DB_PASSWORD,
            database: process.env.DB_NAME,
            port: process.env.DB_PORT || 3306,
            
            allowPublicKeyRetrieval: true, 
            // SSL 설정 대비
            ssl: {
                rejectUnauthorized: false
            }
        });

        console.log(' DB 연결 성공!');
        
        const [rows] = await connection.execute('SELECT 1 as val');
        console.log(' 쿼리 결과:', rows);

        await connection.end();
    } catch (e) {
        console.error(' DB 연결 실패:', e.message);
        console.error('   -> 에러 코드:', e.code);
        
        if (e.code === 'ETIMEDOUT') {
             console.error('힌트: .env 파일에서 DB_HOST를 "localhost" 대신 "127.0.0.1"로 바꿔보세요.');
        }
    }
}

testDB();
