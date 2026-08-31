package javautils.repo;

import java.io.Serializable;

public final class TestNode implements Serializable {
        public byte theByte = (byte) 0x01;
        public short theShort = (short) 0x2345;
        public int theInt = (int) 0x6789ABCD;
        public long theLong = (long) 0xEF0123456789ABCDL;
        public float theFloat = (float) 1.23456789;
        public double theDouble = (double) 9.87654321D;
        public char theChar = 'a';
        public boolean theBoolean = true;
        public String theString = "Hello World";
        public TestNode theChild;

        public TestNode(final TestNode theChild) {
            this.theChild = theChild;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append("\ttheByte: ").append(theByte).append("\n");
            sb.append("\ttheShort: ").append(theShort).append("\n");
            sb.append("\ttheInt: ").append(theInt).append("\n");
            sb.append("\ttheLong: ").append(theLong).append("\n");
            sb.append("\ttheFloat: ").append(theFloat).append("f\n");
            sb.append("\ttheDouble: ").append(theDouble).append("d\n");
            sb.append("\ttheChar: '").append(theChar).append("'\n");
            sb.append("\ttheBoolean: ").append(theBoolean).append("\n");
            sb.append("\ttheString: \"").append(theString).append("\"\n");
            sb.append("\ttheChild: ");
            if (theChild==null) {
                sb.append("null\n");
            } else {
                final String[] lines = theChild.toString().split("\n");
                for(int i=0; i<lines.length; i++) {
                    sb.append("\t").append(lines[i]).append("\n");
                }
            }
            sb.append("}\n");
            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TestNode)) {
                return false;
            } else {
                final TestNode t = (TestNode) o;
                return  (   (theByte == t.theByte)
                        &&  (theShort == t.theShort)
                        &&  (theInt == t.theInt)
                        &&  (theLong == t.theLong)
                        &&  (theFloat == t.theFloat)
                        &&  (theDouble == t.theDouble)
                        &&  (theChar == t.theChar)
                        &&  (theBoolean == t.theBoolean)
                        &&  (theString.equals(t.theString))
                        &&  (theChild == null ? t.theChild == null : theChild.equals(t.theChild))
                        );
            }
        }
    }