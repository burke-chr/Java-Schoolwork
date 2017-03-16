import tester.*;

// represents a list of documents
interface ILoXMLFrag {
  // computes the length (number of characters) in an 
  // XML document. Tags and attributes have no contribution
  // to this length
  int contentLength();
  // does this XML document have a tag with the given name?
  boolean hasTag(String that);
  // does this XML document have an Attribute with the same name
  // as the given String?
  boolean hasAttribute(String that);
  
}

// represents an empty list of documents
class MtLoXMLFrag implements ILoXMLFrag {
  // computes the length (number of characters) in an 
  // XML document. Tags and attributes have no contribution
  // to this length
  public int contentLength() {
    return 0;
  }
  // does this XML document have a tag with the given name?
  public boolean hasTag(String that){
    return false;
  }
  // does this XML document have an Attribute with the same name
  // as the given String?
  public boolean hasAttribute(String that) {
    return false;
  }
}

// represents a list of documents with at least one XMLFrag
class ConsLoXMLFrag implements ILoXMLFrag {
  IXMLFrag first;
  ILoXMLFrag document;
  
  // the constructor
  ConsLoXMLFrag(IXMLFrag first, ILoXMLFrag document) {
    this.first = first;
    this.document = document;
  }
  // computes the length (number of characters) in an 
  // XML document. Tags and attributes have no contribution
  // to this length
  public int contentLength() {
    return this.first.contentLengthHelp() + this.document.contentLength();
  }
  // does this XML document have a tag with the given name?
  public boolean hasTag(String that){
    return this.first.hasTagHelpOne(that) || this.document.hasTag(that);
  }
  // does this XML document have an Attribute with the same name
  // as the given String?
  public boolean hasAttribute(String that) {
    return this.first.hasAttributeHelps(that) || this.document.hasAttribute(that);
  }
}

// represents a document
interface IXMLFrag {
  // determines the length of the IXMLFrag, but
  // does not include Tags or Attributes
  int contentLengthHelp();
  // does this IXMLFrag have a Tag with the same
  // name as the string given?
  boolean hasTagHelpOne(String that);
  // does this IXMLFrag have an Attribute with the same
  // Attribute as the one with the given name?
  boolean hasAttributeHelps(String that);
  
}

// represents a document with just plaintext
class Plaintext implements IXMLFrag {
  String txt;
  
  Plaintext(String txt) {
    this.txt = txt;
  }
  
  // determines the length of the IXMLFrag, but
  // does not include Tags or Attributes
  public int contentLengthHelp() {
    return this.txt.length();
  }
  // does this IXMLFrag have a Tag with the same
  // name as the string given?
  public boolean hasTagHelpOne(String that) {
    return false;
  }
  // does this IXMLFrag have an Attribute with the same
  // Attribute as the one with the given name?
  public boolean hasAttributeHelps(String that) {
    return false;
  }
}

// represents a document with at least one Tag
class Tagged implements IXMLFrag {
  Tag tag;
  ILoXMLFrag content;
  
  Tagged(Tag tag, ILoXMLFrag content) {
    this.tag = tag;
    this.content = content;
  }
  
  // determines the length of the IXMLFrag, but
  // does not include Tags or Attributes
  public int contentLengthHelp() {
    return this.content.contentLength();
  }
  // does this IXMLFrag have a Tag with the same
  // name as the string given?
  public boolean hasTagHelpOne(String that) {
    return this.tag.hasTagHelpTwo(that) || this.content.hasTag(that);
  }
  // does this IXMLFrag have an Attribute with the same
  // Attribute as the one with the given name?
  public boolean hasAttributeHelps(String that) {
    return this.tag.hasAttributeAssist(that) || this.content.hasAttribute(that);
  }
}

// represents a Tag with a name and a List of Attributes
class Tag {
  String name;
  ILoAtt atts;
  
  Tag(String name, ILoAtt atts) {
    this.name = name;
    this.atts = atts;
  }
  
  // does this Tag have the same name as the string given?
  boolean hasTagHelpTwo(String that) {
    return this.name.equals(that);
  }
  // does this Tag have an Attribute with the same name as
  // the given name?
  boolean hasAttributeAssist(String that) {
    return this.atts.hasAttributeHelp(that);
  }
}

// represents a list of Attributes
interface ILoAtt {
  // does this Attribute have the same name as the string given?
  boolean hasAttributeHelp(String that);
}

// represents an empty list of Attributes 
class MtLoAtt implements ILoAtt {
  // does this Attribute have the same name as the string given?
  public boolean hasAttributeHelp(String that) {
    return false;
  }
}

// represents a list of Attributes with at least one Attribute
class ConsLoAtt implements ILoAtt {
  Att att;
  ILoAtt rest;
  
  ConsLoAtt(Att att, ILoAtt rest) {
    this.att = att;
    this.rest = rest;
  }
  
  // does this Attribute have the same name as the string given?
  public boolean hasAttributeHelp(String that) {
    return this.att.hasAttributeHelper(that) || this.rest.hasAttributeHelp(that);
  }
}

// represents an Attribute
class Att {
  String name;
  String value;
  
  Att(String name, String value) {
    this.name = name;
    this.value = value;
  }
  
  // does this Attribute have the same name as the string given?
  boolean hasAttributeHelper(String that) {
    return this.name.equals(that);
  }
}



// examples of XML
class ExamplesXML {
  // examples of Attributes
  Att volumeAtt = new Att("volume", "30db");
  Att durationAtt = new Att("duration", "5sec");

  // examples of Tags
  Tag yellTag = new Tag("yell", new MtLoAtt());                  // <yell>...</yell>
  Tag italicTag = new Tag("italic", new MtLoAtt());              // <italic>...</italic>
  Tag yell2Tag = new Tag("yell", new ConsLoAtt(this.volumeAtt,   // <yell volume="30db">...</yell>
      new MtLoAtt()));
  Tag yell3Tag = new Tag("yell", new ConsLoAtt(this.volumeAtt,   // <yell volume="30db" duration="5sec">...</yell>
      new ConsLoAtt(this.durationAtt, 
          new MtLoAtt())));
  
  // examples of IXMLFrag
  IXMLFrag plaintext = new Plaintext("I am XML!");
  IXMLFrag tagged = new Tagged(this.italicTag, 
      new MtLoXMLFrag());
  IXMLFrag complex = new Tagged(this.yell2Tag, 
      new ConsLoXMLFrag(new Plaintext("I am XML!"), 
          new MtLoXMLFrag()));
  
  // examples of XML
  ILoXMLFrag empty = new MtLoXMLFrag();                        // empty ILoXMLFrag
  ILoXMLFrag xml1 = new ConsLoXMLFrag(new Plaintext("I "),     // "I am XML!"
      new ConsLoXMLFrag(new Plaintext("am "), 
          new ConsLoXMLFrag(new Plaintext("XML"),
              new ConsLoXMLFrag(new Plaintext("!"), 
                  new MtLoXMLFrag()))));
  ILoXMLFrag xml2 = new ConsLoXMLFrag(new Plaintext("I "),    // "I am <yell>XML</yell>!"
      new ConsLoXMLFrag(new Plaintext("am "), 
          new ConsLoXMLFrag(new Tagged(this.yellTag, new ConsLoXMLFrag(new Plaintext("XML"), new MtLoXMLFrag())), 
              new ConsLoXMLFrag(new Plaintext("!"), 
                  new MtLoXMLFrag()))));
  ILoXMLFrag xml3 = new ConsLoXMLFrag(new Plaintext("I "),    // "I am <yell><italic>X</italic>ML</yell>!"
      new ConsLoXMLFrag(new Plaintext("am "),
          new ConsLoXMLFrag(new Tagged(this.yellTag, 
              new ConsLoXMLFrag(new Tagged(this.italicTag, 
                  new ConsLoXMLFrag(new Plaintext("X"), new MtLoXMLFrag())), 
                  new ConsLoXMLFrag(new Plaintext("ML"), new MtLoXMLFrag()))), 
              new ConsLoXMLFrag(new Plaintext("!"), new MtLoXMLFrag()))));  
  ILoXMLFrag xml4 = new ConsLoXMLFrag(new Plaintext("I "),   // "I am <yell volume="30db"><italic>X</italic>ML</yell>!"
      new ConsLoXMLFrag(new Plaintext("am "),
          new ConsLoXMLFrag(new Tagged(this.yell2Tag,
              new ConsLoXMLFrag(new Tagged(this.italicTag,
                  new ConsLoXMLFrag(new Plaintext("X"), new MtLoXMLFrag())),
                  new ConsLoXMLFrag(new Plaintext("ML"), new MtLoXMLFrag()))), 
              new ConsLoXMLFrag(new Plaintext("!"), new MtLoXMLFrag()))));
  ILoXMLFrag xml5 = new ConsLoXMLFrag(new Plaintext("I "),   // "I am <yell volume="30db" duration="5sec"><italic>X</italic>ML</yell>!"
      new ConsLoXMLFrag(new Plaintext("am "),
          new ConsLoXMLFrag(new Tagged(this.yell3Tag,
              new ConsLoXMLFrag(new Tagged(this.italicTag,
                  new ConsLoXMLFrag(new Plaintext("X"), new MtLoXMLFrag())), 
                  new ConsLoXMLFrag(new Plaintext("ML"), new MtLoXMLFrag()))),
              new ConsLoXMLFrag(new Plaintext("!"), new MtLoXMLFrag()))));
  
  
  // tests the contentLengthHelp() method, a helper for contentLength()
  void testContentLengthHelp(Tester t) {
    t.checkExpect(this.plaintext.contentLengthHelp(), 9);
    t.checkExpect(this.tagged.contentLengthHelp(), 0);
    t.checkExpect(this.complex.contentLengthHelp(), 9);
    t.checkExpect(this.plaintext.contentLengthHelp() == this.complex.contentLengthHelp(), true);
  }
  
  // tests the contentLength() method
  void testContentLength(Tester t) {
    t.checkExpect(this.empty.contentLength(), 0);
    t.checkExpect(this.xml1.contentLength(), 9);
    t.checkExpect(this.xml2.contentLength(), 9);
    t.checkExpect(this.xml3.contentLength(), 9);
    t.checkExpect(this.xml4.contentLength(), 9);
    t.checkExpect(this.xml4.contentLength(), 9);
  }
  
  // tests the hasTagHelpTwo(String) method, a helper for hasTagHelpOne(String)
  void testHasTagHelpTwo(Tester t) {
    t.checkExpect(this.italicTag.hasTagHelpTwo("italic"), true);
    t.checkExpect(this.italicTag.hasTagHelpTwo("yell"), false);
    t.checkExpect(this.italicTag.hasTagHelpTwo("hello"), false);
    t.checkExpect(this.yellTag.hasTagHelpTwo("yell"), true);
    t.checkExpect(this.yellTag.hasTagHelpTwo("italic"), false);
    t.checkExpect(this.yellTag.hasTagHelpTwo("world"), false);
    t.checkExpect(this.yell2Tag.hasTagHelpTwo("yell"), true);
    t.checkExpect(this.yell2Tag.hasTagHelpTwo("hello"), false);
    t.checkExpect(this.yell3Tag.hasTagHelpTwo("yell"), true);
    t.checkExpect(this.yell3Tag.hasTagHelpTwo("world"), false);
  }
  
  // tests the hasTagHelpOne(String) method, a helper for hasTag(String)
  void testHasTagHelp(Tester t) {
    t.checkExpect(this.plaintext.hasTagHelpOne("italic"), false);
    t.checkExpect(this.plaintext.hasTagHelpOne("yell"), false);
    t.checkExpect(this.tagged.hasTagHelpOne("italic"), true);
    t.checkExpect(this.tagged.hasTagHelpOne("yell"), false);
    t.checkExpect(this.complex.hasTagHelpOne("italic"), false);
    t.checkExpect(this.complex.hasTagHelpOne("yell"), true);
  }
  
  // tests the hasTag(String) method
  void testHasTag(Tester t) {
    t.checkExpect(this.empty.hasTag("italic"), false);
    t.checkExpect(this.empty.hasTag("yell"), false);
    t.checkExpect(this.xml1.hasTag("italic"), false);
    t.checkExpect(this.xml1.hasTag("yell"), false);
    t.checkExpect(this.xml2.hasTag("italic"), false);
    t.checkExpect(this.xml2.hasTag("yell"), true);
    t.checkExpect(this.xml3.hasTag("italic"), true);
    t.checkExpect(this.xml3.hasTag("yell"), true);
    t.checkExpect(this.xml4.hasTag("italic"), true);
    t.checkExpect(this.xml4.hasTag("yell"), true);
    t.checkExpect(this.xml5.hasTag("italic"), true);
    t.checkExpect(this.xml5.hasTag("yell"), true);
  }
  
  // tests the hasAttributeHelper(String) method, a helper for hasAttributeHelp(String)
  void testHasAttributeHelper(Tester t) {
    t.checkExpect(this.volumeAtt.hasAttributeHelper("volume"), true);
    t.checkExpect(this.volumeAtt.hasAttributeHelper("duration"), false);
    t.checkExpect(this.durationAtt.hasAttributeHelper("duration"), true);
    t.checkExpect(this.durationAtt.hasAttributeHelper("volume"), false);
  }
  

  
  
  
}