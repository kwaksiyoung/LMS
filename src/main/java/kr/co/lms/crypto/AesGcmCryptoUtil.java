package kr.co.lms.crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256-GCM 암호화 유틸리티
 * KISA 권장 표준 준수 (개인정보 보호법 안전성 확보조치 기준 제7조)
 * 
 * - 알고리즘: AES-256-GCM
 * - 키 길이: 256비트
 * - GCM 태그 길이: 128비트
 * - IV 길이: 96비트 (12바이트)
 * 
 * 암호화 결과 형식: [IV(12바이트) + 암호문 + GCM태그] → Base64 인코딩
 */
public class AesGcmCryptoUtil {
    
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128; // 비트 (인증 태그 길이)
    private static final int GCM_IV_LENGTH = 12;   // 바이트 (96비트 - GCM 권장 길이)
    private static final int AES_KEY_SIZE = 256;   // 비트 (KISA 권장)
    
    /**
     * AES-256 키 생성 (초기화 시 또는 키 로테이션 시)
     * 
     * @return 새로 생성된 AES-256 SecretKey
     * @throws Exception 키 생성 실패 시
     */
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE, new SecureRandom());
        return keyGen.generateKey();
    }
    
    /**
     * 평문을 AES-256-GCM으로 암호화
     * 
     * 암호화 과정:
     * 1. 96비트 IV(Initialization Vector) 무작위 생성
     * 2. GCM 파라미터 설정 (IV + 128비트 인증 태그)
     * 3. 평문 암호화 (GCM이 자동으로 인증 태그 생성)
     * 4. [IV + 암호문(인증태그 포함)] → Base64 인코딩
     * 
     * @param plainText 평문 (암호화할 개인정보, 예: email, phone, address)
     * @param secretKey 비밀키 (AES-256)
     * @return Base64 인코딩된 암호문 (IV + 암호문)
     * @throws Exception 암호화 실패 시
     */
    public static String encrypt(String plainText, SecretKey secretKey) throws Exception {
        if (plainText == null || plainText.isEmpty()) {
            return plainText; // null 또는 빈 문자열은 암호화하지 않음
        }
        
        // Step 1: 난수 IV 생성 (96비트 = 12바이트)
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        
        // Step 2: Cipher 초기화
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
        
        // Step 3: 암호화 수행 (GCM이 자동으로 인증 태그 포함)
        byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF-8"));
        
        // Step 4: IV + 암호문 결합
        byte[] encryptedData = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, encryptedData, 0, iv.length);
        System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);
        
        // Base64 인코딩 후 반환
        return Base64.getEncoder().encodeToString(encryptedData);
    }
    
    /**
     * Base64 인코딩된 암호문을 복호화
     * 
     * 복호화 과정:
     * 1. Base64 디코딩 → [IV(12바이트) + 암호문]
     * 2. IV 추출 (처음 12바이트)
     * 3. GCM 파라미터로 Cipher 초기화 (IV 사용)
     * 4. 암호문 복호화 (GCM이 자동으로 인증 태그 검증)
     * 
     * @param encryptedText Base64 인코딩된 암호문
     * @param secretKey 비밀키 (동일한 AES-256)
     * @return 복호화된 평문
     * @throws Exception 복호화 실패 또는 인증 태그 검증 실패 시
     */
    public static String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText; // null 또는 빈 문자열은 복호화하지 않음
        }
        
        // Step 1: Base64 디코딩
        byte[] encryptedData = Base64.getDecoder().decode(encryptedText);
        
        // Step 2: IV 추출 (처음 12바이트)
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(encryptedData, 0, iv, 0, iv.length);
        
        // Step 3: 암호문 추출 (IV 이후)
        byte[] cipherText = new byte[encryptedData.length - GCM_IV_LENGTH];
        System.arraycopy(encryptedData, GCM_IV_LENGTH, cipherText, 0, cipherText.length);
        
        // Step 4: Cipher 초기화
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
        
        // Step 5: 복호화 수행 (GCM이 자동으로 인증 태그 검증)
        byte[] plainText = cipher.doFinal(cipherText);
        
        return new String(plainText, "UTF-8");
    }
    
    /**
     * SecretKey를 Base64 문자열로 변환 (DB 저장용)
     * 
     * @param key 암호화 키
     * @return Base64 인코딩된 키 문자열
     */
    public static String keyToString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    
    /**
     * Base64 문자열을 SecretKey로 변환 (DB 로드용)
     * 
     * @param keyString Base64 인코딩된 키 문자열
     * @return SecretKey 객체
     */
    public static SecretKey stringToKey(String keyString) {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
    
    /**
     * 키 길이 확인 (유효성 검증용)
     * 
     * @param key 확인할 키
     * @return 키의 길이 (비트)
     */
    public static int getKeySize(SecretKey key) {
        return key.getEncoded().length * 8; // 바이트를 비트로 변환
    }
}
