-- Create and select database
DROP DATABASE IF EXISTS uhpooh;
CREATE SCHEMA IF NOT EXISTS `uhpooh` DEFAULT 
CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE uhpooh;

-- Users table
CREATE TABLE users (
    userId INT AUTO_INCREMENT PRIMARY KEY,
    userEmail VARCHAR(255) NOT NULL unique,
    password VARBINARY(255),
    userName VARCHAR(50) NOT NULL,
    userAddress VARCHAR(50) NOT NULL,
    chatId VARCHAR(50),
    pImage VARCHAR(255),
    isAdmin BOOLEAN DEFAULT FALSE,
    regTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    updTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    isLogin BOOLEAN DEFAULT FALSE
);
-- Token table
CREATE TABLE token (
    id INTEGER NOT NULL AUTO_INCREMENT,
    isLogin BOOLEAN DEFAULT FALSE,
    userId INTEGER,
    accessToken VARCHAR(255),
    refreshToken VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (userId) REFERENCES users(userId)
) ENGINE=InnoDB;

-- Places table
CREATE TABLE places (
    placeId INT AUTO_INCREMENT PRIMARY KEY,
    kakaoPlaceId VARCHAR(100) NOT NULL UNIQUE,
    reviewCount INT DEFAULT 0,
    likeCount INT DEFAULT 0,
    regTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    updTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idxKakaoPlaceId (kakaoPlaceId)
);

-- Reviews table
CREATE TABLE reviews (
    reviewId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,    
    placeId INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    images VARCHAR(255),
    regTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    updTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE,
    FOREIGN KEY (placeId) REFERENCES places(placeId) ON DELETE CASCADE,
    INDEX idxUserPlace (userId, placeId)
);

-- Likes table
CREATE TABLE likes (
    likeId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    placeId INT NOT NULL,
    regTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE,
    FOREIGN KEY (placeId) REFERENCES places(placeId) ON DELETE CASCADE,
    UNIQUE KEY uniqueUserPlace (userId, placeId)
);

-- Payments table
CREATE TABLE payments (
    paymentId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    tossPaymentKey VARCHAR(200) NOT NULL,
    amount INT NOT NULL,
    status ENUM('READY', 'IN_PROGRESS', 'DONE', 'CANCELED', 'FAILED') DEFAULT 'READY',
    regTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    updTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE,
    INDEX idxOrderId (paymentId),
    INDEX idxPaymentKey (tossPaymentKey)
);

ALTER TABLE places
ADD COLUMN placeName VARCHAR(255) NOT NULL;

ALTER TABLE reviews DROP COLUMN images;

-- 리뷰 이미지를 저장할 새로운 테이블 생성
CREATE TABLE reviewImages (
    imageId INT AUTO_INCREMENT PRIMARY KEY,
    reviewId INT NOT NULL,
    imageUrl VARCHAR(255) NOT NULL,
    FOREIGN KEY (reviewId) REFERENCES reviews(reviewId) ON DELETE CASCADE,
    INDEX idx_review (reviewId)
);

ALTER TABLE users 
ADD COLUMN provider VARCHAR(255);

ALTER TABLE users 
MODIFY COLUMN userAddress VARCHAR(255) NULL;

ALTER TABLE users
ALTER COLUMN provider SET DEFAULT 'local';

ALTER TABLE token
DROP FOREIGN KEY token_ibfk_1;

ALTER TABLE token
ADD CONSTRAINT token_ibfk_1
FOREIGN KEY (userId) REFERENCES users(userId)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE users 
modify COLUMN userName VARCHAR(50);

ALTER TABLE users 
modify COLUMN userEmail VARCHAR(50);