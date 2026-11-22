package com.leadway.service;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Arrays;
@Service
public class PasswordHashService {
 private static final int ITERATIONS=210000;
 public String hash(String password) {
  byte[] salt=new byte[12];new SecureRandom().nextBytes(salt);
  byte[] digest=derive(password,salt);byte[] all=new byte[32];
  System.arraycopy(salt,0,all,0,12);System.arraycopy(digest,0,all,12,20);
  return Base64.getUrlEncoder().withoutPadding().encodeToString(all);
 }
 public boolean verify(String password,String encoded){
  try {byte[] all=Base64.getUrlDecoder().decode(encoded);if(all.length!=32)return false;
   byte[] salt=Arrays.copyOfRange(all,0,12);byte[] digest=Arrays.copyOfRange(all,12,32);
   return MessageDigest.isEqual(digest,derive(password,salt));
  }catch(IllegalArgumentException ex){return false;}
 }
 private byte[] derive(String password,byte[] salt){
  PBEKeySpec spec=new PBEKeySpec(password.toCharArray(),salt,ITERATIONS,160);
  try {return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();}
  catch(Exception e){throw new IllegalStateException("Falha na criptografia",e);}finally{spec.clearPassword();}
 }
}
