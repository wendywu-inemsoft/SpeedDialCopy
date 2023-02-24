
/**
 * <p>Title: </p>
 *
 * <p>Description: A very secure text based encryption algorithm.</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
/**
 * @author Jeff Genender
 **/
public class CipherTxtBased
{
  private static final byte[] _3desData=
    {
    (byte)0x55,(byte)0x65,(byte)0x61,(byte)0x13,(byte)0x34,(byte)0x82,
    (byte)0x0D,(byte)0x4A,(byte)0xA3,(byte)0x90,(byte)0x55,(byte)0xFE,
    (byte)0x12,(byte)0x17,(byte)0xAC,(byte)0x77,(byte)0x39,(byte)0x19,
    (byte)0x76,(byte)0x6F,(byte)0xBA,(byte)0x39,(byte)0x31,(byte)0x2F};
  private static SecretKeySpec _key=new SecretKeySpec(_3desData,"DESede");
  public static String encrypt(String text)
  {
    byte[] plaintext=text.getBytes();
    try
    {
      // Get a 3DES Cipher object
      Cipher cipher=Cipher.getInstance("DESede"); // Triple-DES encryption
      // Set it into encryption mode
      cipher.init(Cipher.ENCRYPT_MODE,_key);
      // Encrypt data
      byte[] cipherText=cipher.doFinal(plaintext);
      BASE64Encoder b64=new BASE64Encoder();
      return b64.encode(cipherText);
    }
    catch(Exception e)
    {
      throw new java.lang.RuntimeException(e);
    }
  }
  public static String decrypt(String text)
  {
    try
    {
      BASE64Decoder b64=new BASE64Decoder();
      byte[] cipherText=b64.decodeBuffer(text);
      // Get a 3DES Cipher object
      Cipher cipher=Cipher.getInstance("DESede"); // Triple-DES encryption
      // Set it into decryption mode
      cipher.init(Cipher.DECRYPT_MODE,_key);
      // Decrypt data
      String plainText=new String(cipher.doFinal(cipherText));
      return plainText;
    }
    catch(Exception e)
    {
      throw new java.lang.RuntimeException(e);
    }
  }
}
