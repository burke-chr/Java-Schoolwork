import java.util.*;
import tester.*;

/**
 * A class that defines a new permutation code, as well as methods for encoding
 * and decoding of the messages that use this code.
 */
public class PermutationCode {
  
  // The original list of characters to be encoded
  ArrayList<Character> alphabet = 
      new ArrayList<Character>(Arrays.asList(
          'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
          'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 
          't', 'u', 'v', 'w', 'x', 'y', 'z'));

  // encoding code
  ArrayList<Character> code = new ArrayList<Character>(26);

  // A random number generator
  Random rand = new Random();

  // Create a new instance of the encoder/decoder with a new permutation code 
  PermutationCode() {
    this.code = this.initEncoder();
  }

  // Create a new instance of the encoder/decoder with the given code 
  PermutationCode(ArrayList<Character> code) {
    this.code = code;
  }
  
  /*TEMPLATE:
   * Fields:
   *  ... this.alphabet ...      -- ArrayList<Character>
   *  ... this.code ...          -- ArrayList<Character>
   *  ... this.rand ...          -- ArrayList<Character>
   *  
   * Methods:
   *  ... this.initEncoder() ...       -- ArrayList<Character>
   *  ... this.encode(String) ...      -- String
   *  ... this.decode(String) ...      -- String
   *  
   * 
   */

  // Initialize the encoding permutation of the characters
  // method template: same as class template
  ArrayList<Character> initEncoder() {
    ArrayList<Character> alphabet2 = 
        new ArrayList<Character>(Arrays.asList(
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 
            't', 'u', 'v', 'w', 'x', 'y', 'z'));
    
    while (alphabet2.size() > 0) {
      int n = rand.nextInt(alphabet2.size());
      this.code.add(alphabet2.get(n));
      alphabet2.remove(n);
    }
    return this.code;
  }

  // produce an encoded String from the given String
  // method template: same as class template
  String encode(String source) {
    ArrayList<String> alphabet3 = 
        new ArrayList<String>(Arrays.asList(
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", 
            "k", "l", "m", "n", "o", "p", "q", "r", "s", 
            "t", "u", "v", "w", "x", "y", "z"));
    if (source.length() == 0) {
      return "";
    }
    else {
      String s = source.substring(0, 1);
      return s.replace(s, this.code.get(alphabet3.indexOf(s)).toString())
          + this.encode(source.substring(1, source.length()));
    }

  }

  // produce a decoded String from the given String
  // method template: same as class template
  String decode(String code) {
    ArrayList<String> alphabet4 = 
        new ArrayList<String>(Arrays.asList(
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", 
            "k", "l", "m", "n", "o", "p", "q", "r", "s", 
            "t", "u", "v", "w", "x", "y", "z")); 
    if (code.length() == 0) {
      return "";
    }
    else {
      String s = code.substring(0, 1);
      return s.replace(s, alphabet4.get(this.code.indexOf(s.charAt(0))))
          + this.decode(code.substring(1, code.length()));
    }
  }
}

class ExamplesPermutation {

  PermutationCode p1 = new PermutationCode(
      new ArrayList<Character>(Arrays.asList(
          'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
          'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 
          't', 'u', 'v', 'w', 'x', 'y', 'z', 'a')));  

  // tests the encode method
  void testEncode(Tester t) {
    t.checkExpect(this.p1.encode(""), "");
    t.checkExpect(this.p1.encode("cat"), "dbu");
    t.checkExpect(this.p1.encode("house"), "ipvtf");
    t.checkExpect(this.p1.encode("aaaa"), "bbbb");
  }
  
  // tests the decode method
  void testDecode(Tester t) {
    t.checkExpect(this.p1.decode(""), "");
    t.checkExpect(this.p1.decode("dbu"), "cat");
    t.checkExpect(this.p1.decode("ipvtf"), "house");
    t.checkExpect(this.p1.decode("bbbb"), "aaaa");
  }
  
  // tests the initEncoder method
  void testInitEncoder(Tester t) {
    t.checkExpect(new PermutationCode(new ArrayList<Character>(26)).code.size(), 0);
    t.checkExpect(new PermutationCode(new ArrayList<Character>(26)).initEncoder().size(), 26);
  }
}
