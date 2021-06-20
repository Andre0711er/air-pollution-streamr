package com.marthalerit.helper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class StringHelper {
  public final static int ALGO_SHA1 = 1;

  public final static int ALGO_MD5 = 2;

  public final static int ALGO_SHA256 = 3;

  /**
   * return hash from string
   *
   * @param word String to get hash from
   * @return
   * @throws NoSuchAlgorithmException
   */
  public static String getHash(final String word) throws NoSuchAlgorithmException {
    return getHash(word.getBytes(StandardCharsets.UTF_8), ALGO_SHA1);
  }

  /**
   * return hash from bytes
   *
   * @param data      byte-array to hash
   * @param algorithm Hash Type. MD5, SHA-1, ...
   * @return hashed string
   * @throws NoSuchAlgorithmException
   */
  private static String getHash(final byte[] data, final int algorithm) throws NoSuchAlgorithmException {
    final String type = switch (algorithm) {
      case ALGO_SHA1 -> "SHA-1";
      case ALGO_MD5 -> "MD5";
      default -> "SHA-256";
    };

    MessageDigest crypt = MessageDigest.getInstance(type);
    crypt.reset();
    crypt.update(data);

    return byteToHex(crypt.digest());
  }

  private static String byteToHex(final byte[] hash) {
    Formatter formatter = new Formatter();
    for (byte b : hash) {
      formatter.format("%02x", b);
    }
    String result = formatter.toString();
    formatter.close();
    return result;
  }
}
