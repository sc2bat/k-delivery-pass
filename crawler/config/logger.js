const fs = require('fs');
const { createLogger, format, transports } = require('winston');
require('winston-daily-rotate-file');

const logDir = 'logs';
if (!fs.existsSync(logDir)) fs.mkdirSync(logDir);

const logger = createLogger({
    level: 'info',
    format: format.combine(
        format.timestamp({ format: 'YYYY-MM-DD HH:mm:ss' }),
        format.printf(({ timestamp, level, message }) => `${timestamp} [${level.toUpperCase()}]: ${message}`)
    ),
    transports: [
        new transports.Console(),
        new transports.DailyRotateFile({
            filename: `${logDir}/crawler-%DATE%.log`,
            datePattern: 'YYYY-MM-DD',
            zippedArchive: true,
            maxSize: '20m',
            maxFiles: '30d'
        })
    ]
});

module.exports = logger;