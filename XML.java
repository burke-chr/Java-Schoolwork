import tester.*;

// represents a list of documents
interface ILoXMLFrag {
  
}

// represents an empty list of documents
class MtLoXMLFrag implements ILoXMLFrag {
  
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
}

// represents a document
interface IXMLFrag {
  
}

// represents a document with just plaintext
class Plaintext implements IXMLFrag {
  String txt;
  
  Plaintext(String txt) {
    this.txt = txt;
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
}

// represents a Tag with a name and a List of Attributes
class Tag {
  String name;
  ILoAtt atts;
  
  Tag(String name, ILoAtt atts) {
    this.name = name;
    this.atts = atts;
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
  
  // examples of XML
  ILoXMLFrag first = new ConsLoXMLFrag(new Plaintext("I"),     // "I am XML!"
      new ConsLoXMLFrag(new Plaintext("am"), 
          new ConsLoXMLFrag(new Plaintext("XML"),
              new ConsLoXMLFrag(new Plaintext("!"), 
                  new MtLoXMLFrag()))));
  ILoXMLFrag second = new ConsLoXMLFrag(new Plaintext("I"),   // "I am <yell>XML</yell>!"
      new ConsLoXMLFrag(new Plaintext("am"), 
          new ConsLoXMLFrag(new Tagged(this.yellTag, new ConsLoXMLFrag(new Plaintext("XML"), new MtLoXMLFrag())), 
              new ConsLoXMLFrag(new Plaintext("!"), 
                  new MtLoXMLFrag()))));
  ILoXMLFrag third = new ConsLoXMLFrag(new Plaintext("I"),    // "I am <yell><italic>X</italic>ML</yell>!"
      new ConsLoXMLFrag(new Plaintext("am"),
          new ConsLoXMLFrag(new Tagged(this.yellTag, 
              new ConsLoXMLFrag(new Tagged(this.italicTag, 
                  new ConsLoXMLFrag(new Plaintext("X"), new MtLoXMLFrag())), 
                  new ConsLoXMLFrag(new Plaintext("ML"), new MtLoXMLFrag()))), 
              new ConsLoXMLFrag(new Plaintext("!"), new MtLoXMLFrag()))));  
  ILoXMLFrag fourth = new ConsLoXMLFrag(new Plaintext("I"),   // "I am <yell volume="30db"><italic>X</italic>ML</yell>!"
      new ConsLoXMLFrag(new Plaintext("am"),
          new ConsLoXMLFrag(new Tagged(this.yell2Tag,
              new ConsLoXMLFrag(new Tagged(this.italicTag,
                  new ConsLoXMLFrag(new Plaintext("X"), new MtLoXMLFrag())),
                  new ConsLoXMLFrag(new Plaintext("ML"), new MtLoXMLFrag()))), 
              new ConsLoXMLFrag(new Plaintext("!"), new MtLoXMLFrag()))));
  ILoXMLFrag fifth = new ConsLoXMLFrag(new Plaintext("I"),   // "I am <yell volume="30db" duration="5sec"><italic>X</italic>ML</yell>!"
      new ConsLoXMLFrag(new Plaintext("am"),
          new ConsLoXMLFrag(new Tagged(this.yell3Tag,
              new ConsLoXMLFrag(new Tagged(this.italicTag,
                  new ConsLoXMLFrag(new Plaintext("X"), new MtLoXMLFrag())), 
                  new ConsLoXMLFrag(new Plaintext("ML"), new MtLoXMLFrag()))),
              new ConsLoXMLFrag(new Plaintext("!"), new MtLoXMLFrag()))));
  
  
}