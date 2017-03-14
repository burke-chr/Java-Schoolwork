import tester.*;

// represents a list of documents
interface ILoXMLFrag {
  // computes the length (number of characters) in an 
  // XML document. Tags and attributes have no contribution
  // to this length
  int contentLength();
  // does this XML document have a tag with the given name?
  boolean hasTag(String that);
  
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
    return this.first.hasTagHelp(that) || this.document.hasTag(that);
  }
}

// represents a document
interface IXMLFrag {
  // determines the length of the IXMLFrag, but
  // does not include Tags or Attributes
  int contentLengthHelp();
  // does this IXMLFrag have a Tag with the same
  // name as the string given?
  boolean hasTagHelp(String that);
  
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
  public boolean hasTagHelp(String that) {
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
  public boolean hasTagHelp(String that) {
    return this.tag.hasTagAssist(that) || this.content.hasTag(that);
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
  boolean hasTagAssist(String that) {
    return this.name.equals(that);
  }
}

// represents a list of Attributes
interface ILoAtt {
  
}

// represents an empty list of Attributes 
class MtLoAtt implements ILoAtt {
  
}

// represents a list of Attributes with at least one Attribute
class ConsLoAtt implements ILoAtt {
  Att att;
  ILoAtt rest;
  
  ConsLoAtt(Att att, ILoAtt rest) {
    this.att = att;
    this.rest = rest;
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
  
  // tests the hasTagAssist(String) method, a helper for hasTagHelp(String)
  void testHasTagAssist(Tester t) {
    t.checkExpect(this.italicTag.hasTagAssist("italic"), true);
    t.checkExpect(this.italicTag.hasTagAssist("yell"), false);
    t.checkExpect(this.italicTag.hasTagAssist("hello"), false);
    t.checkExpect(this.yellTag.hasTagAssist("yell"), true);
    t.checkExpect(this.yellTag.hasTagAssist("italic"), false);
    t.checkExpect(this.yellTag.hasTagAssist("world"), false);
    t.checkExpect(this.yell2Tag.hasTagAssist("yell"), true);
    t.checkExpect(this.yell2Tag.hasTagAssist("hello"), false);
    t.checkExpect(this.yell3Tag.hasTagAssist("yell"), true);
    t.checkExpect(this.yell3Tag.hasTagAssist("world"), false);
  }
  
  // tests the hasTagHelp(String) method, a helper for hasTag(String)
  void testHasTagHelp(Tester t) {
    t.checkExpect(this.plaintext.hasTagHelp("italic"), false);
    t.checkExpect(this.plaintext.hasTagHelp("yell"), false);
    t.checkExpect(this.tagged.hasTagHelp("italic"), true);
    t.checkExpect(this.tagged.hasTagHelp("yell"), false);
    t.checkExpect(this.complex.hasTagHelp("italic"), false);
    t.checkExpect(this.complex.hasTagHelp("yell"), true);
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
  
  
  
}