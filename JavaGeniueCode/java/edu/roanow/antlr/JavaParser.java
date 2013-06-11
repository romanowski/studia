// $ANTLR 3.0.1 /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g 2012-04-30 21:05:01

   package edu.romanow.antlr;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

/** A Java 1.5 grammar for ANTLR v3 derived from the spec
 *
 *  This is a very close representation of the spec; the changes
 *  are comestic (remove left recursion) and also fixes (the spec
 *  isn't exactly perfect).  I have run this on the 1.4.2 source
 *  and some nasty looking enums from 1.5, but have not really
 *  tested for 1.5 compatibility.
 *
 *  I built this with: java -Xmx100M org.antlr.Tool java.g
 *  and got two errors that are ok (for now):
 *  java.g:691:9: Decision can match input such as
 *    "'0'..'9'{'E', 'e'}{'+', '-'}'0'..'9'{'D', 'F', 'd', 'f'}"
 *    using multiple alternatives: 3, 4
 *  As a result, alternative(s) 4 were disabled for that input
 *  java.g:734:35: Decision can match input such as "{'$', 'A'..'Z',
 *    '_', 'a'..'z', '\u00C0'..'\u00D6', '\u00D8'..'\u00F6',
 *    '\u00F8'..'\u1FFF', '\u3040'..'\u318F', '\u3300'..'\u337F',
 *    '\u3400'..'\u3D2D', '\u4E00'..'\u9FFF', '\uF900'..'\uFAFF'}"
 *    using multiple alternatives: 1, 2
 *  As a result, alternative(s) 2 were disabled for that input
 *
 *  You can turn enum on/off as a keyword :)
 *
 *  Version 1.0 -- initial release July 5, 2006 (requires 3.0b2 or higher)
 *
 *  Primary author: Terence Parr, July 2006
 *
 *  Version 1.0.1 -- corrections by Koen Vanderkimpen & Marko van Dooren,
 *      October 25, 2006;
 *      fixed normalInterfaceDeclaration: now uses typeParameters instead
 *          of typeParameter (according to JLS, 3rd edition)
 *      fixed castExpression: no longer allows expression next to type
 *          (according to semantics in JLS, in contrast with syntax in JLS)
 *
 *  Version 1.0.2 -- Terence Parr, Nov 27, 2006
 *      java spec I built this from had some bizarre for-loop control.
 *          Looked weird and so I looked elsewhere...Yep, it's messed up.
 *          simplified.
 *
 *  Version 1.0.3 -- Chris Hogue, Feb 26, 2007
 *      Factored out an annotationName rule and used it in the annotation rule.
 *          Not sure why, but typeName wasn't recognizing references to inner
 *          annotations (e.g. @InterfaceName.InnerAnnotation())
 *      Factored out the elementValue section of an annotation reference.  Created
 *          elementValuePair and elementValuePairs rules, then used them in the
 *          annotation rule.  Allows it to recognize annotation references with
 *          multiple, comma separated attributes.
 *      Updated elementValueArrayInitializer so that it allows multiple elements.
 *          (It was only allowing 0 or 1 element).
 *      Updated localVariableDeclaration to allow annotations.  Interestingly the JLS
 *          doesn't appear to indicate this is legal, but it does work as of at least
 *          JDK 1.5.0_06.
 *      Moved the Identifier portion of annotationTypeElementRest to annotationMethodRest.
 *          Because annotationConstantRest already references variableDeclarator which
 *          has the Identifier portion in it, the parser would fail on constants in
 *          annotation definitions because it expected two identifiers.
 *      Added optional trailing ';' to the alternatives in annotationTypeElementRest.
 *          Wouldn't handle an inner interface that has a trailing ';'.
 *      Swapped the expression and type rule reference order in castExpression to
 *          make it check for genericized casts first.  It was failing to recognize a
 *          statement like  "Class<Byte> TYPE = (Class<Byte>)...;" because it was seeing
 *          'Class<Byte' in the cast expression as a less than expression, then failing
 *          on the '>'.
 *      Changed createdName to use typeArguments instead of nonWildcardTypeArguments.
 *         
 *      Changed the 'this' alternative in primary to allow 'identifierSuffix' rather than
 *          just 'arguments'.  The case it couldn't handle was a call to an explicit
 *          generic method invocation (e.g. this.<E>doSomething()).  Using identifierSuffix
 *          may be overly aggressive--perhaps should create a more constrained thisSuffix rule?
 *
 *  Version 1.0.4 -- Hiroaki Nakamura, May 3, 2007
 *
 *  Fixed formalParameterDecls, localVariableDeclaration, forInit,
 *  and forVarControl to use variableModifier* not 'final'? (annotation)?
 *
 *  Version 1.0.5 -- Terence, June 21, 2007
 *  --a[i].foo didn't work. Fixed unaryExpression
 *
 *  Version 1.0.6 -- John Ridgway, March 17, 2008
 *      Made "assert" a switchable keyword like "enum".
 *      Fixed compilationUnit to disallow "annotation importDeclaration ...".
 *      Changed "Identifier ('.' Identifier)*" to "qualifiedName" in more
 *          places.
 *      Changed modifier* and/or variableModifier* to classOrInterfaceModifiers,
 *          modifiers or variableModifiers, as appropriate.
 *      Renamed "bound" to "typeBound" to better match language in the JLS.
 *      Added "memberDeclaration" which rewrites to methodDeclaration or
 *      fieldDeclaration and pulled type into memberDeclaration.  So we parse
 *          type and then move on to decide whether we're dealing with a field
 *          or a method.
 *      Modified "constructorDeclaration" to use "constructorBody" instead of
 *          "methodBody".  constructorBody starts with explicitConstructorInvocation,
 *          then goes on to blockStatement*.  Pulling explicitConstructorInvocation
 *          out of expressions allowed me to simplify "primary".
 *      Changed variableDeclarator to simplify it.
 *      Changed type to use classOrInterfaceType, thus simplifying it; of course
 *          I then had to add classOrInterfaceType, but it is used in several
 *          places.
 *      Fixed annotations, old version allowed "@X(y,z)", which is illegal.
 *      Added optional comma to end of "elementValueArrayInitializer"; as per JLS.
 *      Changed annotationTypeElementRest to use normalClassDeclaration and
 *          normalInterfaceDeclaration rather than classDeclaration and
 *          interfaceDeclaration, thus getting rid of a couple of grammar ambiguities.
 *      Split localVariableDeclaration into localVariableDeclarationStatement
 *          (includes the terminating semi-colon) and localVariableDeclaration.
 *          This allowed me to use localVariableDeclaration in "forInit" clauses,
 *           simplifying them.
 *      Changed switchBlockStatementGroup to use multiple labels.  This adds an
 *          ambiguity, but if one uses appropriately greedy parsing it yields the
 *           parse that is closest to the meaning of the switch statement.
 *      Renamed "forVarControl" to "enhancedForControl" -- JLS language.
 *      Added semantic predicates to test for shift operations rather than other
 *          things.  Thus, for instance, the string "< <" will never be treated
 *          as a left-shift operator.
 *      In "creator" we rule out "nonWildcardTypeArguments" on arrayCreation,
 *          which are illegal.
 *      Moved "nonWildcardTypeArguments into innerCreator.
 *      Removed 'super' superSuffix from explicitGenericInvocation, since that
 *          is only used in explicitConstructorInvocation at the beginning of a
 *           constructorBody.  (This is part of the simplification of expressions
 *           mentioned earlier.)
 *      Simplified primary (got rid of those things that are only used in
 *          explicitConstructorInvocation).
 *      Lexer -- removed "Exponent?" from FloatingPointLiteral choice 4, since it
 *          led to an ambiguity.
 *
 *      This grammar successfully parses every .java file in the JDK 1.5 source
 *          tree (excluding those whose file names include '-', which are not
 *          valid Java compilation units).
 *
 *  Known remaining problems:
 *      "Letter" and "JavaIDDigit" are wrong.  The actual specification of
 *      "Letter" should be "a character for which the method
 *      Character.isJavaIdentifierStart(int) returns true."  A "Java
 *      letter-or-digit is a character for which the method
 *      Character.isJavaIdentifierPart(int) returns true."
 */
public class JavaParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "IDENTIFIER", "INTLITERAL", "LONGLITERAL", "FLOATLITERAL", "DOUBLELITERAL", "CHARLITERAL", "STRINGLITERAL", "TRUE", "FALSE", "NULL", "IntegerNumber", "LongSuffix", "HexPrefix", "HexDigit", "Exponent", "NonIntegerNumber", "FloatSuffix", "DoubleSuffix", "EscapeSequence", "WS", "COMMENT", "LINE_COMMENT", "ABSTRACT", "ASSERT", "BOOLEAN", "BREAK", "BYTE", "CASE", "CATCH", "CHAR", "CLASS", "CONST", "CONTINUE", "DEFAULT", "DO", "DOUBLE", "ELSE", "ENUM", "EXTENDS", "FINAL", "FINALLY", "FLOAT", "FOR", "GOTO", "IF", "IMPLEMENTS", "IMPORT", "INSTANCEOF", "INT", "INTERFACE", "LONG", "NATIVE", "NEW", "PACKAGE", "PRIVATE", "PROTECTED", "PUBLIC", "RETURN", "SHORT", "STATIC", "STRICTFP", "SUPER", "SWITCH", "SYNCHRONIZED", "THIS", "THROW", "THROWS", "TRANSIENT", "TRY", "VOID", "VOLATILE", "WHILE", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "LBRACKET", "RBRACKET", "SEMI", "COMMA", "DOT", "ELLIPSIS", "EQ", "BANG", "TILDE", "QUES", "COLON", "EQEQ", "AMPAMP", "BARBAR", "PLUSPLUS", "SUBSUB", "PLUS", "SUB", "STAR", "SLASH", "AMP", "BAR", "CARET", "PERCENT", "PLUSEQ", "SUBEQ", "STAREQ", "SLASHEQ", "AMPEQ", "BAREQ", "CARETEQ", "PERCENTEQ", "MONKEYS_AT", "BANGEQ", "GT", "LT", "IdentifierStart", "IdentifierPart", "SurrogateIdentifer"
    };
    public static final int PACKAGE=57;
    public static final int LT=115;
    public static final int STAR=98;
    public static final int WHILE=75;
    public static final int CONST=35;
    public static final int CASE=31;
    public static final int CHAR=33;
    public static final int NEW=56;
    public static final int DO=38;
    public static final int EOF=-1;
    public static final int BREAK=29;
    public static final int LBRACKET=80;
    public static final int FINAL=43;
    public static final int RPAREN=77;
    public static final int IMPORT=50;
    public static final int SUBSUB=95;
    public static final int STAREQ=106;
    public static final int FloatSuffix=20;
    public static final int NonIntegerNumber=19;
    public static final int CARET=102;
    public static final int RETURN=61;
    public static final int THIS=68;
    public static final int DOUBLE=39;
    public static final int MONKEYS_AT=112;
    public static final int BARBAR=93;
    public static final int VOID=73;
    public static final int SUPER=65;
    public static final int GOTO=47;
    public static final int EQ=86;
    public static final int AMPAMP=92;
    public static final int COMMENT=24;
    public static final int QUES=89;
    public static final int EQEQ=91;
    public static final int HexPrefix=16;
    public static final int RBRACE=79;
    public static final int LINE_COMMENT=25;
    public static final int PRIVATE=58;
    public static final int STATIC=63;
    public static final int SWITCH=66;
    public static final int NULL=13;
    public static final int ELSE=40;
    public static final int STRICTFP=64;
    public static final int DOUBLELITERAL=8;
    public static final int IdentifierStart=116;
    public static final int NATIVE=55;
    public static final int ELLIPSIS=85;
    public static final int THROWS=70;
    public static final int INT=52;
    public static final int SLASHEQ=107;
    public static final int INTLITERAL=5;
    public static final int ASSERT=27;
    public static final int TRY=72;
    public static final int LONGLITERAL=6;
    public static final int LongSuffix=15;
    public static final int WS=23;
    public static final int SurrogateIdentifer=118;
    public static final int CHARLITERAL=9;
    public static final int GT=114;
    public static final int CATCH=32;
    public static final int FALSE=12;
    public static final int EscapeSequence=22;
    public static final int THROW=69;
    public static final int PROTECTED=59;
    public static final int CLASS=34;
    public static final int BAREQ=109;
    public static final int IntegerNumber=14;
    public static final int AMP=100;
    public static final int PLUSPLUS=94;
    public static final int LBRACE=78;
    public static final int SUBEQ=105;
    public static final int Exponent=18;
    public static final int FOR=46;
    public static final int SUB=97;
    public static final int FLOAT=45;
    public static final int ABSTRACT=26;
    public static final int HexDigit=17;
    public static final int PLUSEQ=104;
    public static final int LPAREN=76;
    public static final int IF=48;
    public static final int SLASH=99;
    public static final int BOOLEAN=28;
    public static final int SYNCHRONIZED=67;
    public static final int IMPLEMENTS=49;
    public static final int CONTINUE=36;
    public static final int COMMA=83;
    public static final int AMPEQ=108;
    public static final int IDENTIFIER=4;
    public static final int TRANSIENT=71;
    public static final int TILDE=88;
    public static final int BANGEQ=113;
    public static final int PLUS=96;
    public static final int RBRACKET=81;
    public static final int DOT=84;
    public static final int IdentifierPart=117;
    public static final int BYTE=30;
    public static final int PERCENT=103;
    public static final int VOLATILE=74;
    public static final int DEFAULT=37;
    public static final int SHORT=62;
    public static final int BANG=87;
    public static final int INSTANCEOF=51;
    public static final int TRUE=11;
    public static final int SEMI=82;
    public static final int COLON=90;
    public static final int ENUM=41;
    public static final int PERCENTEQ=111;
    public static final int DoubleSuffix=21;
    public static final int FINALLY=44;
    public static final int STRINGLITERAL=10;
    public static final int CARETEQ=110;
    public static final int INTERFACE=53;
    public static final int LONG=54;
    public static final int EXTENDS=42;
    public static final int FLOATLITERAL=7;
    public static final int PUBLIC=60;
    public static final int BAR=101;

        public JavaParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[381+1];
         }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "/home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g"; }


    public static class compilationUnit_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start compilationUnit
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:299:1: compilationUnit : ( ( annotations )? packageDeclaration )? ( importDeclaration )* ( typeDeclaration )* ;
    public final compilationUnit_return compilationUnit() throws RecognitionException {
        compilationUnit_return retval = new compilationUnit_return();
        retval.start = input.LT(1);
        int compilationUnit_StartIndex = input.index();
        Object root_0 = null;

        annotations_return annotations1 = null;

        packageDeclaration_return packageDeclaration2 = null;

        importDeclaration_return importDeclaration3 = null;

        typeDeclaration_return typeDeclaration4 = null;



        try {
            if ( backtracking>0 && alreadyParsedRule(input, 1) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:304:5: ( ( ( annotations )? packageDeclaration )? ( importDeclaration )* ( typeDeclaration )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:304:9: ( ( annotations )? packageDeclaration )? ( importDeclaration )* ( typeDeclaration )*
            {
            root_0 = (Object)adaptor.nil();

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:304:9: ( ( annotations )? packageDeclaration )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==MONKEYS_AT) ) {
                int LA2_1 = input.LA(2);

                if ( (synpred2()) ) {
                    alt2=1;
                }
            }
            else if ( (LA2_0==PACKAGE) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:304:13: ( annotations )? packageDeclaration
                    {
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:304:13: ( annotations )?
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==MONKEYS_AT) ) {
                        alt1=1;
                    }
                    switch (alt1) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:304:14: annotations
                            {
                            pushFollow(FOLLOW_annotations_in_compilationUnit106);
                            annotations1=annotations();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, annotations1.getTree());

                            }
                            break;

                    }

                    pushFollow(FOLLOW_packageDeclaration_in_compilationUnit135);
                    packageDeclaration2=packageDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, packageDeclaration2.getTree());

                    }
                    break;

            }

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:308:9: ( importDeclaration )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==IMPORT) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:308:10: importDeclaration
            	    {
            	    pushFollow(FOLLOW_importDeclaration_in_compilationUnit157);
            	    importDeclaration3=importDeclaration();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, importDeclaration3.getTree());

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:310:9: ( typeDeclaration )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==ABSTRACT||LA4_0==CLASS||LA4_0==ENUM||LA4_0==FINAL||LA4_0==INTERFACE||LA4_0==NATIVE||(LA4_0>=PRIVATE && LA4_0<=PUBLIC)||(LA4_0>=STATIC && LA4_0<=STRICTFP)||LA4_0==SYNCHRONIZED||LA4_0==TRANSIENT||LA4_0==VOLATILE||LA4_0==SEMI||LA4_0==MONKEYS_AT) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:310:10: typeDeclaration
            	    {
            	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit179);
            	    typeDeclaration4=typeDeclaration();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, typeDeclaration4.getTree());

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 1, compilationUnit_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end compilationUnit

    public static class packageDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start packageDeclaration
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:314:1: packageDeclaration : 'package' qualifiedName ';' ;
    public final packageDeclaration_return packageDeclaration() throws RecognitionException {
        packageDeclaration_return retval = new packageDeclaration_return();
        retval.start = input.LT(1);
        int packageDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal5=null;
        Token char_literal7=null;
        qualifiedName_return qualifiedName6 = null;


        Object string_literal5_tree=null;
        Object char_literal7_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 2) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:315:5: ( 'package' qualifiedName ';' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:315:9: 'package' qualifiedName ';'
            {
            root_0 = (Object)adaptor.nil();

            string_literal5=(Token)input.LT(1);
            match(input,PACKAGE,FOLLOW_PACKAGE_in_packageDeclaration210); if (failed) return retval;
            if ( backtracking==0 ) {
            string_literal5_tree = (Object)adaptor.create(string_literal5);
            adaptor.addChild(root_0, string_literal5_tree);
            }
            pushFollow(FOLLOW_qualifiedName_in_packageDeclaration212);
            qualifiedName6=qualifiedName();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, qualifiedName6.getTree());
            char_literal7=(Token)input.LT(1);
            match(input,SEMI,FOLLOW_SEMI_in_packageDeclaration222); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal7_tree = (Object)adaptor.create(char_literal7);
            adaptor.addChild(root_0, char_literal7_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 2, packageDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end packageDeclaration

    public static class importDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start importDeclaration
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:319:1: importDeclaration : ( 'import' ( 'static' )? IDENTIFIER '.' '*' ';' | 'import' ( 'static' )? IDENTIFIER ( '.' IDENTIFIER )+ ( '.' '*' )? ';' );
    public final importDeclaration_return importDeclaration() throws RecognitionException {
        importDeclaration_return retval = new importDeclaration_return();
        retval.start = input.LT(1);
        int importDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal8=null;
        Token string_literal9=null;
        Token IDENTIFIER10=null;
        Token char_literal11=null;
        Token char_literal12=null;
        Token char_literal13=null;
        Token string_literal14=null;
        Token string_literal15=null;
        Token IDENTIFIER16=null;
        Token char_literal17=null;
        Token IDENTIFIER18=null;
        Token char_literal19=null;
        Token char_literal20=null;
        Token char_literal21=null;

        Object string_literal8_tree=null;
        Object string_literal9_tree=null;
        Object IDENTIFIER10_tree=null;
        Object char_literal11_tree=null;
        Object char_literal12_tree=null;
        Object char_literal13_tree=null;
        Object string_literal14_tree=null;
        Object string_literal15_tree=null;
        Object IDENTIFIER16_tree=null;
        Object char_literal17_tree=null;
        Object IDENTIFIER18_tree=null;
        Object char_literal19_tree=null;
        Object char_literal20_tree=null;
        Object char_literal21_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 3) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:320:5: ( 'import' ( 'static' )? IDENTIFIER '.' '*' ';' | 'import' ( 'static' )? IDENTIFIER ( '.' IDENTIFIER )+ ( '.' '*' )? ';' )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==IMPORT) ) {
                int LA9_1 = input.LA(2);

                if ( (LA9_1==STATIC) ) {
                    int LA9_2 = input.LA(3);

                    if ( (LA9_2==IDENTIFIER) ) {
                        int LA9_3 = input.LA(4);

                        if ( (LA9_3==DOT) ) {
                            int LA9_4 = input.LA(5);

                            if ( (LA9_4==IDENTIFIER) ) {
                                alt9=2;
                            }
                            else if ( (LA9_4==STAR) ) {
                                alt9=1;
                            }
                            else {
                                if (backtracking>0) {failed=true; return retval;}
                                NoViableAltException nvae =
                                    new NoViableAltException("319:1: importDeclaration : ( 'import' ( 'static' )? IDENTIFIER '.' '*' ';' | 'import' ( 'static' )? IDENTIFIER ( '.' IDENTIFIER )+ ( '.' '*' )? ';' );", 9, 4, input);

                                throw nvae;
                            }
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("319:1: importDeclaration : ( 'import' ( 'static' )? IDENTIFIER '.' '*' ';' | 'import' ( 'static' )? IDENTIFIER ( '.' IDENTIFIER )+ ( '.' '*' )? ';' );", 9, 3, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("319:1: importDeclaration : ( 'import' ( 'static' )? IDENTIFIER '.' '*' ';' | 'import' ( 'static' )? IDENTIFIER ( '.' IDENTIFIER )+ ( '.' '*' )? ';' );", 9, 2, input);

                        throw nvae;
                    }
                }
                else if ( (LA9_1==IDENTIFIER) ) {
                    int LA9_3 = input.LA(3);

                    if ( (LA9_3==DOT) ) {
                        int LA9_4 = input.LA(4);

                        if ( (LA9_4==IDENTIFIER) ) {
                            alt9=2;
                        }
                        else if ( (LA9_4==STAR) ) {
                            alt9=1;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("319:1: importDeclaration : ( 'import' ( 'static' )? IDENTIFIER '.' '*' ';' | 'import' ( 'static' )? IDENTIFIER ( '.' IDENTIFIER )+ ( '.' '*' )? ';' );", 9, 4, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("319:1: importDeclaration : ( 'import' ( 'static' )? IDENTIFIER '.' '*' ';' | 'import' ( 'static' )? IDENTIFIER ( '.' IDENTIFIER )+ ( '.' '*' )? ';' );", 9, 3, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("319:1: importDeclaration : ( 'import' ( 'static' )? IDENTIFIER '.' '*' ';' | 'import' ( 'static' )? IDENTIFIER ( '.' IDENTIFIER )+ ( '.' '*' )? ';' );", 9, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("319:1: importDeclaration : ( 'import' ( 'static' )? IDENTIFIER '.' '*' ';' | 'import' ( 'static' )? IDENTIFIER ( '.' IDENTIFIER )+ ( '.' '*' )? ';' );", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:320:9: 'import' ( 'static' )? IDENTIFIER '.' '*' ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal8=(Token)input.LT(1);
                    match(input,IMPORT,FOLLOW_IMPORT_in_importDeclaration243); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal8_tree = (Object)adaptor.create(string_literal8);
                    adaptor.addChild(root_0, string_literal8_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:321:9: ( 'static' )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==STATIC) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:321:10: 'static'
                            {
                            string_literal9=(Token)input.LT(1);
                            match(input,STATIC,FOLLOW_STATIC_in_importDeclaration255); if (failed) return retval;
                            if ( backtracking==0 ) {
                            string_literal9_tree = (Object)adaptor.create(string_literal9);
                            adaptor.addChild(root_0, string_literal9_tree);
                            }

                            }
                            break;

                    }

                    IDENTIFIER10=(Token)input.LT(1);
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_importDeclaration276); if (failed) return retval;
                    if ( backtracking==0 ) {
                    IDENTIFIER10_tree = (Object)adaptor.create(IDENTIFIER10);
                    adaptor.addChild(root_0, IDENTIFIER10_tree);
                    }
                    char_literal11=(Token)input.LT(1);
                    match(input,DOT,FOLLOW_DOT_in_importDeclaration278); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal11_tree = (Object)adaptor.create(char_literal11);
                    adaptor.addChild(root_0, char_literal11_tree);
                    }
                    char_literal12=(Token)input.LT(1);
                    match(input,STAR,FOLLOW_STAR_in_importDeclaration280); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal12_tree = (Object)adaptor.create(char_literal12);
                    adaptor.addChild(root_0, char_literal12_tree);
                    }
                    char_literal13=(Token)input.LT(1);
                    match(input,SEMI,FOLLOW_SEMI_in_importDeclaration290); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal13_tree = (Object)adaptor.create(char_literal13);
                    adaptor.addChild(root_0, char_literal13_tree);
                    }

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:325:9: 'import' ( 'static' )? IDENTIFIER ( '.' IDENTIFIER )+ ( '.' '*' )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal14=(Token)input.LT(1);
                    match(input,IMPORT,FOLLOW_IMPORT_in_importDeclaration307); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal14_tree = (Object)adaptor.create(string_literal14);
                    adaptor.addChild(root_0, string_literal14_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:326:9: ( 'static' )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==STATIC) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:326:10: 'static'
                            {
                            string_literal15=(Token)input.LT(1);
                            match(input,STATIC,FOLLOW_STATIC_in_importDeclaration319); if (failed) return retval;
                            if ( backtracking==0 ) {
                            string_literal15_tree = (Object)adaptor.create(string_literal15);
                            adaptor.addChild(root_0, string_literal15_tree);
                            }

                            }
                            break;

                    }

                    IDENTIFIER16=(Token)input.LT(1);
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_importDeclaration340); if (failed) return retval;
                    if ( backtracking==0 ) {
                    IDENTIFIER16_tree = (Object)adaptor.create(IDENTIFIER16);
                    adaptor.addChild(root_0, IDENTIFIER16_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:329:9: ( '.' IDENTIFIER )+
                    int cnt7=0;
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==DOT) ) {
                            int LA7_1 = input.LA(2);

                            if ( (LA7_1==IDENTIFIER) ) {
                                alt7=1;
                            }


                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:329:10: '.' IDENTIFIER
                    	    {
                    	    char_literal17=(Token)input.LT(1);
                    	    match(input,DOT,FOLLOW_DOT_in_importDeclaration351); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal17_tree = (Object)adaptor.create(char_literal17);
                    	    adaptor.addChild(root_0, char_literal17_tree);
                    	    }
                    	    IDENTIFIER18=(Token)input.LT(1);
                    	    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_importDeclaration353); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    IDENTIFIER18_tree = (Object)adaptor.create(IDENTIFIER18);
                    	    adaptor.addChild(root_0, IDENTIFIER18_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt7 >= 1 ) break loop7;
                    	    if (backtracking>0) {failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(7, input);
                                throw eee;
                        }
                        cnt7++;
                    } while (true);

                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:331:9: ( '.' '*' )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==DOT) ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:331:10: '.' '*'
                            {
                            char_literal19=(Token)input.LT(1);
                            match(input,DOT,FOLLOW_DOT_in_importDeclaration375); if (failed) return retval;
                            if ( backtracking==0 ) {
                            char_literal19_tree = (Object)adaptor.create(char_literal19);
                            adaptor.addChild(root_0, char_literal19_tree);
                            }
                            char_literal20=(Token)input.LT(1);
                            match(input,STAR,FOLLOW_STAR_in_importDeclaration377); if (failed) return retval;
                            if ( backtracking==0 ) {
                            char_literal20_tree = (Object)adaptor.create(char_literal20);
                            adaptor.addChild(root_0, char_literal20_tree);
                            }

                            }
                            break;

                    }

                    char_literal21=(Token)input.LT(1);
                    match(input,SEMI,FOLLOW_SEMI_in_importDeclaration398); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal21_tree = (Object)adaptor.create(char_literal21);
                    adaptor.addChild(root_0, char_literal21_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 3, importDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end importDeclaration

    public static class qualifiedImportName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start qualifiedImportName
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:336:1: qualifiedImportName : IDENTIFIER ( '.' IDENTIFIER )* ;
    public final qualifiedImportName_return qualifiedImportName() throws RecognitionException {
        qualifiedImportName_return retval = new qualifiedImportName_return();
        retval.start = input.LT(1);
        int qualifiedImportName_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER22=null;
        Token char_literal23=null;
        Token IDENTIFIER24=null;

        Object IDENTIFIER22_tree=null;
        Object char_literal23_tree=null;
        Object IDENTIFIER24_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 4) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:337:5: ( IDENTIFIER ( '.' IDENTIFIER )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:337:9: IDENTIFIER ( '.' IDENTIFIER )*
            {
            root_0 = (Object)adaptor.nil();

            IDENTIFIER22=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedImportName418); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER22_tree = (Object)adaptor.create(IDENTIFIER22);
            adaptor.addChild(root_0, IDENTIFIER22_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:338:9: ( '.' IDENTIFIER )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==DOT) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:338:10: '.' IDENTIFIER
            	    {
            	    char_literal23=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_qualifiedImportName429); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal23_tree = (Object)adaptor.create(char_literal23);
            	    adaptor.addChild(root_0, char_literal23_tree);
            	    }
            	    IDENTIFIER24=(Token)input.LT(1);
            	    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedImportName431); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    IDENTIFIER24_tree = (Object)adaptor.create(IDENTIFIER24);
            	    adaptor.addChild(root_0, IDENTIFIER24_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 4, qualifiedImportName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end qualifiedImportName

    public static class typeDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start typeDeclaration
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:342:1: typeDeclaration : ( classOrInterfaceDeclaration | ';' );
    public final typeDeclaration_return typeDeclaration() throws RecognitionException {
        typeDeclaration_return retval = new typeDeclaration_return();
        retval.start = input.LT(1);
        int typeDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal26=null;
        classOrInterfaceDeclaration_return classOrInterfaceDeclaration25 = null;


        Object char_literal26_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 5) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:343:5: ( classOrInterfaceDeclaration | ';' )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==ABSTRACT||LA11_0==CLASS||LA11_0==ENUM||LA11_0==FINAL||LA11_0==INTERFACE||LA11_0==NATIVE||(LA11_0>=PRIVATE && LA11_0<=PUBLIC)||(LA11_0>=STATIC && LA11_0<=STRICTFP)||LA11_0==SYNCHRONIZED||LA11_0==TRANSIENT||LA11_0==VOLATILE||LA11_0==MONKEYS_AT) ) {
                alt11=1;
            }
            else if ( (LA11_0==SEMI) ) {
                alt11=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("342:1: typeDeclaration : ( classOrInterfaceDeclaration | ';' );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:343:9: classOrInterfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration462);
                    classOrInterfaceDeclaration25=classOrInterfaceDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceDeclaration25.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:344:9: ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal26=(Token)input.LT(1);
                    match(input,SEMI,FOLLOW_SEMI_in_typeDeclaration472); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal26_tree = (Object)adaptor.create(char_literal26);
                    adaptor.addChild(root_0, char_literal26_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 5, typeDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end typeDeclaration

    public static class classOrInterfaceDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start classOrInterfaceDeclaration
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:347:1: classOrInterfaceDeclaration : ( classDeclaration | interfaceDeclaration );
    public final classOrInterfaceDeclaration_return classOrInterfaceDeclaration() throws RecognitionException {
        classOrInterfaceDeclaration_return retval = new classOrInterfaceDeclaration_return();
        retval.start = input.LT(1);
        int classOrInterfaceDeclaration_StartIndex = input.index();
        Object root_0 = null;

        classDeclaration_return classDeclaration27 = null;

        interfaceDeclaration_return interfaceDeclaration28 = null;



        try {
            if ( backtracking>0 && alreadyParsedRule(input, 6) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:348:5: ( classDeclaration | interfaceDeclaration )
            int alt12=2;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA12_1 = input.LA(2);

                if ( (synpred12()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("347:1: classOrInterfaceDeclaration : ( classDeclaration | interfaceDeclaration );", 12, 1, input);

                    throw nvae;
                }
                }
                break;
            case PUBLIC:
                {
                int LA12_2 = input.LA(2);

                if ( (synpred12()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("347:1: classOrInterfaceDeclaration : ( classDeclaration | interfaceDeclaration );", 12, 2, input);

                    throw nvae;
                }
                }
                break;
            case PROTECTED:
                {
                int LA12_3 = input.LA(2);

                if ( (synpred12()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("347:1: classOrInterfaceDeclaration : ( classDeclaration | interfaceDeclaration );", 12, 3, input);

                    throw nvae;
                }
                }
                break;
            case PRIVATE:
                {
                int LA12_4 = input.LA(2);

                if ( (synpred12()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("347:1: classOrInterfaceDeclaration : ( classDeclaration | interfaceDeclaration );", 12, 4, input);

                    throw nvae;
                }
                }
                break;
            case STATIC:
                {
                int LA12_5 = input.LA(2);

                if ( (synpred12()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("347:1: classOrInterfaceDeclaration : ( classDeclaration | interfaceDeclaration );", 12, 5, input);

                    throw nvae;
                }
                }
                break;
            case ABSTRACT:
                {
                int LA12_6 = input.LA(2);

                if ( (synpred12()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("347:1: classOrInterfaceDeclaration : ( classDeclaration | interfaceDeclaration );", 12, 6, input);

                    throw nvae;
                }
                }
                break;
            case FINAL:
                {
                int LA12_7 = input.LA(2);

                if ( (synpred12()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("347:1: classOrInterfaceDeclaration : ( classDeclaration | interfaceDeclaration );", 12, 7, input);

                    throw nvae;
                }
                }
                break;
            case NATIVE:
                {
                int LA12_8 = input.LA(2);

                if ( (synpred12()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("347:1: classOrInterfaceDeclaration : ( classDeclaration | interfaceDeclaration );", 12, 8, input);

                    throw nvae;
                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA12_9 = input.LA(2);

                if ( (synpred12()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("347:1: classOrInterfaceDeclaration : ( classDeclaration | interfaceDeclaration );", 12, 9, input);

                    throw nvae;
                }
                }
                break;
            case TRANSIENT:
                {
                int LA12_10 = input.LA(2);

                if ( (synpred12()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("347:1: classOrInterfaceDeclaration : ( classDeclaration | interfaceDeclaration );", 12, 10, input);

                    throw nvae;
                }
                }
                break;
            case VOLATILE:
                {
                int LA12_11 = input.LA(2);

                if ( (synpred12()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("347:1: classOrInterfaceDeclaration : ( classDeclaration | interfaceDeclaration );", 12, 11, input);

                    throw nvae;
                }
                }
                break;
            case STRICTFP:
                {
                int LA12_12 = input.LA(2);

                if ( (synpred12()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("347:1: classOrInterfaceDeclaration : ( classDeclaration | interfaceDeclaration );", 12, 12, input);

                    throw nvae;
                }
                }
                break;
            case CLASS:
            case ENUM:
                {
                alt12=1;
                }
                break;
            case INTERFACE:
                {
                alt12=2;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("347:1: classOrInterfaceDeclaration : ( classDeclaration | interfaceDeclaration );", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:348:10: classDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classDeclaration_in_classOrInterfaceDeclaration493);
                    classDeclaration27=classDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, classDeclaration27.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:349:9: interfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration503);
                    interfaceDeclaration28=interfaceDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, interfaceDeclaration28.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 6, classOrInterfaceDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end classOrInterfaceDeclaration

    public static class modifiers_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start modifiers
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:353:1: modifiers : ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )* ;
    public final modifiers_return modifiers() throws RecognitionException {
        modifiers_return retval = new modifiers_return();
        retval.start = input.LT(1);
        int modifiers_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal30=null;
        Token string_literal31=null;
        Token string_literal32=null;
        Token string_literal33=null;
        Token string_literal34=null;
        Token string_literal35=null;
        Token string_literal36=null;
        Token string_literal37=null;
        Token string_literal38=null;
        Token string_literal39=null;
        Token string_literal40=null;
        annotation_return annotation29 = null;


        Object string_literal30_tree=null;
        Object string_literal31_tree=null;
        Object string_literal32_tree=null;
        Object string_literal33_tree=null;
        Object string_literal34_tree=null;
        Object string_literal35_tree=null;
        Object string_literal36_tree=null;
        Object string_literal37_tree=null;
        Object string_literal38_tree=null;
        Object string_literal39_tree=null;
        Object string_literal40_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 7) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:354:5: ( ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:355:5: ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )*
            {
            root_0 = (Object)adaptor.nil();

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:355:5: ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )*
            loop13:
            do {
                int alt13=13;
                switch ( input.LA(1) ) {
                case MONKEYS_AT:
                    {
                    int LA13_2 = input.LA(2);

                    if ( (LA13_2==IDENTIFIER) ) {
                        alt13=1;
                    }


                    }
                    break;
                case PUBLIC:
                    {
                    alt13=2;
                    }
                    break;
                case PROTECTED:
                    {
                    alt13=3;
                    }
                    break;
                case PRIVATE:
                    {
                    alt13=4;
                    }
                    break;
                case STATIC:
                    {
                    alt13=5;
                    }
                    break;
                case ABSTRACT:
                    {
                    alt13=6;
                    }
                    break;
                case FINAL:
                    {
                    alt13=7;
                    }
                    break;
                case NATIVE:
                    {
                    alt13=8;
                    }
                    break;
                case SYNCHRONIZED:
                    {
                    alt13=9;
                    }
                    break;
                case TRANSIENT:
                    {
                    alt13=10;
                    }
                    break;
                case VOLATILE:
                    {
                    alt13=11;
                    }
                    break;
                case STRICTFP:
                    {
                    alt13=12;
                    }
                    break;

                }

                switch (alt13) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:355:10: annotation
            	    {
            	    pushFollow(FOLLOW_annotation_in_modifiers538);
            	    annotation29=annotation();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, annotation29.getTree());

            	    }
            	    break;
            	case 2 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:356:9: 'public'
            	    {
            	    string_literal30=(Token)input.LT(1);
            	    match(input,PUBLIC,FOLLOW_PUBLIC_in_modifiers548); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    string_literal30_tree = (Object)adaptor.create(string_literal30);
            	    adaptor.addChild(root_0, string_literal30_tree);
            	    }

            	    }
            	    break;
            	case 3 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:357:9: 'protected'
            	    {
            	    string_literal31=(Token)input.LT(1);
            	    match(input,PROTECTED,FOLLOW_PROTECTED_in_modifiers558); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    string_literal31_tree = (Object)adaptor.create(string_literal31);
            	    adaptor.addChild(root_0, string_literal31_tree);
            	    }

            	    }
            	    break;
            	case 4 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:358:9: 'private'
            	    {
            	    string_literal32=(Token)input.LT(1);
            	    match(input,PRIVATE,FOLLOW_PRIVATE_in_modifiers568); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    string_literal32_tree = (Object)adaptor.create(string_literal32);
            	    adaptor.addChild(root_0, string_literal32_tree);
            	    }

            	    }
            	    break;
            	case 5 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:359:9: 'static'
            	    {
            	    string_literal33=(Token)input.LT(1);
            	    match(input,STATIC,FOLLOW_STATIC_in_modifiers578); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    string_literal33_tree = (Object)adaptor.create(string_literal33);
            	    adaptor.addChild(root_0, string_literal33_tree);
            	    }

            	    }
            	    break;
            	case 6 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:360:9: 'abstract'
            	    {
            	    string_literal34=(Token)input.LT(1);
            	    match(input,ABSTRACT,FOLLOW_ABSTRACT_in_modifiers588); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    string_literal34_tree = (Object)adaptor.create(string_literal34);
            	    adaptor.addChild(root_0, string_literal34_tree);
            	    }

            	    }
            	    break;
            	case 7 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:361:9: 'final'
            	    {
            	    string_literal35=(Token)input.LT(1);
            	    match(input,FINAL,FOLLOW_FINAL_in_modifiers598); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    string_literal35_tree = (Object)adaptor.create(string_literal35);
            	    adaptor.addChild(root_0, string_literal35_tree);
            	    }

            	    }
            	    break;
            	case 8 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:362:9: 'native'
            	    {
            	    string_literal36=(Token)input.LT(1);
            	    match(input,NATIVE,FOLLOW_NATIVE_in_modifiers608); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    string_literal36_tree = (Object)adaptor.create(string_literal36);
            	    adaptor.addChild(root_0, string_literal36_tree);
            	    }

            	    }
            	    break;
            	case 9 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:363:9: 'synchronized'
            	    {
            	    string_literal37=(Token)input.LT(1);
            	    match(input,SYNCHRONIZED,FOLLOW_SYNCHRONIZED_in_modifiers618); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    string_literal37_tree = (Object)adaptor.create(string_literal37);
            	    adaptor.addChild(root_0, string_literal37_tree);
            	    }

            	    }
            	    break;
            	case 10 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:364:9: 'transient'
            	    {
            	    string_literal38=(Token)input.LT(1);
            	    match(input,TRANSIENT,FOLLOW_TRANSIENT_in_modifiers628); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    string_literal38_tree = (Object)adaptor.create(string_literal38);
            	    adaptor.addChild(root_0, string_literal38_tree);
            	    }

            	    }
            	    break;
            	case 11 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:365:9: 'volatile'
            	    {
            	    string_literal39=(Token)input.LT(1);
            	    match(input,VOLATILE,FOLLOW_VOLATILE_in_modifiers638); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    string_literal39_tree = (Object)adaptor.create(string_literal39);
            	    adaptor.addChild(root_0, string_literal39_tree);
            	    }

            	    }
            	    break;
            	case 12 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:366:9: 'strictfp'
            	    {
            	    string_literal40=(Token)input.LT(1);
            	    match(input,STRICTFP,FOLLOW_STRICTFP_in_modifiers648); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    string_literal40_tree = (Object)adaptor.create(string_literal40);
            	    adaptor.addChild(root_0, string_literal40_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 7, modifiers_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end modifiers

    public static class variableModifiers_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start variableModifiers
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:371:1: variableModifiers : ( 'final' | annotation )* ;
    public final variableModifiers_return variableModifiers() throws RecognitionException {
        variableModifiers_return retval = new variableModifiers_return();
        retval.start = input.LT(1);
        int variableModifiers_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal41=null;
        annotation_return annotation42 = null;


        Object string_literal41_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 8) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:372:5: ( ( 'final' | annotation )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:372:9: ( 'final' | annotation )*
            {
            root_0 = (Object)adaptor.nil();

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:372:9: ( 'final' | annotation )*
            loop14:
            do {
                int alt14=3;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==FINAL) ) {
                    alt14=1;
                }
                else if ( (LA14_0==MONKEYS_AT) ) {
                    alt14=2;
                }


                switch (alt14) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:372:13: 'final'
            	    {
            	    string_literal41=(Token)input.LT(1);
            	    match(input,FINAL,FOLLOW_FINAL_in_variableModifiers680); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    string_literal41_tree = (Object)adaptor.create(string_literal41);
            	    adaptor.addChild(root_0, string_literal41_tree);
            	    }

            	    }
            	    break;
            	case 2 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:373:13: annotation
            	    {
            	    pushFollow(FOLLOW_annotation_in_variableModifiers694);
            	    annotation42=annotation();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, annotation42.getTree());

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 8, variableModifiers_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end variableModifiers

    public static class classDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start classDeclaration
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:378:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );
    public final classDeclaration_return classDeclaration() throws RecognitionException {
        classDeclaration_return retval = new classDeclaration_return();
        retval.start = input.LT(1);
        int classDeclaration_StartIndex = input.index();
        Object root_0 = null;

        normalClassDeclaration_return normalClassDeclaration43 = null;

        enumDeclaration_return enumDeclaration44 = null;



        try {
            if ( backtracking>0 && alreadyParsedRule(input, 9) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:379:5: ( normalClassDeclaration | enumDeclaration )
            int alt15=2;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA15_1 = input.LA(2);

                if ( (synpred27()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("378:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );", 15, 1, input);

                    throw nvae;
                }
                }
                break;
            case PUBLIC:
                {
                int LA15_2 = input.LA(2);

                if ( (synpred27()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("378:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );", 15, 2, input);

                    throw nvae;
                }
                }
                break;
            case PROTECTED:
                {
                int LA15_3 = input.LA(2);

                if ( (synpred27()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("378:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );", 15, 3, input);

                    throw nvae;
                }
                }
                break;
            case PRIVATE:
                {
                int LA15_4 = input.LA(2);

                if ( (synpred27()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("378:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );", 15, 4, input);

                    throw nvae;
                }
                }
                break;
            case STATIC:
                {
                int LA15_5 = input.LA(2);

                if ( (synpred27()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("378:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );", 15, 5, input);

                    throw nvae;
                }
                }
                break;
            case ABSTRACT:
                {
                int LA15_6 = input.LA(2);

                if ( (synpred27()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("378:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );", 15, 6, input);

                    throw nvae;
                }
                }
                break;
            case FINAL:
                {
                int LA15_7 = input.LA(2);

                if ( (synpred27()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("378:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );", 15, 7, input);

                    throw nvae;
                }
                }
                break;
            case NATIVE:
                {
                int LA15_8 = input.LA(2);

                if ( (synpred27()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("378:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );", 15, 8, input);

                    throw nvae;
                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA15_9 = input.LA(2);

                if ( (synpred27()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("378:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );", 15, 9, input);

                    throw nvae;
                }
                }
                break;
            case TRANSIENT:
                {
                int LA15_10 = input.LA(2);

                if ( (synpred27()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("378:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );", 15, 10, input);

                    throw nvae;
                }
                }
                break;
            case VOLATILE:
                {
                int LA15_11 = input.LA(2);

                if ( (synpred27()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("378:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );", 15, 11, input);

                    throw nvae;
                }
                }
                break;
            case STRICTFP:
                {
                int LA15_12 = input.LA(2);

                if ( (synpred27()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("378:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );", 15, 12, input);

                    throw nvae;
                }
                }
                break;
            case CLASS:
                {
                alt15=1;
                }
                break;
            case ENUM:
                {
                alt15=2;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("378:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:379:9: normalClassDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_normalClassDeclaration_in_classDeclaration730);
                    normalClassDeclaration43=normalClassDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, normalClassDeclaration43.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:380:9: enumDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enumDeclaration_in_classDeclaration740);
                    enumDeclaration44=enumDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, enumDeclaration44.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 9, classDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end classDeclaration

    public static class normalClassDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start normalClassDeclaration
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:383:1: normalClassDeclaration : modifiers 'class' IDENTIFIER ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody ;
    public final normalClassDeclaration_return normalClassDeclaration() throws RecognitionException {
        normalClassDeclaration_return retval = new normalClassDeclaration_return();
        retval.start = input.LT(1);
        int normalClassDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal46=null;
        Token IDENTIFIER47=null;
        Token string_literal49=null;
        Token string_literal51=null;
        modifiers_return modifiers45 = null;

        typeParameters_return typeParameters48 = null;

        type_return type50 = null;

        typeList_return typeList52 = null;

        classBody_return classBody53 = null;


        Object string_literal46_tree=null;
        Object IDENTIFIER47_tree=null;
        Object string_literal49_tree=null;
        Object string_literal51_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 10) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:384:5: ( modifiers 'class' IDENTIFIER ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:384:9: modifiers 'class' IDENTIFIER ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_normalClassDeclaration760);
            modifiers45=modifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, modifiers45.getTree());
            string_literal46=(Token)input.LT(1);
            match(input,CLASS,FOLLOW_CLASS_in_normalClassDeclaration763); if (failed) return retval;
            if ( backtracking==0 ) {
            string_literal46_tree = (Object)adaptor.create(string_literal46);
            adaptor.addChild(root_0, string_literal46_tree);
            }
            IDENTIFIER47=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_normalClassDeclaration765); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER47_tree = (Object)adaptor.create(IDENTIFIER47);
            adaptor.addChild(root_0, IDENTIFIER47_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:385:9: ( typeParameters )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==LT) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:385:10: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalClassDeclaration776);
                    typeParameters48=typeParameters();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, typeParameters48.getTree());

                    }
                    break;

            }

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:387:9: ( 'extends' type )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==EXTENDS) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:387:10: 'extends' type
                    {
                    string_literal49=(Token)input.LT(1);
                    match(input,EXTENDS,FOLLOW_EXTENDS_in_normalClassDeclaration798); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal49_tree = (Object)adaptor.create(string_literal49);
                    adaptor.addChild(root_0, string_literal49_tree);
                    }
                    pushFollow(FOLLOW_type_in_normalClassDeclaration800);
                    type50=type();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, type50.getTree());

                    }
                    break;

            }

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:389:9: ( 'implements' typeList )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==IMPLEMENTS) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:389:10: 'implements' typeList
                    {
                    string_literal51=(Token)input.LT(1);
                    match(input,IMPLEMENTS,FOLLOW_IMPLEMENTS_in_normalClassDeclaration822); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal51_tree = (Object)adaptor.create(string_literal51);
                    adaptor.addChild(root_0, string_literal51_tree);
                    }
                    pushFollow(FOLLOW_typeList_in_normalClassDeclaration824);
                    typeList52=typeList();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, typeList52.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_classBody_in_normalClassDeclaration857);
            classBody53=classBody();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, classBody53.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 10, normalClassDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end normalClassDeclaration

    public static class typeParameters_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start typeParameters
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:395:1: typeParameters : '<' typeParameter ( ',' typeParameter )* '>' ;
    public final typeParameters_return typeParameters() throws RecognitionException {
        typeParameters_return retval = new typeParameters_return();
        retval.start = input.LT(1);
        int typeParameters_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal54=null;
        Token char_literal56=null;
        Token char_literal58=null;
        typeParameter_return typeParameter55 = null;

        typeParameter_return typeParameter57 = null;


        Object char_literal54_tree=null;
        Object char_literal56_tree=null;
        Object char_literal58_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 11) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:396:5: ( '<' typeParameter ( ',' typeParameter )* '>' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:396:9: '<' typeParameter ( ',' typeParameter )* '>'
            {
            root_0 = (Object)adaptor.nil();

            char_literal54=(Token)input.LT(1);
            match(input,LT,FOLLOW_LT_in_typeParameters878); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal54_tree = (Object)adaptor.create(char_literal54);
            adaptor.addChild(root_0, char_literal54_tree);
            }
            pushFollow(FOLLOW_typeParameter_in_typeParameters892);
            typeParameter55=typeParameter();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, typeParameter55.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:398:13: ( ',' typeParameter )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==COMMA) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:398:14: ',' typeParameter
            	    {
            	    char_literal56=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_typeParameters907); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal56_tree = (Object)adaptor.create(char_literal56);
            	    adaptor.addChild(root_0, char_literal56_tree);
            	    }
            	    pushFollow(FOLLOW_typeParameter_in_typeParameters909);
            	    typeParameter57=typeParameter();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, typeParameter57.getTree());

            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);

            char_literal58=(Token)input.LT(1);
            match(input,GT,FOLLOW_GT_in_typeParameters934); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal58_tree = (Object)adaptor.create(char_literal58);
            adaptor.addChild(root_0, char_literal58_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 11, typeParameters_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end typeParameters

    public static class typeParameter_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start typeParameter
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:403:1: typeParameter : IDENTIFIER ( 'extends' typeBound )? ;
    public final typeParameter_return typeParameter() throws RecognitionException {
        typeParameter_return retval = new typeParameter_return();
        retval.start = input.LT(1);
        int typeParameter_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER59=null;
        Token string_literal60=null;
        typeBound_return typeBound61 = null;


        Object IDENTIFIER59_tree=null;
        Object string_literal60_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 12) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:404:5: ( IDENTIFIER ( 'extends' typeBound )? )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:404:9: IDENTIFIER ( 'extends' typeBound )?
            {
            root_0 = (Object)adaptor.nil();

            IDENTIFIER59=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_typeParameter954); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER59_tree = (Object)adaptor.create(IDENTIFIER59);
            adaptor.addChild(root_0, IDENTIFIER59_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:405:9: ( 'extends' typeBound )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==EXTENDS) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:405:10: 'extends' typeBound
                    {
                    string_literal60=(Token)input.LT(1);
                    match(input,EXTENDS,FOLLOW_EXTENDS_in_typeParameter965); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal60_tree = (Object)adaptor.create(string_literal60);
                    adaptor.addChild(root_0, string_literal60_tree);
                    }
                    pushFollow(FOLLOW_typeBound_in_typeParameter967);
                    typeBound61=typeBound();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, typeBound61.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 12, typeParameter_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end typeParameter

    public static class typeBound_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start typeBound
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:410:1: typeBound : type ( '&' type )* ;
    public final typeBound_return typeBound() throws RecognitionException {
        typeBound_return retval = new typeBound_return();
        retval.start = input.LT(1);
        int typeBound_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal63=null;
        type_return type62 = null;

        type_return type64 = null;


        Object char_literal63_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 13) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:411:5: ( type ( '&' type )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:411:9: type ( '&' type )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_type_in_typeBound999);
            type62=type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, type62.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:412:9: ( '&' type )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==AMP) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:412:10: '&' type
            	    {
            	    char_literal63=(Token)input.LT(1);
            	    match(input,AMP,FOLLOW_AMP_in_typeBound1010); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal63_tree = (Object)adaptor.create(char_literal63);
            	    adaptor.addChild(root_0, char_literal63_tree);
            	    }
            	    pushFollow(FOLLOW_type_in_typeBound1012);
            	    type64=type();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, type64.getTree());

            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 13, typeBound_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end typeBound

    public static class enumDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start enumDeclaration
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:417:1: enumDeclaration : modifiers ( 'enum' ) IDENTIFIER ( 'implements' typeList )? enumBody ;
    public final enumDeclaration_return enumDeclaration() throws RecognitionException {
        enumDeclaration_return retval = new enumDeclaration_return();
        retval.start = input.LT(1);
        int enumDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal66=null;
        Token IDENTIFIER67=null;
        Token string_literal68=null;
        modifiers_return modifiers65 = null;

        typeList_return typeList69 = null;

        enumBody_return enumBody70 = null;


        Object string_literal66_tree=null;
        Object IDENTIFIER67_tree=null;
        Object string_literal68_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 14) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:418:5: ( modifiers ( 'enum' ) IDENTIFIER ( 'implements' typeList )? enumBody )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:418:9: modifiers ( 'enum' ) IDENTIFIER ( 'implements' typeList )? enumBody
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_enumDeclaration1044);
            modifiers65=modifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, modifiers65.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:419:9: ( 'enum' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:419:10: 'enum'
            {
            string_literal66=(Token)input.LT(1);
            match(input,ENUM,FOLLOW_ENUM_in_enumDeclaration1056); if (failed) return retval;
            if ( backtracking==0 ) {
            string_literal66_tree = (Object)adaptor.create(string_literal66);
            adaptor.addChild(root_0, string_literal66_tree);
            }

            }

            IDENTIFIER67=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_enumDeclaration1077); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER67_tree = (Object)adaptor.create(IDENTIFIER67);
            adaptor.addChild(root_0, IDENTIFIER67_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:422:9: ( 'implements' typeList )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==IMPLEMENTS) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:422:10: 'implements' typeList
                    {
                    string_literal68=(Token)input.LT(1);
                    match(input,IMPLEMENTS,FOLLOW_IMPLEMENTS_in_enumDeclaration1088); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal68_tree = (Object)adaptor.create(string_literal68);
                    adaptor.addChild(root_0, string_literal68_tree);
                    }
                    pushFollow(FOLLOW_typeList_in_enumDeclaration1090);
                    typeList69=typeList();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, typeList69.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_enumBody_in_enumDeclaration1111);
            enumBody70=enumBody();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, enumBody70.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 14, enumDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end enumDeclaration

    public static class enumBody_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start enumBody
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:428:1: enumBody : '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' ;
    public final enumBody_return enumBody() throws RecognitionException {
        enumBody_return retval = new enumBody_return();
        retval.start = input.LT(1);
        int enumBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal71=null;
        Token char_literal73=null;
        Token char_literal75=null;
        enumConstants_return enumConstants72 = null;

        enumBodyDeclarations_return enumBodyDeclarations74 = null;


        Object char_literal71_tree=null;
        Object char_literal73_tree=null;
        Object char_literal75_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 15) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:429:5: ( '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:429:9: '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal71=(Token)input.LT(1);
            match(input,LBRACE,FOLLOW_LBRACE_in_enumBody1136); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal71_tree = (Object)adaptor.create(char_literal71);
            adaptor.addChild(root_0, char_literal71_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:430:9: ( enumConstants )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==IDENTIFIER||LA23_0==MONKEYS_AT) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:430:10: enumConstants
                    {
                    pushFollow(FOLLOW_enumConstants_in_enumBody1147);
                    enumConstants72=enumConstants();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, enumConstants72.getTree());

                    }
                    break;

            }

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:432:9: ( ',' )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==COMMA) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:0:0: ','
                    {
                    char_literal73=(Token)input.LT(1);
                    match(input,COMMA,FOLLOW_COMMA_in_enumBody1169); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal73_tree = (Object)adaptor.create(char_literal73);
                    adaptor.addChild(root_0, char_literal73_tree);
                    }

                    }
                    break;

            }

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:433:9: ( enumBodyDeclarations )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==SEMI) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:433:10: enumBodyDeclarations
                    {
                    pushFollow(FOLLOW_enumBodyDeclarations_in_enumBody1182);
                    enumBodyDeclarations74=enumBodyDeclarations();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, enumBodyDeclarations74.getTree());

                    }
                    break;

            }

            char_literal75=(Token)input.LT(1);
            match(input,RBRACE,FOLLOW_RBRACE_in_enumBody1204); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal75_tree = (Object)adaptor.create(char_literal75);
            adaptor.addChild(root_0, char_literal75_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 15, enumBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end enumBody

    public static class enumConstants_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start enumConstants
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:438:1: enumConstants : enumConstant ( ',' enumConstant )* ;
    public final enumConstants_return enumConstants() throws RecognitionException {
        enumConstants_return retval = new enumConstants_return();
        retval.start = input.LT(1);
        int enumConstants_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal77=null;
        enumConstant_return enumConstant76 = null;

        enumConstant_return enumConstant78 = null;


        Object char_literal77_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 16) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:439:5: ( enumConstant ( ',' enumConstant )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:439:9: enumConstant ( ',' enumConstant )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_enumConstant_in_enumConstants1224);
            enumConstant76=enumConstant();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, enumConstant76.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:440:9: ( ',' enumConstant )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==COMMA) ) {
                    int LA26_1 = input.LA(2);

                    if ( (LA26_1==IDENTIFIER||LA26_1==MONKEYS_AT) ) {
                        alt26=1;
                    }


                }


                switch (alt26) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:440:10: ',' enumConstant
            	    {
            	    char_literal77=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_enumConstants1235); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal77_tree = (Object)adaptor.create(char_literal77);
            	    adaptor.addChild(root_0, char_literal77_tree);
            	    }
            	    pushFollow(FOLLOW_enumConstant_in_enumConstants1237);
            	    enumConstant78=enumConstant();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, enumConstant78.getTree());

            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 16, enumConstants_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end enumConstants

    public static class enumConstant_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start enumConstant
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:444:1: enumConstant : ( annotations )? IDENTIFIER ( arguments )? ( classBody )? ;
    public final enumConstant_return enumConstant() throws RecognitionException {
        enumConstant_return retval = new enumConstant_return();
        retval.start = input.LT(1);
        int enumConstant_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER80=null;
        annotations_return annotations79 = null;

        arguments_return arguments81 = null;

        classBody_return classBody82 = null;


        Object IDENTIFIER80_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 17) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:449:5: ( ( annotations )? IDENTIFIER ( arguments )? ( classBody )? )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:449:9: ( annotations )? IDENTIFIER ( arguments )? ( classBody )?
            {
            root_0 = (Object)adaptor.nil();

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:449:9: ( annotations )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==MONKEYS_AT) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:449:10: annotations
                    {
                    pushFollow(FOLLOW_annotations_in_enumConstant1271);
                    annotations79=annotations();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, annotations79.getTree());

                    }
                    break;

            }

            IDENTIFIER80=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_enumConstant1292); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER80_tree = (Object)adaptor.create(IDENTIFIER80);
            adaptor.addChild(root_0, IDENTIFIER80_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:452:9: ( arguments )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==LPAREN) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:452:10: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_enumConstant1303);
                    arguments81=arguments();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, arguments81.getTree());

                    }
                    break;

            }

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:454:9: ( classBody )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==LBRACE) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:454:10: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_enumConstant1325);
                    classBody82=classBody();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, classBody82.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 17, enumConstant_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end enumConstant

    public static class enumBodyDeclarations_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start enumBodyDeclarations
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:460:1: enumBodyDeclarations : ';' ( classBodyDeclaration )* ;
    public final enumBodyDeclarations_return enumBodyDeclarations() throws RecognitionException {
        enumBodyDeclarations_return retval = new enumBodyDeclarations_return();
        retval.start = input.LT(1);
        int enumBodyDeclarations_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal83=null;
        classBodyDeclaration_return classBodyDeclaration84 = null;


        Object char_literal83_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 18) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:461:5: ( ';' ( classBodyDeclaration )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:461:9: ';' ( classBodyDeclaration )*
            {
            root_0 = (Object)adaptor.nil();

            char_literal83=(Token)input.LT(1);
            match(input,SEMI,FOLLOW_SEMI_in_enumBodyDeclarations1366); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal83_tree = (Object)adaptor.create(char_literal83);
            adaptor.addChild(root_0, char_literal83_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:462:9: ( classBodyDeclaration )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==IDENTIFIER||LA30_0==ABSTRACT||LA30_0==BOOLEAN||LA30_0==BYTE||(LA30_0>=CHAR && LA30_0<=CLASS)||LA30_0==DOUBLE||LA30_0==ENUM||LA30_0==FINAL||LA30_0==FLOAT||(LA30_0>=INT && LA30_0<=NATIVE)||(LA30_0>=PRIVATE && LA30_0<=PUBLIC)||(LA30_0>=SHORT && LA30_0<=STRICTFP)||LA30_0==SYNCHRONIZED||LA30_0==TRANSIENT||(LA30_0>=VOID && LA30_0<=VOLATILE)||LA30_0==LBRACE||LA30_0==SEMI||LA30_0==MONKEYS_AT||LA30_0==LT) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:462:10: classBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_classBodyDeclaration_in_enumBodyDeclarations1378);
            	    classBodyDeclaration84=classBodyDeclaration();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, classBodyDeclaration84.getTree());

            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 18, enumBodyDeclarations_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end enumBodyDeclarations

    public static class interfaceDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start interfaceDeclaration
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:466:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );
    public final interfaceDeclaration_return interfaceDeclaration() throws RecognitionException {
        interfaceDeclaration_return retval = new interfaceDeclaration_return();
        retval.start = input.LT(1);
        int interfaceDeclaration_StartIndex = input.index();
        Object root_0 = null;

        normalInterfaceDeclaration_return normalInterfaceDeclaration85 = null;

        annotationTypeDeclaration_return annotationTypeDeclaration86 = null;



        try {
            if ( backtracking>0 && alreadyParsedRule(input, 19) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:467:5: ( normalInterfaceDeclaration | annotationTypeDeclaration )
            int alt31=2;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA31_1 = input.LA(2);

                if ( (synpred43()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("466:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );", 31, 1, input);

                    throw nvae;
                }
                }
                break;
            case PUBLIC:
                {
                int LA31_2 = input.LA(2);

                if ( (synpred43()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("466:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );", 31, 2, input);

                    throw nvae;
                }
                }
                break;
            case PROTECTED:
                {
                int LA31_3 = input.LA(2);

                if ( (synpred43()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("466:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );", 31, 3, input);

                    throw nvae;
                }
                }
                break;
            case PRIVATE:
                {
                int LA31_4 = input.LA(2);

                if ( (synpred43()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("466:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );", 31, 4, input);

                    throw nvae;
                }
                }
                break;
            case STATIC:
                {
                int LA31_5 = input.LA(2);

                if ( (synpred43()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("466:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );", 31, 5, input);

                    throw nvae;
                }
                }
                break;
            case ABSTRACT:
                {
                int LA31_6 = input.LA(2);

                if ( (synpred43()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("466:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );", 31, 6, input);

                    throw nvae;
                }
                }
                break;
            case FINAL:
                {
                int LA31_7 = input.LA(2);

                if ( (synpred43()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("466:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );", 31, 7, input);

                    throw nvae;
                }
                }
                break;
            case NATIVE:
                {
                int LA31_8 = input.LA(2);

                if ( (synpred43()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("466:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );", 31, 8, input);

                    throw nvae;
                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA31_9 = input.LA(2);

                if ( (synpred43()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("466:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );", 31, 9, input);

                    throw nvae;
                }
                }
                break;
            case TRANSIENT:
                {
                int LA31_10 = input.LA(2);

                if ( (synpred43()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("466:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );", 31, 10, input);

                    throw nvae;
                }
                }
                break;
            case VOLATILE:
                {
                int LA31_11 = input.LA(2);

                if ( (synpred43()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("466:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );", 31, 11, input);

                    throw nvae;
                }
                }
                break;
            case STRICTFP:
                {
                int LA31_12 = input.LA(2);

                if ( (synpred43()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("466:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );", 31, 12, input);

                    throw nvae;
                }
                }
                break;
            case INTERFACE:
                {
                alt31=1;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("466:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );", 31, 0, input);

                throw nvae;
            }

            switch (alt31) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:467:9: normalInterfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration1409);
                    normalInterfaceDeclaration85=normalInterfaceDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, normalInterfaceDeclaration85.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:468:9: annotationTypeDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration1419);
                    annotationTypeDeclaration86=annotationTypeDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, annotationTypeDeclaration86.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 19, interfaceDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end interfaceDeclaration

    public static class normalInterfaceDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start normalInterfaceDeclaration
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:471:1: normalInterfaceDeclaration : modifiers 'interface' IDENTIFIER ( typeParameters )? ( 'extends' typeList )? interfaceBody ;
    public final normalInterfaceDeclaration_return normalInterfaceDeclaration() throws RecognitionException {
        normalInterfaceDeclaration_return retval = new normalInterfaceDeclaration_return();
        retval.start = input.LT(1);
        int normalInterfaceDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal88=null;
        Token IDENTIFIER89=null;
        Token string_literal91=null;
        modifiers_return modifiers87 = null;

        typeParameters_return typeParameters90 = null;

        typeList_return typeList92 = null;

        interfaceBody_return interfaceBody93 = null;


        Object string_literal88_tree=null;
        Object IDENTIFIER89_tree=null;
        Object string_literal91_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 20) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:472:5: ( modifiers 'interface' IDENTIFIER ( typeParameters )? ( 'extends' typeList )? interfaceBody )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:472:9: modifiers 'interface' IDENTIFIER ( typeParameters )? ( 'extends' typeList )? interfaceBody
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_normalInterfaceDeclaration1443);
            modifiers87=modifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, modifiers87.getTree());
            string_literal88=(Token)input.LT(1);
            match(input,INTERFACE,FOLLOW_INTERFACE_in_normalInterfaceDeclaration1445); if (failed) return retval;
            if ( backtracking==0 ) {
            string_literal88_tree = (Object)adaptor.create(string_literal88);
            adaptor.addChild(root_0, string_literal88_tree);
            }
            IDENTIFIER89=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_normalInterfaceDeclaration1447); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER89_tree = (Object)adaptor.create(IDENTIFIER89);
            adaptor.addChild(root_0, IDENTIFIER89_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:473:9: ( typeParameters )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==LT) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:473:10: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalInterfaceDeclaration1458);
                    typeParameters90=typeParameters();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, typeParameters90.getTree());

                    }
                    break;

            }

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:475:9: ( 'extends' typeList )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==EXTENDS) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:475:10: 'extends' typeList
                    {
                    string_literal91=(Token)input.LT(1);
                    match(input,EXTENDS,FOLLOW_EXTENDS_in_normalInterfaceDeclaration1480); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal91_tree = (Object)adaptor.create(string_literal91);
                    adaptor.addChild(root_0, string_literal91_tree);
                    }
                    pushFollow(FOLLOW_typeList_in_normalInterfaceDeclaration1482);
                    typeList92=typeList();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, typeList92.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_interfaceBody_in_normalInterfaceDeclaration1503);
            interfaceBody93=interfaceBody();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, interfaceBody93.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 20, normalInterfaceDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end normalInterfaceDeclaration

    public static class typeList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start typeList
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:480:1: typeList : type ( ',' type )* ;
    public final typeList_return typeList() throws RecognitionException {
        typeList_return retval = new typeList_return();
        retval.start = input.LT(1);
        int typeList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal95=null;
        type_return type94 = null;

        type_return type96 = null;


        Object char_literal95_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 21) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:481:5: ( type ( ',' type )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:481:9: type ( ',' type )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_type_in_typeList1523);
            type94=type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, type94.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:482:9: ( ',' type )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==COMMA) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:482:10: ',' type
            	    {
            	    char_literal95=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_typeList1534); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal95_tree = (Object)adaptor.create(char_literal95);
            	    adaptor.addChild(root_0, char_literal95_tree);
            	    }
            	    pushFollow(FOLLOW_type_in_typeList1536);
            	    type96=type();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, type96.getTree());

            	    }
            	    break;

            	default :
            	    break loop34;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 21, typeList_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end typeList

    public static class classBody_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start classBody
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:486:1: classBody : '{' ( classBodyDeclaration )* '}' ;
    public final classBody_return classBody() throws RecognitionException {
        classBody_return retval = new classBody_return();
        retval.start = input.LT(1);
        int classBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal97=null;
        Token char_literal99=null;
        classBodyDeclaration_return classBodyDeclaration98 = null;


        Object char_literal97_tree=null;
        Object char_literal99_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 22) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:487:5: ( '{' ( classBodyDeclaration )* '}' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:487:9: '{' ( classBodyDeclaration )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal97=(Token)input.LT(1);
            match(input,LBRACE,FOLLOW_LBRACE_in_classBody1567); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal97_tree = (Object)adaptor.create(char_literal97);
            adaptor.addChild(root_0, char_literal97_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:488:9: ( classBodyDeclaration )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( (LA35_0==IDENTIFIER||LA35_0==ABSTRACT||LA35_0==BOOLEAN||LA35_0==BYTE||(LA35_0>=CHAR && LA35_0<=CLASS)||LA35_0==DOUBLE||LA35_0==ENUM||LA35_0==FINAL||LA35_0==FLOAT||(LA35_0>=INT && LA35_0<=NATIVE)||(LA35_0>=PRIVATE && LA35_0<=PUBLIC)||(LA35_0>=SHORT && LA35_0<=STRICTFP)||LA35_0==SYNCHRONIZED||LA35_0==TRANSIENT||(LA35_0>=VOID && LA35_0<=VOLATILE)||LA35_0==LBRACE||LA35_0==SEMI||LA35_0==MONKEYS_AT||LA35_0==LT) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:488:10: classBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_classBodyDeclaration_in_classBody1579);
            	    classBodyDeclaration98=classBodyDeclaration();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, classBodyDeclaration98.getTree());

            	    }
            	    break;

            	default :
            	    break loop35;
                }
            } while (true);

            char_literal99=(Token)input.LT(1);
            match(input,RBRACE,FOLLOW_RBRACE_in_classBody1601); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal99_tree = (Object)adaptor.create(char_literal99);
            adaptor.addChild(root_0, char_literal99_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 22, classBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end classBody

    public static class interfaceBody_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start interfaceBody
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:493:1: interfaceBody : '{' ( interfaceBodyDeclaration )* '}' ;
    public final interfaceBody_return interfaceBody() throws RecognitionException {
        interfaceBody_return retval = new interfaceBody_return();
        retval.start = input.LT(1);
        int interfaceBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal100=null;
        Token char_literal102=null;
        interfaceBodyDeclaration_return interfaceBodyDeclaration101 = null;


        Object char_literal100_tree=null;
        Object char_literal102_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 23) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:494:5: ( '{' ( interfaceBodyDeclaration )* '}' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:494:9: '{' ( interfaceBodyDeclaration )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal100=(Token)input.LT(1);
            match(input,LBRACE,FOLLOW_LBRACE_in_interfaceBody1621); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal100_tree = (Object)adaptor.create(char_literal100);
            adaptor.addChild(root_0, char_literal100_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:495:9: ( interfaceBodyDeclaration )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( (LA36_0==IDENTIFIER||LA36_0==ABSTRACT||LA36_0==BOOLEAN||LA36_0==BYTE||(LA36_0>=CHAR && LA36_0<=CLASS)||LA36_0==DOUBLE||LA36_0==ENUM||LA36_0==FINAL||LA36_0==FLOAT||(LA36_0>=INT && LA36_0<=NATIVE)||(LA36_0>=PRIVATE && LA36_0<=PUBLIC)||(LA36_0>=SHORT && LA36_0<=STRICTFP)||LA36_0==SYNCHRONIZED||LA36_0==TRANSIENT||(LA36_0>=VOID && LA36_0<=VOLATILE)||LA36_0==SEMI||LA36_0==MONKEYS_AT||LA36_0==LT) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:495:10: interfaceBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_interfaceBodyDeclaration_in_interfaceBody1633);
            	    interfaceBodyDeclaration101=interfaceBodyDeclaration();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, interfaceBodyDeclaration101.getTree());

            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);

            char_literal102=(Token)input.LT(1);
            match(input,RBRACE,FOLLOW_RBRACE_in_interfaceBody1655); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal102_tree = (Object)adaptor.create(char_literal102);
            adaptor.addChild(root_0, char_literal102_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 23, interfaceBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end interfaceBody

    public static class classBodyDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start classBodyDeclaration
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:500:1: classBodyDeclaration : ( ';' | ( 'static' )? block | memberDecl );
    public final classBodyDeclaration_return classBodyDeclaration() throws RecognitionException {
        classBodyDeclaration_return retval = new classBodyDeclaration_return();
        retval.start = input.LT(1);
        int classBodyDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal103=null;
        Token string_literal104=null;
        block_return block105 = null;

        memberDecl_return memberDecl106 = null;


        Object char_literal103_tree=null;
        Object string_literal104_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 24) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:501:5: ( ';' | ( 'static' )? block | memberDecl )
            int alt38=3;
            switch ( input.LA(1) ) {
            case SEMI:
                {
                alt38=1;
                }
                break;
            case STATIC:
                {
                int LA38_2 = input.LA(2);

                if ( (LA38_2==IDENTIFIER||LA38_2==ABSTRACT||LA38_2==BOOLEAN||LA38_2==BYTE||(LA38_2>=CHAR && LA38_2<=CLASS)||LA38_2==DOUBLE||LA38_2==ENUM||LA38_2==FINAL||LA38_2==FLOAT||(LA38_2>=INT && LA38_2<=NATIVE)||(LA38_2>=PRIVATE && LA38_2<=PUBLIC)||(LA38_2>=SHORT && LA38_2<=STRICTFP)||LA38_2==SYNCHRONIZED||LA38_2==TRANSIENT||(LA38_2>=VOID && LA38_2<=VOLATILE)||LA38_2==MONKEYS_AT||LA38_2==LT) ) {
                    alt38=3;
                }
                else if ( (LA38_2==LBRACE) ) {
                    alt38=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("500:1: classBodyDeclaration : ( ';' | ( 'static' )? block | memberDecl );", 38, 2, input);

                    throw nvae;
                }
                }
                break;
            case LBRACE:
                {
                alt38=2;
                }
                break;
            case IDENTIFIER:
            case ABSTRACT:
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case CLASS:
            case DOUBLE:
            case ENUM:
            case FINAL:
            case FLOAT:
            case INT:
            case INTERFACE:
            case LONG:
            case NATIVE:
            case PRIVATE:
            case PROTECTED:
            case PUBLIC:
            case SHORT:
            case STRICTFP:
            case SYNCHRONIZED:
            case TRANSIENT:
            case VOID:
            case VOLATILE:
            case MONKEYS_AT:
            case LT:
                {
                alt38=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("500:1: classBodyDeclaration : ( ';' | ( 'static' )? block | memberDecl );", 38, 0, input);

                throw nvae;
            }

            switch (alt38) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:501:9: ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal103=(Token)input.LT(1);
                    match(input,SEMI,FOLLOW_SEMI_in_classBodyDeclaration1675); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal103_tree = (Object)adaptor.create(char_literal103);
                    adaptor.addChild(root_0, char_literal103_tree);
                    }

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:502:9: ( 'static' )? block
                    {
                    root_0 = (Object)adaptor.nil();

                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:502:9: ( 'static' )?
                    int alt37=2;
                    int LA37_0 = input.LA(1);

                    if ( (LA37_0==STATIC) ) {
                        alt37=1;
                    }
                    switch (alt37) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:502:10: 'static'
                            {
                            string_literal104=(Token)input.LT(1);
                            match(input,STATIC,FOLLOW_STATIC_in_classBodyDeclaration1686); if (failed) return retval;
                            if ( backtracking==0 ) {
                            string_literal104_tree = (Object)adaptor.create(string_literal104);
                            adaptor.addChild(root_0, string_literal104_tree);
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_block_in_classBodyDeclaration1708);
                    block105=block();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, block105.getTree());

                    }
                    break;
                case 3 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:505:9: memberDecl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_memberDecl_in_classBodyDeclaration1718);
                    memberDecl106=memberDecl();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, memberDecl106.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 24, classBodyDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end classBodyDeclaration

    public static class memberDecl_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start memberDecl
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:508:1: memberDecl : ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration );
    public final memberDecl_return memberDecl() throws RecognitionException {
        memberDecl_return retval = new memberDecl_return();
        retval.start = input.LT(1);
        int memberDecl_StartIndex = input.index();
        Object root_0 = null;

        fieldDeclaration_return fieldDeclaration107 = null;

        methodDeclaration_return methodDeclaration108 = null;

        classDeclaration_return classDeclaration109 = null;

        interfaceDeclaration_return interfaceDeclaration110 = null;



        try {
            if ( backtracking>0 && alreadyParsedRule(input, 25) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:509:5: ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration )
            int alt39=4;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA39_1 = input.LA(2);

                if ( (synpred52()) ) {
                    alt39=1;
                }
                else if ( (synpred53()) ) {
                    alt39=2;
                }
                else if ( (synpred54()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("508:1: memberDecl : ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration );", 39, 1, input);

                    throw nvae;
                }
                }
                break;
            case PUBLIC:
                {
                int LA39_2 = input.LA(2);

                if ( (synpred52()) ) {
                    alt39=1;
                }
                else if ( (synpred53()) ) {
                    alt39=2;
                }
                else if ( (synpred54()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("508:1: memberDecl : ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration );", 39, 2, input);

                    throw nvae;
                }
                }
                break;
            case PROTECTED:
                {
                int LA39_3 = input.LA(2);

                if ( (synpred52()) ) {
                    alt39=1;
                }
                else if ( (synpred53()) ) {
                    alt39=2;
                }
                else if ( (synpred54()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("508:1: memberDecl : ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration );", 39, 3, input);

                    throw nvae;
                }
                }
                break;
            case PRIVATE:
                {
                int LA39_4 = input.LA(2);

                if ( (synpred52()) ) {
                    alt39=1;
                }
                else if ( (synpred53()) ) {
                    alt39=2;
                }
                else if ( (synpred54()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("508:1: memberDecl : ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration );", 39, 4, input);

                    throw nvae;
                }
                }
                break;
            case STATIC:
                {
                int LA39_5 = input.LA(2);

                if ( (synpred52()) ) {
                    alt39=1;
                }
                else if ( (synpred53()) ) {
                    alt39=2;
                }
                else if ( (synpred54()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("508:1: memberDecl : ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration );", 39, 5, input);

                    throw nvae;
                }
                }
                break;
            case ABSTRACT:
                {
                int LA39_6 = input.LA(2);

                if ( (synpred52()) ) {
                    alt39=1;
                }
                else if ( (synpred53()) ) {
                    alt39=2;
                }
                else if ( (synpred54()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("508:1: memberDecl : ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration );", 39, 6, input);

                    throw nvae;
                }
                }
                break;
            case FINAL:
                {
                int LA39_7 = input.LA(2);

                if ( (synpred52()) ) {
                    alt39=1;
                }
                else if ( (synpred53()) ) {
                    alt39=2;
                }
                else if ( (synpred54()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("508:1: memberDecl : ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration );", 39, 7, input);

                    throw nvae;
                }
                }
                break;
            case NATIVE:
                {
                int LA39_8 = input.LA(2);

                if ( (synpred52()) ) {
                    alt39=1;
                }
                else if ( (synpred53()) ) {
                    alt39=2;
                }
                else if ( (synpred54()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("508:1: memberDecl : ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration );", 39, 8, input);

                    throw nvae;
                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA39_9 = input.LA(2);

                if ( (synpred52()) ) {
                    alt39=1;
                }
                else if ( (synpred53()) ) {
                    alt39=2;
                }
                else if ( (synpred54()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("508:1: memberDecl : ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration );", 39, 9, input);

                    throw nvae;
                }
                }
                break;
            case TRANSIENT:
                {
                int LA39_10 = input.LA(2);

                if ( (synpred52()) ) {
                    alt39=1;
                }
                else if ( (synpred53()) ) {
                    alt39=2;
                }
                else if ( (synpred54()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("508:1: memberDecl : ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration );", 39, 10, input);

                    throw nvae;
                }
                }
                break;
            case VOLATILE:
                {
                int LA39_11 = input.LA(2);

                if ( (synpred52()) ) {
                    alt39=1;
                }
                else if ( (synpred53()) ) {
                    alt39=2;
                }
                else if ( (synpred54()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("508:1: memberDecl : ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration );", 39, 11, input);

                    throw nvae;
                }
                }
                break;
            case STRICTFP:
                {
                int LA39_12 = input.LA(2);

                if ( (synpred52()) ) {
                    alt39=1;
                }
                else if ( (synpred53()) ) {
                    alt39=2;
                }
                else if ( (synpred54()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("508:1: memberDecl : ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration );", 39, 12, input);

                    throw nvae;
                }
                }
                break;
            case IDENTIFIER:
                {
                int LA39_13 = input.LA(2);

                if ( (synpred52()) ) {
                    alt39=1;
                }
                else if ( (synpred53()) ) {
                    alt39=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("508:1: memberDecl : ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration );", 39, 13, input);

                    throw nvae;
                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                {
                int LA39_14 = input.LA(2);

                if ( (synpred52()) ) {
                    alt39=1;
                }
                else if ( (synpred53()) ) {
                    alt39=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("508:1: memberDecl : ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration );", 39, 14, input);

                    throw nvae;
                }
                }
                break;
            case VOID:
            case LT:
                {
                alt39=2;
                }
                break;
            case CLASS:
            case ENUM:
                {
                alt39=3;
                }
                break;
            case INTERFACE:
                {
                alt39=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("508:1: memberDecl : ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration );", 39, 0, input);

                throw nvae;
            }

            switch (alt39) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:509:10: fieldDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_fieldDeclaration_in_memberDecl1739);
                    fieldDeclaration107=fieldDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, fieldDeclaration107.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:510:10: methodDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_methodDeclaration_in_memberDecl1750);
                    methodDeclaration108=methodDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, methodDeclaration108.getTree());

                    }
                    break;
                case 3 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:511:10: classDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classDeclaration_in_memberDecl1761);
                    classDeclaration109=classDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, classDeclaration109.getTree());

                    }
                    break;
                case 4 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:512:10: interfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceDeclaration_in_memberDecl1772);
                    interfaceDeclaration110=interfaceDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, interfaceDeclaration110.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 25, memberDecl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end memberDecl

    public static class methodDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start methodDeclaration
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:516:1: methodDeclaration : ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) );
    public final methodDeclaration_return methodDeclaration() throws RecognitionException {
        methodDeclaration_return retval = new methodDeclaration_return();
        retval.start = input.LT(1);
        int methodDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER113=null;
        Token string_literal115=null;
        Token char_literal117=null;
        Token char_literal120=null;
        Token string_literal124=null;
        Token IDENTIFIER125=null;
        Token char_literal127=null;
        Token char_literal128=null;
        Token string_literal129=null;
        Token char_literal132=null;
        modifiers_return modifiers111 = null;

        typeParameters_return typeParameters112 = null;

        formalParameters_return formalParameters114 = null;

        qualifiedNameList_return qualifiedNameList116 = null;

        explicitConstructorInvocation_return explicitConstructorInvocation118 = null;

        blockStatement_return blockStatement119 = null;

        modifiers_return modifiers121 = null;

        typeParameters_return typeParameters122 = null;

        type_return type123 = null;

        formalParameters_return formalParameters126 = null;

        qualifiedNameList_return qualifiedNameList130 = null;

        block_return block131 = null;


        Object IDENTIFIER113_tree=null;
        Object string_literal115_tree=null;
        Object char_literal117_tree=null;
        Object char_literal120_tree=null;
        Object string_literal124_tree=null;
        Object IDENTIFIER125_tree=null;
        Object char_literal127_tree=null;
        Object char_literal128_tree=null;
        Object string_literal129_tree=null;
        Object char_literal132_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 26) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:517:5: ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) )
            int alt49=2;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA49_1 = input.LA(2);

                if ( (synpred59()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("516:1: methodDeclaration : ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) );", 49, 1, input);

                    throw nvae;
                }
                }
                break;
            case PUBLIC:
                {
                int LA49_2 = input.LA(2);

                if ( (synpred59()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("516:1: methodDeclaration : ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) );", 49, 2, input);

                    throw nvae;
                }
                }
                break;
            case PROTECTED:
                {
                int LA49_3 = input.LA(2);

                if ( (synpred59()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("516:1: methodDeclaration : ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) );", 49, 3, input);

                    throw nvae;
                }
                }
                break;
            case PRIVATE:
                {
                int LA49_4 = input.LA(2);

                if ( (synpred59()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("516:1: methodDeclaration : ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) );", 49, 4, input);

                    throw nvae;
                }
                }
                break;
            case STATIC:
                {
                int LA49_5 = input.LA(2);

                if ( (synpred59()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("516:1: methodDeclaration : ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) );", 49, 5, input);

                    throw nvae;
                }
                }
                break;
            case ABSTRACT:
                {
                int LA49_6 = input.LA(2);

                if ( (synpred59()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("516:1: methodDeclaration : ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) );", 49, 6, input);

                    throw nvae;
                }
                }
                break;
            case FINAL:
                {
                int LA49_7 = input.LA(2);

                if ( (synpred59()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("516:1: methodDeclaration : ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) );", 49, 7, input);

                    throw nvae;
                }
                }
                break;
            case NATIVE:
                {
                int LA49_8 = input.LA(2);

                if ( (synpred59()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("516:1: methodDeclaration : ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) );", 49, 8, input);

                    throw nvae;
                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA49_9 = input.LA(2);

                if ( (synpred59()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("516:1: methodDeclaration : ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) );", 49, 9, input);

                    throw nvae;
                }
                }
                break;
            case TRANSIENT:
                {
                int LA49_10 = input.LA(2);

                if ( (synpred59()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("516:1: methodDeclaration : ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) );", 49, 10, input);

                    throw nvae;
                }
                }
                break;
            case VOLATILE:
                {
                int LA49_11 = input.LA(2);

                if ( (synpred59()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("516:1: methodDeclaration : ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) );", 49, 11, input);

                    throw nvae;
                }
                }
                break;
            case STRICTFP:
                {
                int LA49_12 = input.LA(2);

                if ( (synpred59()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("516:1: methodDeclaration : ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) );", 49, 12, input);

                    throw nvae;
                }
                }
                break;
            case LT:
                {
                int LA49_13 = input.LA(2);

                if ( (synpred59()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("516:1: methodDeclaration : ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) );", 49, 13, input);

                    throw nvae;
                }
                }
                break;
            case IDENTIFIER:
                {
                int LA49_14 = input.LA(2);

                if ( (synpred59()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("516:1: methodDeclaration : ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) );", 49, 14, input);

                    throw nvae;
                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
            case VOID:
                {
                alt49=2;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("516:1: methodDeclaration : ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) );", 49, 0, input);

                throw nvae;
            }

            switch (alt49) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:519:10: modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_modifiers_in_methodDeclaration1810);
                    modifiers111=modifiers();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, modifiers111.getTree());
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:520:9: ( typeParameters )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);

                    if ( (LA40_0==LT) ) {
                        alt40=1;
                    }
                    switch (alt40) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:520:10: typeParameters
                            {
                            pushFollow(FOLLOW_typeParameters_in_methodDeclaration1821);
                            typeParameters112=typeParameters();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, typeParameters112.getTree());

                            }
                            break;

                    }

                    IDENTIFIER113=(Token)input.LT(1);
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodDeclaration1842); if (failed) return retval;
                    if ( backtracking==0 ) {
                    IDENTIFIER113_tree = (Object)adaptor.create(IDENTIFIER113);
                    adaptor.addChild(root_0, IDENTIFIER113_tree);
                    }
                    pushFollow(FOLLOW_formalParameters_in_methodDeclaration1852);
                    formalParameters114=formalParameters();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, formalParameters114.getTree());
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:524:9: ( 'throws' qualifiedNameList )?
                    int alt41=2;
                    int LA41_0 = input.LA(1);

                    if ( (LA41_0==THROWS) ) {
                        alt41=1;
                    }
                    switch (alt41) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:524:10: 'throws' qualifiedNameList
                            {
                            string_literal115=(Token)input.LT(1);
                            match(input,THROWS,FOLLOW_THROWS_in_methodDeclaration1863); if (failed) return retval;
                            if ( backtracking==0 ) {
                            string_literal115_tree = (Object)adaptor.create(string_literal115);
                            adaptor.addChild(root_0, string_literal115_tree);
                            }
                            pushFollow(FOLLOW_qualifiedNameList_in_methodDeclaration1865);
                            qualifiedNameList116=qualifiedNameList();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList116.getTree());

                            }
                            break;

                    }

                    char_literal117=(Token)input.LT(1);
                    match(input,LBRACE,FOLLOW_LBRACE_in_methodDeclaration1886); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal117_tree = (Object)adaptor.create(char_literal117);
                    adaptor.addChild(root_0, char_literal117_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:527:9: ( explicitConstructorInvocation )?
                    int alt42=2;
                    switch ( input.LA(1) ) {
                        case LT:
                            {
                            alt42=1;
                            }
                            break;
                        case THIS:
                            {
                            int LA42_2 = input.LA(2);

                            if ( (synpred57()) ) {
                                alt42=1;
                            }
                            }
                            break;
                        case LPAREN:
                            {
                            int LA42_3 = input.LA(2);

                            if ( (synpred57()) ) {
                                alt42=1;
                            }
                            }
                            break;
                        case SUPER:
                            {
                            int LA42_4 = input.LA(2);

                            if ( (synpred57()) ) {
                                alt42=1;
                            }
                            }
                            break;
                        case IDENTIFIER:
                            {
                            int LA42_5 = input.LA(2);

                            if ( (synpred57()) ) {
                                alt42=1;
                            }
                            }
                            break;
                        case INTLITERAL:
                        case LONGLITERAL:
                        case FLOATLITERAL:
                        case DOUBLELITERAL:
                        case CHARLITERAL:
                        case STRINGLITERAL:
                        case TRUE:
                        case FALSE:
                        case NULL:
                            {
                            int LA42_6 = input.LA(2);

                            if ( (synpred57()) ) {
                                alt42=1;
                            }
                            }
                            break;
                        case NEW:
                            {
                            int LA42_7 = input.LA(2);

                            if ( (synpred57()) ) {
                                alt42=1;
                            }
                            }
                            break;
                        case BOOLEAN:
                        case BYTE:
                        case CHAR:
                        case DOUBLE:
                        case FLOAT:
                        case INT:
                        case LONG:
                        case SHORT:
                            {
                            int LA42_8 = input.LA(2);

                            if ( (synpred57()) ) {
                                alt42=1;
                            }
                            }
                            break;
                        case VOID:
                            {
                            int LA42_9 = input.LA(2);

                            if ( (synpred57()) ) {
                                alt42=1;
                            }
                            }
                            break;
                    }

                    switch (alt42) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:527:10: explicitConstructorInvocation
                            {
                            pushFollow(FOLLOW_explicitConstructorInvocation_in_methodDeclaration1898);
                            explicitConstructorInvocation118=explicitConstructorInvocation();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, explicitConstructorInvocation118.getTree());

                            }
                            break;

                    }

                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:529:9: ( blockStatement )*
                    loop43:
                    do {
                        int alt43=2;
                        int LA43_0 = input.LA(1);

                        if ( ((LA43_0>=IDENTIFIER && LA43_0<=NULL)||(LA43_0>=ABSTRACT && LA43_0<=BYTE)||(LA43_0>=CHAR && LA43_0<=CLASS)||LA43_0==CONTINUE||(LA43_0>=DO && LA43_0<=DOUBLE)||LA43_0==ENUM||LA43_0==FINAL||(LA43_0>=FLOAT && LA43_0<=FOR)||LA43_0==IF||(LA43_0>=INT && LA43_0<=NEW)||(LA43_0>=PRIVATE && LA43_0<=THROW)||(LA43_0>=TRANSIENT && LA43_0<=LPAREN)||LA43_0==LBRACE||LA43_0==SEMI||(LA43_0>=BANG && LA43_0<=TILDE)||(LA43_0>=PLUSPLUS && LA43_0<=SUB)||LA43_0==MONKEYS_AT) ) {
                            alt43=1;
                        }


                        switch (alt43) {
                    	case 1 :
                    	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:529:10: blockStatement
                    	    {
                    	    pushFollow(FOLLOW_blockStatement_in_methodDeclaration1920);
                    	    blockStatement119=blockStatement();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) adaptor.addChild(root_0, blockStatement119.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop43;
                        }
                    } while (true);

                    char_literal120=(Token)input.LT(1);
                    match(input,RBRACE,FOLLOW_RBRACE_in_methodDeclaration1941); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal120_tree = (Object)adaptor.create(char_literal120);
                    adaptor.addChild(root_0, char_literal120_tree);
                    }

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:532:9: modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' )
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_modifiers_in_methodDeclaration1951);
                    modifiers121=modifiers();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, modifiers121.getTree());
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:533:9: ( typeParameters )?
                    int alt44=2;
                    int LA44_0 = input.LA(1);

                    if ( (LA44_0==LT) ) {
                        alt44=1;
                    }
                    switch (alt44) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:533:10: typeParameters
                            {
                            pushFollow(FOLLOW_typeParameters_in_methodDeclaration1962);
                            typeParameters122=typeParameters();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, typeParameters122.getTree());

                            }
                            break;

                    }

                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:535:9: ( type | 'void' )
                    int alt45=2;
                    int LA45_0 = input.LA(1);

                    if ( (LA45_0==IDENTIFIER||LA45_0==BOOLEAN||LA45_0==BYTE||LA45_0==CHAR||LA45_0==DOUBLE||LA45_0==FLOAT||LA45_0==INT||LA45_0==LONG||LA45_0==SHORT) ) {
                        alt45=1;
                    }
                    else if ( (LA45_0==VOID) ) {
                        alt45=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("535:9: ( type | 'void' )", 45, 0, input);

                        throw nvae;
                    }
                    switch (alt45) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:535:10: type
                            {
                            pushFollow(FOLLOW_type_in_methodDeclaration1984);
                            type123=type();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, type123.getTree());

                            }
                            break;
                        case 2 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:536:13: 'void'
                            {
                            string_literal124=(Token)input.LT(1);
                            match(input,VOID,FOLLOW_VOID_in_methodDeclaration1998); if (failed) return retval;
                            if ( backtracking==0 ) {
                            string_literal124_tree = (Object)adaptor.create(string_literal124);
                            adaptor.addChild(root_0, string_literal124_tree);
                            }

                            }
                            break;

                    }

                    IDENTIFIER125=(Token)input.LT(1);
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodDeclaration2018); if (failed) return retval;
                    if ( backtracking==0 ) {
                    IDENTIFIER125_tree = (Object)adaptor.create(IDENTIFIER125);
                    adaptor.addChild(root_0, IDENTIFIER125_tree);
                    }
                    pushFollow(FOLLOW_formalParameters_in_methodDeclaration2028);
                    formalParameters126=formalParameters();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, formalParameters126.getTree());
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:540:9: ( '[' ']' )*
                    loop46:
                    do {
                        int alt46=2;
                        int LA46_0 = input.LA(1);

                        if ( (LA46_0==LBRACKET) ) {
                            alt46=1;
                        }


                        switch (alt46) {
                    	case 1 :
                    	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:540:10: '[' ']'
                    	    {
                    	    char_literal127=(Token)input.LT(1);
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_methodDeclaration2039); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal127_tree = (Object)adaptor.create(char_literal127);
                    	    adaptor.addChild(root_0, char_literal127_tree);
                    	    }
                    	    char_literal128=(Token)input.LT(1);
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_methodDeclaration2041); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal128_tree = (Object)adaptor.create(char_literal128);
                    	    adaptor.addChild(root_0, char_literal128_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop46;
                        }
                    } while (true);

                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:542:9: ( 'throws' qualifiedNameList )?
                    int alt47=2;
                    int LA47_0 = input.LA(1);

                    if ( (LA47_0==THROWS) ) {
                        alt47=1;
                    }
                    switch (alt47) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:542:10: 'throws' qualifiedNameList
                            {
                            string_literal129=(Token)input.LT(1);
                            match(input,THROWS,FOLLOW_THROWS_in_methodDeclaration2063); if (failed) return retval;
                            if ( backtracking==0 ) {
                            string_literal129_tree = (Object)adaptor.create(string_literal129);
                            adaptor.addChild(root_0, string_literal129_tree);
                            }
                            pushFollow(FOLLOW_qualifiedNameList_in_methodDeclaration2065);
                            qualifiedNameList130=qualifiedNameList();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList130.getTree());

                            }
                            break;

                    }

                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:544:9: ( block | ';' )
                    int alt48=2;
                    int LA48_0 = input.LA(1);

                    if ( (LA48_0==LBRACE) ) {
                        alt48=1;
                    }
                    else if ( (LA48_0==SEMI) ) {
                        alt48=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("544:9: ( block | ';' )", 48, 0, input);

                        throw nvae;
                    }
                    switch (alt48) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:545:13: block
                            {
                            pushFollow(FOLLOW_block_in_methodDeclaration2120);
                            block131=block();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, block131.getTree());

                            }
                            break;
                        case 2 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:546:13: ';'
                            {
                            char_literal132=(Token)input.LT(1);
                            match(input,SEMI,FOLLOW_SEMI_in_methodDeclaration2134); if (failed) return retval;
                            if ( backtracking==0 ) {
                            char_literal132_tree = (Object)adaptor.create(char_literal132);
                            adaptor.addChild(root_0, char_literal132_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 26, methodDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end methodDeclaration

    public static class fieldDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start fieldDeclaration
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:551:1: fieldDeclaration : modifiers type variableDeclarator ( ',' variableDeclarator )* ';' ;
    public final fieldDeclaration_return fieldDeclaration() throws RecognitionException {
        fieldDeclaration_return retval = new fieldDeclaration_return();
        retval.start = input.LT(1);
        int fieldDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal136=null;
        Token char_literal138=null;
        modifiers_return modifiers133 = null;

        type_return type134 = null;

        variableDeclarator_return variableDeclarator135 = null;

        variableDeclarator_return variableDeclarator137 = null;


        Object char_literal136_tree=null;
        Object char_literal138_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 27) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:552:5: ( modifiers type variableDeclarator ( ',' variableDeclarator )* ';' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:552:9: modifiers type variableDeclarator ( ',' variableDeclarator )* ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_fieldDeclaration2166);
            modifiers133=modifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, modifiers133.getTree());
            pushFollow(FOLLOW_type_in_fieldDeclaration2176);
            type134=type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, type134.getTree());
            pushFollow(FOLLOW_variableDeclarator_in_fieldDeclaration2186);
            variableDeclarator135=variableDeclarator();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, variableDeclarator135.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:555:9: ( ',' variableDeclarator )*
            loop50:
            do {
                int alt50=2;
                int LA50_0 = input.LA(1);

                if ( (LA50_0==COMMA) ) {
                    alt50=1;
                }


                switch (alt50) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:555:10: ',' variableDeclarator
            	    {
            	    char_literal136=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_fieldDeclaration2197); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal136_tree = (Object)adaptor.create(char_literal136);
            	    adaptor.addChild(root_0, char_literal136_tree);
            	    }
            	    pushFollow(FOLLOW_variableDeclarator_in_fieldDeclaration2199);
            	    variableDeclarator137=variableDeclarator();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, variableDeclarator137.getTree());

            	    }
            	    break;

            	default :
            	    break loop50;
                }
            } while (true);

            char_literal138=(Token)input.LT(1);
            match(input,SEMI,FOLLOW_SEMI_in_fieldDeclaration2220); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal138_tree = (Object)adaptor.create(char_literal138);
            adaptor.addChild(root_0, char_literal138_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 27, fieldDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end fieldDeclaration

    public static class variableDeclarator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start variableDeclarator
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:560:1: variableDeclarator : IDENTIFIER ( '[' ']' )* ( '=' variableInitializer )? ;
    public final variableDeclarator_return variableDeclarator() throws RecognitionException {
        variableDeclarator_return retval = new variableDeclarator_return();
        retval.start = input.LT(1);
        int variableDeclarator_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER139=null;
        Token char_literal140=null;
        Token char_literal141=null;
        Token char_literal142=null;
        variableInitializer_return variableInitializer143 = null;


        Object IDENTIFIER139_tree=null;
        Object char_literal140_tree=null;
        Object char_literal141_tree=null;
        Object char_literal142_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 28) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:561:5: ( IDENTIFIER ( '[' ']' )* ( '=' variableInitializer )? )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:561:9: IDENTIFIER ( '[' ']' )* ( '=' variableInitializer )?
            {
            root_0 = (Object)adaptor.nil();

            IDENTIFIER139=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_variableDeclarator2240); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER139_tree = (Object)adaptor.create(IDENTIFIER139);
            adaptor.addChild(root_0, IDENTIFIER139_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:562:9: ( '[' ']' )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);

                if ( (LA51_0==LBRACKET) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:562:10: '[' ']'
            	    {
            	    char_literal140=(Token)input.LT(1);
            	    match(input,LBRACKET,FOLLOW_LBRACKET_in_variableDeclarator2251); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal140_tree = (Object)adaptor.create(char_literal140);
            	    adaptor.addChild(root_0, char_literal140_tree);
            	    }
            	    char_literal141=(Token)input.LT(1);
            	    match(input,RBRACKET,FOLLOW_RBRACKET_in_variableDeclarator2253); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal141_tree = (Object)adaptor.create(char_literal141);
            	    adaptor.addChild(root_0, char_literal141_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop51;
                }
            } while (true);

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:564:9: ( '=' variableInitializer )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==EQ) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:564:10: '=' variableInitializer
                    {
                    char_literal142=(Token)input.LT(1);
                    match(input,EQ,FOLLOW_EQ_in_variableDeclarator2275); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal142_tree = (Object)adaptor.create(char_literal142);
                    adaptor.addChild(root_0, char_literal142_tree);
                    }
                    pushFollow(FOLLOW_variableInitializer_in_variableDeclarator2277);
                    variableInitializer143=variableInitializer();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, variableInitializer143.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 28, variableDeclarator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end variableDeclarator

    public static class interfaceBodyDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start interfaceBodyDeclaration
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:568:1: interfaceBodyDeclaration : ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' );
    public final interfaceBodyDeclaration_return interfaceBodyDeclaration() throws RecognitionException {
        interfaceBodyDeclaration_return retval = new interfaceBodyDeclaration_return();
        retval.start = input.LT(1);
        int interfaceBodyDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal148=null;
        interfaceFieldDeclaration_return interfaceFieldDeclaration144 = null;

        interfaceMethodDeclaration_return interfaceMethodDeclaration145 = null;

        interfaceDeclaration_return interfaceDeclaration146 = null;

        classDeclaration_return classDeclaration147 = null;


        Object char_literal148_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 29) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:572:5: ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' )
            int alt53=5;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA53_1 = input.LA(2);

                if ( (synpred68()) ) {
                    alt53=1;
                }
                else if ( (synpred69()) ) {
                    alt53=2;
                }
                else if ( (synpred70()) ) {
                    alt53=3;
                }
                else if ( (synpred71()) ) {
                    alt53=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("568:1: interfaceBodyDeclaration : ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' );", 53, 1, input);

                    throw nvae;
                }
                }
                break;
            case PUBLIC:
                {
                int LA53_2 = input.LA(2);

                if ( (synpred68()) ) {
                    alt53=1;
                }
                else if ( (synpred69()) ) {
                    alt53=2;
                }
                else if ( (synpred70()) ) {
                    alt53=3;
                }
                else if ( (synpred71()) ) {
                    alt53=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("568:1: interfaceBodyDeclaration : ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' );", 53, 2, input);

                    throw nvae;
                }
                }
                break;
            case PROTECTED:
                {
                int LA53_3 = input.LA(2);

                if ( (synpred68()) ) {
                    alt53=1;
                }
                else if ( (synpred69()) ) {
                    alt53=2;
                }
                else if ( (synpred70()) ) {
                    alt53=3;
                }
                else if ( (synpred71()) ) {
                    alt53=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("568:1: interfaceBodyDeclaration : ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' );", 53, 3, input);

                    throw nvae;
                }
                }
                break;
            case PRIVATE:
                {
                int LA53_4 = input.LA(2);

                if ( (synpred68()) ) {
                    alt53=1;
                }
                else if ( (synpred69()) ) {
                    alt53=2;
                }
                else if ( (synpred70()) ) {
                    alt53=3;
                }
                else if ( (synpred71()) ) {
                    alt53=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("568:1: interfaceBodyDeclaration : ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' );", 53, 4, input);

                    throw nvae;
                }
                }
                break;
            case STATIC:
                {
                int LA53_5 = input.LA(2);

                if ( (synpred68()) ) {
                    alt53=1;
                }
                else if ( (synpred69()) ) {
                    alt53=2;
                }
                else if ( (synpred70()) ) {
                    alt53=3;
                }
                else if ( (synpred71()) ) {
                    alt53=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("568:1: interfaceBodyDeclaration : ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' );", 53, 5, input);

                    throw nvae;
                }
                }
                break;
            case ABSTRACT:
                {
                int LA53_6 = input.LA(2);

                if ( (synpred68()) ) {
                    alt53=1;
                }
                else if ( (synpred69()) ) {
                    alt53=2;
                }
                else if ( (synpred70()) ) {
                    alt53=3;
                }
                else if ( (synpred71()) ) {
                    alt53=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("568:1: interfaceBodyDeclaration : ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' );", 53, 6, input);

                    throw nvae;
                }
                }
                break;
            case FINAL:
                {
                int LA53_7 = input.LA(2);

                if ( (synpred68()) ) {
                    alt53=1;
                }
                else if ( (synpred69()) ) {
                    alt53=2;
                }
                else if ( (synpred70()) ) {
                    alt53=3;
                }
                else if ( (synpred71()) ) {
                    alt53=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("568:1: interfaceBodyDeclaration : ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' );", 53, 7, input);

                    throw nvae;
                }
                }
                break;
            case NATIVE:
                {
                int LA53_8 = input.LA(2);

                if ( (synpred68()) ) {
                    alt53=1;
                }
                else if ( (synpred69()) ) {
                    alt53=2;
                }
                else if ( (synpred70()) ) {
                    alt53=3;
                }
                else if ( (synpred71()) ) {
                    alt53=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("568:1: interfaceBodyDeclaration : ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' );", 53, 8, input);

                    throw nvae;
                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA53_9 = input.LA(2);

                if ( (synpred68()) ) {
                    alt53=1;
                }
                else if ( (synpred69()) ) {
                    alt53=2;
                }
                else if ( (synpred70()) ) {
                    alt53=3;
                }
                else if ( (synpred71()) ) {
                    alt53=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("568:1: interfaceBodyDeclaration : ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' );", 53, 9, input);

                    throw nvae;
                }
                }
                break;
            case TRANSIENT:
                {
                int LA53_10 = input.LA(2);

                if ( (synpred68()) ) {
                    alt53=1;
                }
                else if ( (synpred69()) ) {
                    alt53=2;
                }
                else if ( (synpred70()) ) {
                    alt53=3;
                }
                else if ( (synpred71()) ) {
                    alt53=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("568:1: interfaceBodyDeclaration : ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' );", 53, 10, input);

                    throw nvae;
                }
                }
                break;
            case VOLATILE:
                {
                int LA53_11 = input.LA(2);

                if ( (synpred68()) ) {
                    alt53=1;
                }
                else if ( (synpred69()) ) {
                    alt53=2;
                }
                else if ( (synpred70()) ) {
                    alt53=3;
                }
                else if ( (synpred71()) ) {
                    alt53=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("568:1: interfaceBodyDeclaration : ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' );", 53, 11, input);

                    throw nvae;
                }
                }
                break;
            case STRICTFP:
                {
                int LA53_12 = input.LA(2);

                if ( (synpred68()) ) {
                    alt53=1;
                }
                else if ( (synpred69()) ) {
                    alt53=2;
                }
                else if ( (synpred70()) ) {
                    alt53=3;
                }
                else if ( (synpred71()) ) {
                    alt53=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("568:1: interfaceBodyDeclaration : ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' );", 53, 12, input);

                    throw nvae;
                }
                }
                break;
            case IDENTIFIER:
                {
                int LA53_13 = input.LA(2);

                if ( (synpred68()) ) {
                    alt53=1;
                }
                else if ( (synpred69()) ) {
                    alt53=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("568:1: interfaceBodyDeclaration : ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' );", 53, 13, input);

                    throw nvae;
                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                {
                int LA53_14 = input.LA(2);

                if ( (synpred68()) ) {
                    alt53=1;
                }
                else if ( (synpred69()) ) {
                    alt53=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("568:1: interfaceBodyDeclaration : ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' );", 53, 14, input);

                    throw nvae;
                }
                }
                break;
            case VOID:
            case LT:
                {
                alt53=2;
                }
                break;
            case INTERFACE:
                {
                alt53=3;
                }
                break;
            case CLASS:
            case ENUM:
                {
                alt53=4;
                }
                break;
            case SEMI:
                {
                alt53=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("568:1: interfaceBodyDeclaration : ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' );", 53, 0, input);

                throw nvae;
            }

            switch (alt53) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:573:9: interfaceFieldDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceFieldDeclaration_in_interfaceBodyDeclaration2316);
                    interfaceFieldDeclaration144=interfaceFieldDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, interfaceFieldDeclaration144.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:574:9: interfaceMethodDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceMethodDeclaration_in_interfaceBodyDeclaration2326);
                    interfaceMethodDeclaration145=interfaceMethodDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, interfaceMethodDeclaration145.getTree());

                    }
                    break;
                case 3 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:575:9: interfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceDeclaration_in_interfaceBodyDeclaration2336);
                    interfaceDeclaration146=interfaceDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, interfaceDeclaration146.getTree());

                    }
                    break;
                case 4 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:576:9: classDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classDeclaration_in_interfaceBodyDeclaration2346);
                    classDeclaration147=classDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, classDeclaration147.getTree());

                    }
                    break;
                case 5 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:577:9: ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal148=(Token)input.LT(1);
                    match(input,SEMI,FOLLOW_SEMI_in_interfaceBodyDeclaration2356); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal148_tree = (Object)adaptor.create(char_literal148);
                    adaptor.addChild(root_0, char_literal148_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 29, interfaceBodyDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end interfaceBodyDeclaration

    public static class interfaceMethodDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start interfaceMethodDeclaration
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:580:1: interfaceMethodDeclaration : modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' ;
    public final interfaceMethodDeclaration_return interfaceMethodDeclaration() throws RecognitionException {
        interfaceMethodDeclaration_return retval = new interfaceMethodDeclaration_return();
        retval.start = input.LT(1);
        int interfaceMethodDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal152=null;
        Token IDENTIFIER153=null;
        Token char_literal155=null;
        Token char_literal156=null;
        Token string_literal157=null;
        Token char_literal159=null;
        modifiers_return modifiers149 = null;

        typeParameters_return typeParameters150 = null;

        type_return type151 = null;

        formalParameters_return formalParameters154 = null;

        qualifiedNameList_return qualifiedNameList158 = null;


        Object string_literal152_tree=null;
        Object IDENTIFIER153_tree=null;
        Object char_literal155_tree=null;
        Object char_literal156_tree=null;
        Object string_literal157_tree=null;
        Object char_literal159_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 30) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:581:5: ( modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:581:9: modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_interfaceMethodDeclaration2376);
            modifiers149=modifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, modifiers149.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:582:9: ( typeParameters )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==LT) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:582:10: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_interfaceMethodDeclaration2387);
                    typeParameters150=typeParameters();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, typeParameters150.getTree());

                    }
                    break;

            }

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:584:9: ( type | 'void' )
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==IDENTIFIER||LA55_0==BOOLEAN||LA55_0==BYTE||LA55_0==CHAR||LA55_0==DOUBLE||LA55_0==FLOAT||LA55_0==INT||LA55_0==LONG||LA55_0==SHORT) ) {
                alt55=1;
            }
            else if ( (LA55_0==VOID) ) {
                alt55=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("584:9: ( type | 'void' )", 55, 0, input);

                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:584:10: type
                    {
                    pushFollow(FOLLOW_type_in_interfaceMethodDeclaration2409);
                    type151=type();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, type151.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:585:10: 'void'
                    {
                    string_literal152=(Token)input.LT(1);
                    match(input,VOID,FOLLOW_VOID_in_interfaceMethodDeclaration2420); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal152_tree = (Object)adaptor.create(string_literal152);
                    adaptor.addChild(root_0, string_literal152_tree);
                    }

                    }
                    break;

            }

            IDENTIFIER153=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_interfaceMethodDeclaration2440); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER153_tree = (Object)adaptor.create(IDENTIFIER153);
            adaptor.addChild(root_0, IDENTIFIER153_tree);
            }
            pushFollow(FOLLOW_formalParameters_in_interfaceMethodDeclaration2450);
            formalParameters154=formalParameters();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, formalParameters154.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:589:9: ( '[' ']' )*
            loop56:
            do {
                int alt56=2;
                int LA56_0 = input.LA(1);

                if ( (LA56_0==LBRACKET) ) {
                    alt56=1;
                }


                switch (alt56) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:589:10: '[' ']'
            	    {
            	    char_literal155=(Token)input.LT(1);
            	    match(input,LBRACKET,FOLLOW_LBRACKET_in_interfaceMethodDeclaration2461); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal155_tree = (Object)adaptor.create(char_literal155);
            	    adaptor.addChild(root_0, char_literal155_tree);
            	    }
            	    char_literal156=(Token)input.LT(1);
            	    match(input,RBRACKET,FOLLOW_RBRACKET_in_interfaceMethodDeclaration2463); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal156_tree = (Object)adaptor.create(char_literal156);
            	    adaptor.addChild(root_0, char_literal156_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop56;
                }
            } while (true);

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:591:9: ( 'throws' qualifiedNameList )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==THROWS) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:591:10: 'throws' qualifiedNameList
                    {
                    string_literal157=(Token)input.LT(1);
                    match(input,THROWS,FOLLOW_THROWS_in_interfaceMethodDeclaration2485); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal157_tree = (Object)adaptor.create(string_literal157);
                    adaptor.addChild(root_0, string_literal157_tree);
                    }
                    pushFollow(FOLLOW_qualifiedNameList_in_interfaceMethodDeclaration2487);
                    qualifiedNameList158=qualifiedNameList();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList158.getTree());

                    }
                    break;

            }

            char_literal159=(Token)input.LT(1);
            match(input,SEMI,FOLLOW_SEMI_in_interfaceMethodDeclaration2500); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal159_tree = (Object)adaptor.create(char_literal159);
            adaptor.addChild(root_0, char_literal159_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 30, interfaceMethodDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end interfaceMethodDeclaration

    public static class interfaceFieldDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start interfaceFieldDeclaration
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:595:1: interfaceFieldDeclaration : modifiers type variableDeclarator ( ',' variableDeclarator )* ';' ;
    public final interfaceFieldDeclaration_return interfaceFieldDeclaration() throws RecognitionException {
        interfaceFieldDeclaration_return retval = new interfaceFieldDeclaration_return();
        retval.start = input.LT(1);
        int interfaceFieldDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal163=null;
        Token char_literal165=null;
        modifiers_return modifiers160 = null;

        type_return type161 = null;

        variableDeclarator_return variableDeclarator162 = null;

        variableDeclarator_return variableDeclarator164 = null;


        Object char_literal163_tree=null;
        Object char_literal165_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 31) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:601:5: ( modifiers type variableDeclarator ( ',' variableDeclarator )* ';' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:601:9: modifiers type variableDeclarator ( ',' variableDeclarator )* ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_interfaceFieldDeclaration2522);
            modifiers160=modifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, modifiers160.getTree());
            pushFollow(FOLLOW_type_in_interfaceFieldDeclaration2524);
            type161=type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, type161.getTree());
            pushFollow(FOLLOW_variableDeclarator_in_interfaceFieldDeclaration2526);
            variableDeclarator162=variableDeclarator();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, variableDeclarator162.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:602:9: ( ',' variableDeclarator )*
            loop58:
            do {
                int alt58=2;
                int LA58_0 = input.LA(1);

                if ( (LA58_0==COMMA) ) {
                    alt58=1;
                }


                switch (alt58) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:602:10: ',' variableDeclarator
            	    {
            	    char_literal163=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_interfaceFieldDeclaration2537); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal163_tree = (Object)adaptor.create(char_literal163);
            	    adaptor.addChild(root_0, char_literal163_tree);
            	    }
            	    pushFollow(FOLLOW_variableDeclarator_in_interfaceFieldDeclaration2539);
            	    variableDeclarator164=variableDeclarator();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, variableDeclarator164.getTree());

            	    }
            	    break;

            	default :
            	    break loop58;
                }
            } while (true);

            char_literal165=(Token)input.LT(1);
            match(input,SEMI,FOLLOW_SEMI_in_interfaceFieldDeclaration2560); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal165_tree = (Object)adaptor.create(char_literal165);
            adaptor.addChild(root_0, char_literal165_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 31, interfaceFieldDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end interfaceFieldDeclaration

    public static class type_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start type
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:608:1: type : ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* );
    public final type_return type() throws RecognitionException {
        type_return retval = new type_return();
        retval.start = input.LT(1);
        int type_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal167=null;
        Token char_literal168=null;
        Token char_literal170=null;
        Token char_literal171=null;
        classOrInterfaceType_return classOrInterfaceType166 = null;

        primitiveType_return primitiveType169 = null;


        Object char_literal167_tree=null;
        Object char_literal168_tree=null;
        Object char_literal170_tree=null;
        Object char_literal171_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 32) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:609:5: ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* )
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==IDENTIFIER) ) {
                alt61=1;
            }
            else if ( (LA61_0==BOOLEAN||LA61_0==BYTE||LA61_0==CHAR||LA61_0==DOUBLE||LA61_0==FLOAT||LA61_0==INT||LA61_0==LONG||LA61_0==SHORT) ) {
                alt61=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("608:1: type : ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* );", 61, 0, input);

                throw nvae;
            }
            switch (alt61) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:609:9: classOrInterfaceType ( '[' ']' )*
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceType_in_type2581);
                    classOrInterfaceType166=classOrInterfaceType();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceType166.getTree());
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:610:9: ( '[' ']' )*
                    loop59:
                    do {
                        int alt59=2;
                        int LA59_0 = input.LA(1);

                        if ( (LA59_0==LBRACKET) ) {
                            alt59=1;
                        }


                        switch (alt59) {
                    	case 1 :
                    	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:610:10: '[' ']'
                    	    {
                    	    char_literal167=(Token)input.LT(1);
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_type2592); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal167_tree = (Object)adaptor.create(char_literal167);
                    	    adaptor.addChild(root_0, char_literal167_tree);
                    	    }
                    	    char_literal168=(Token)input.LT(1);
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_type2594); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal168_tree = (Object)adaptor.create(char_literal168);
                    	    adaptor.addChild(root_0, char_literal168_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop59;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:612:9: primitiveType ( '[' ']' )*
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_type2615);
                    primitiveType169=primitiveType();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, primitiveType169.getTree());
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:613:9: ( '[' ']' )*
                    loop60:
                    do {
                        int alt60=2;
                        int LA60_0 = input.LA(1);

                        if ( (LA60_0==LBRACKET) ) {
                            alt60=1;
                        }


                        switch (alt60) {
                    	case 1 :
                    	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:613:10: '[' ']'
                    	    {
                    	    char_literal170=(Token)input.LT(1);
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_type2626); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal170_tree = (Object)adaptor.create(char_literal170);
                    	    adaptor.addChild(root_0, char_literal170_tree);
                    	    }
                    	    char_literal171=(Token)input.LT(1);
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_type2628); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal171_tree = (Object)adaptor.create(char_literal171);
                    	    adaptor.addChild(root_0, char_literal171_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop60;
                        }
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 32, type_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end type

    public static class classOrInterfaceType_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start classOrInterfaceType
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:618:1: classOrInterfaceType : IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )* ;
    public final classOrInterfaceType_return classOrInterfaceType() throws RecognitionException {
        classOrInterfaceType_return retval = new classOrInterfaceType_return();
        retval.start = input.LT(1);
        int classOrInterfaceType_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER172=null;
        Token char_literal174=null;
        Token IDENTIFIER175=null;
        typeArguments_return typeArguments173 = null;

        typeArguments_return typeArguments176 = null;


        Object IDENTIFIER172_tree=null;
        Object char_literal174_tree=null;
        Object IDENTIFIER175_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 33) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:619:5: ( IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:619:9: IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )*
            {
            root_0 = (Object)adaptor.nil();

            IDENTIFIER172=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classOrInterfaceType2660); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER172_tree = (Object)adaptor.create(IDENTIFIER172);
            adaptor.addChild(root_0, IDENTIFIER172_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:620:9: ( typeArguments )?
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==LT) ) {
                int LA62_1 = input.LA(2);

                if ( (LA62_1==IDENTIFIER||LA62_1==BOOLEAN||LA62_1==BYTE||LA62_1==CHAR||LA62_1==DOUBLE||LA62_1==FLOAT||LA62_1==INT||LA62_1==LONG||LA62_1==SHORT||LA62_1==QUES) ) {
                    alt62=1;
                }
            }
            switch (alt62) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:620:10: typeArguments
                    {
                    pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType2671);
                    typeArguments173=typeArguments();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, typeArguments173.getTree());

                    }
                    break;

            }

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:622:9: ( '.' IDENTIFIER ( typeArguments )? )*
            loop64:
            do {
                int alt64=2;
                int LA64_0 = input.LA(1);

                if ( (LA64_0==DOT) ) {
                    alt64=1;
                }


                switch (alt64) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:622:10: '.' IDENTIFIER ( typeArguments )?
            	    {
            	    char_literal174=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_classOrInterfaceType2693); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal174_tree = (Object)adaptor.create(char_literal174);
            	    adaptor.addChild(root_0, char_literal174_tree);
            	    }
            	    IDENTIFIER175=(Token)input.LT(1);
            	    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classOrInterfaceType2695); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    IDENTIFIER175_tree = (Object)adaptor.create(IDENTIFIER175);
            	    adaptor.addChild(root_0, IDENTIFIER175_tree);
            	    }
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:623:13: ( typeArguments )?
            	    int alt63=2;
            	    int LA63_0 = input.LA(1);

            	    if ( (LA63_0==LT) ) {
            	        int LA63_1 = input.LA(2);

            	        if ( (LA63_1==IDENTIFIER||LA63_1==BOOLEAN||LA63_1==BYTE||LA63_1==CHAR||LA63_1==DOUBLE||LA63_1==FLOAT||LA63_1==INT||LA63_1==LONG||LA63_1==SHORT||LA63_1==QUES) ) {
            	            alt63=1;
            	        }
            	    }
            	    switch (alt63) {
            	        case 1 :
            	            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:623:14: typeArguments
            	            {
            	            pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType2710);
            	            typeArguments176=typeArguments();
            	            _fsp--;
            	            if (failed) return retval;
            	            if ( backtracking==0 ) adaptor.addChild(root_0, typeArguments176.getTree());

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop64;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 33, classOrInterfaceType_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end classOrInterfaceType

    public static class primitiveType_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start primitiveType
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:628:1: primitiveType : ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' );
    public final primitiveType_return primitiveType() throws RecognitionException {
        primitiveType_return retval = new primitiveType_return();
        retval.start = input.LT(1);
        int primitiveType_StartIndex = input.index();
        Object root_0 = null;

        Token set177=null;

        Object set177_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 34) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:629:5: ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:
            {
            root_0 = (Object)adaptor.nil();

            set177=(Token)input.LT(1);
            if ( input.LA(1)==BOOLEAN||input.LA(1)==BYTE||input.LA(1)==CHAR||input.LA(1)==DOUBLE||input.LA(1)==FLOAT||input.LA(1)==INT||input.LA(1)==LONG||input.LA(1)==SHORT ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set177));
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_primitiveType0);    throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 34, primitiveType_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end primitiveType

    public static class typeArguments_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start typeArguments
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:639:1: typeArguments : '<' typeArgument ( ',' typeArgument )* '>' ;
    public final typeArguments_return typeArguments() throws RecognitionException {
        typeArguments_return retval = new typeArguments_return();
        retval.start = input.LT(1);
        int typeArguments_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal178=null;
        Token char_literal180=null;
        Token char_literal182=null;
        typeArgument_return typeArgument179 = null;

        typeArgument_return typeArgument181 = null;


        Object char_literal178_tree=null;
        Object char_literal180_tree=null;
        Object char_literal182_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 35) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:640:5: ( '<' typeArgument ( ',' typeArgument )* '>' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:640:9: '<' typeArgument ( ',' typeArgument )* '>'
            {
            root_0 = (Object)adaptor.nil();

            char_literal178=(Token)input.LT(1);
            match(input,LT,FOLLOW_LT_in_typeArguments2847); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal178_tree = (Object)adaptor.create(char_literal178);
            adaptor.addChild(root_0, char_literal178_tree);
            }
            pushFollow(FOLLOW_typeArgument_in_typeArguments2849);
            typeArgument179=typeArgument();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, typeArgument179.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:641:9: ( ',' typeArgument )*
            loop65:
            do {
                int alt65=2;
                int LA65_0 = input.LA(1);

                if ( (LA65_0==COMMA) ) {
                    alt65=1;
                }


                switch (alt65) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:641:10: ',' typeArgument
            	    {
            	    char_literal180=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_typeArguments2860); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal180_tree = (Object)adaptor.create(char_literal180);
            	    adaptor.addChild(root_0, char_literal180_tree);
            	    }
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments2862);
            	    typeArgument181=typeArgument();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, typeArgument181.getTree());

            	    }
            	    break;

            	default :
            	    break loop65;
                }
            } while (true);

            char_literal182=(Token)input.LT(1);
            match(input,GT,FOLLOW_GT_in_typeArguments2884); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal182_tree = (Object)adaptor.create(char_literal182);
            adaptor.addChild(root_0, char_literal182_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 35, typeArguments_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end typeArguments

    public static class typeArgument_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start typeArgument
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:646:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );
    public final typeArgument_return typeArgument() throws RecognitionException {
        typeArgument_return retval = new typeArgument_return();
        retval.start = input.LT(1);
        int typeArgument_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal184=null;
        Token set185=null;
        type_return type183 = null;

        type_return type186 = null;


        Object char_literal184_tree=null;
        Object set185_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 36) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:647:5: ( type | '?' ( ( 'extends' | 'super' ) type )? )
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==IDENTIFIER||LA67_0==BOOLEAN||LA67_0==BYTE||LA67_0==CHAR||LA67_0==DOUBLE||LA67_0==FLOAT||LA67_0==INT||LA67_0==LONG||LA67_0==SHORT) ) {
                alt67=1;
            }
            else if ( (LA67_0==QUES) ) {
                alt67=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("646:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );", 67, 0, input);

                throw nvae;
            }
            switch (alt67) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:647:9: type
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_type_in_typeArgument2904);
                    type183=type();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, type183.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:648:9: '?' ( ( 'extends' | 'super' ) type )?
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal184=(Token)input.LT(1);
                    match(input,QUES,FOLLOW_QUES_in_typeArgument2914); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal184_tree = (Object)adaptor.create(char_literal184);
                    adaptor.addChild(root_0, char_literal184_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:649:9: ( ( 'extends' | 'super' ) type )?
                    int alt66=2;
                    int LA66_0 = input.LA(1);

                    if ( (LA66_0==EXTENDS||LA66_0==SUPER) ) {
                        alt66=1;
                    }
                    switch (alt66) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:650:13: ( 'extends' | 'super' ) type
                            {
                            set185=(Token)input.LT(1);
                            if ( input.LA(1)==EXTENDS||input.LA(1)==SUPER ) {
                                input.consume();
                                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set185));
                                errorRecovery=false;failed=false;
                            }
                            else {
                                if (backtracking>0) {failed=true; return retval;}
                                MismatchedSetException mse =
                                    new MismatchedSetException(null,input);
                                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_typeArgument2938);    throw mse;
                            }

                            pushFollow(FOLLOW_type_in_typeArgument2982);
                            type186=type();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, type186.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 36, typeArgument_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end typeArgument

    public static class qualifiedNameList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start qualifiedNameList
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:657:1: qualifiedNameList : qualifiedName ( ',' qualifiedName )* ;
    public final qualifiedNameList_return qualifiedNameList() throws RecognitionException {
        qualifiedNameList_return retval = new qualifiedNameList_return();
        retval.start = input.LT(1);
        int qualifiedNameList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal188=null;
        qualifiedName_return qualifiedName187 = null;

        qualifiedName_return qualifiedName189 = null;


        Object char_literal188_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 37) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:658:5: ( qualifiedName ( ',' qualifiedName )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:658:9: qualifiedName ( ',' qualifiedName )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList3013);
            qualifiedName187=qualifiedName();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, qualifiedName187.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:659:9: ( ',' qualifiedName )*
            loop68:
            do {
                int alt68=2;
                int LA68_0 = input.LA(1);

                if ( (LA68_0==COMMA) ) {
                    alt68=1;
                }


                switch (alt68) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:659:10: ',' qualifiedName
            	    {
            	    char_literal188=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_qualifiedNameList3024); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal188_tree = (Object)adaptor.create(char_literal188);
            	    adaptor.addChild(root_0, char_literal188_tree);
            	    }
            	    pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList3026);
            	    qualifiedName189=qualifiedName();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, qualifiedName189.getTree());

            	    }
            	    break;

            	default :
            	    break loop68;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 37, qualifiedNameList_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end qualifiedNameList

    public static class formalParameters_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start formalParameters
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:663:1: formalParameters : '(' ( formalParameterDecls )? ')' ;
    public final formalParameters_return formalParameters() throws RecognitionException {
        formalParameters_return retval = new formalParameters_return();
        retval.start = input.LT(1);
        int formalParameters_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal190=null;
        Token char_literal192=null;
        formalParameterDecls_return formalParameterDecls191 = null;


        Object char_literal190_tree=null;
        Object char_literal192_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 38) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:664:5: ( '(' ( formalParameterDecls )? ')' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:664:9: '(' ( formalParameterDecls )? ')'
            {
            root_0 = (Object)adaptor.nil();

            char_literal190=(Token)input.LT(1);
            match(input,LPAREN,FOLLOW_LPAREN_in_formalParameters3057); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal190_tree = (Object)adaptor.create(char_literal190);
            adaptor.addChild(root_0, char_literal190_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:665:9: ( formalParameterDecls )?
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==IDENTIFIER||LA69_0==BOOLEAN||LA69_0==BYTE||LA69_0==CHAR||LA69_0==DOUBLE||LA69_0==FINAL||LA69_0==FLOAT||LA69_0==INT||LA69_0==LONG||LA69_0==SHORT||LA69_0==MONKEYS_AT) ) {
                alt69=1;
            }
            switch (alt69) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:665:10: formalParameterDecls
                    {
                    pushFollow(FOLLOW_formalParameterDecls_in_formalParameters3068);
                    formalParameterDecls191=formalParameterDecls();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, formalParameterDecls191.getTree());

                    }
                    break;

            }

            char_literal192=(Token)input.LT(1);
            match(input,RPAREN,FOLLOW_RPAREN_in_formalParameters3090); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal192_tree = (Object)adaptor.create(char_literal192);
            adaptor.addChild(root_0, char_literal192_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 38, formalParameters_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end formalParameters

    public static class formalParameterDecls_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start formalParameterDecls
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:670:1: formalParameterDecls : ( ellipsisParameterDecl | normalParameterDecl ( ',' normalParameterDecl )* | ( normalParameterDecl ',' )+ ellipsisParameterDecl );
    public final formalParameterDecls_return formalParameterDecls() throws RecognitionException {
        formalParameterDecls_return retval = new formalParameterDecls_return();
        retval.start = input.LT(1);
        int formalParameterDecls_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal195=null;
        Token char_literal198=null;
        ellipsisParameterDecl_return ellipsisParameterDecl193 = null;

        normalParameterDecl_return normalParameterDecl194 = null;

        normalParameterDecl_return normalParameterDecl196 = null;

        normalParameterDecl_return normalParameterDecl197 = null;

        ellipsisParameterDecl_return ellipsisParameterDecl199 = null;


        Object char_literal195_tree=null;
        Object char_literal198_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 39) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:671:5: ( ellipsisParameterDecl | normalParameterDecl ( ',' normalParameterDecl )* | ( normalParameterDecl ',' )+ ellipsisParameterDecl )
            int alt72=3;
            switch ( input.LA(1) ) {
            case FINAL:
                {
                int LA72_1 = input.LA(2);

                if ( (synpred96()) ) {
                    alt72=1;
                }
                else if ( (synpred98()) ) {
                    alt72=2;
                }
                else if ( (true) ) {
                    alt72=3;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("670:1: formalParameterDecls : ( ellipsisParameterDecl | normalParameterDecl ( ',' normalParameterDecl )* | ( normalParameterDecl ',' )+ ellipsisParameterDecl );", 72, 1, input);

                    throw nvae;
                }
                }
                break;
            case MONKEYS_AT:
                {
                int LA72_2 = input.LA(2);

                if ( (synpred96()) ) {
                    alt72=1;
                }
                else if ( (synpred98()) ) {
                    alt72=2;
                }
                else if ( (true) ) {
                    alt72=3;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("670:1: formalParameterDecls : ( ellipsisParameterDecl | normalParameterDecl ( ',' normalParameterDecl )* | ( normalParameterDecl ',' )+ ellipsisParameterDecl );", 72, 2, input);

                    throw nvae;
                }
                }
                break;
            case IDENTIFIER:
                {
                int LA72_3 = input.LA(2);

                if ( (synpred96()) ) {
                    alt72=1;
                }
                else if ( (synpred98()) ) {
                    alt72=2;
                }
                else if ( (true) ) {
                    alt72=3;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("670:1: formalParameterDecls : ( ellipsisParameterDecl | normalParameterDecl ( ',' normalParameterDecl )* | ( normalParameterDecl ',' )+ ellipsisParameterDecl );", 72, 3, input);

                    throw nvae;
                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                {
                int LA72_4 = input.LA(2);

                if ( (synpred96()) ) {
                    alt72=1;
                }
                else if ( (synpred98()) ) {
                    alt72=2;
                }
                else if ( (true) ) {
                    alt72=3;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("670:1: formalParameterDecls : ( ellipsisParameterDecl | normalParameterDecl ( ',' normalParameterDecl )* | ( normalParameterDecl ',' )+ ellipsisParameterDecl );", 72, 4, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("670:1: formalParameterDecls : ( ellipsisParameterDecl | normalParameterDecl ( ',' normalParameterDecl )* | ( normalParameterDecl ',' )+ ellipsisParameterDecl );", 72, 0, input);

                throw nvae;
            }

            switch (alt72) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:671:9: ellipsisParameterDecl
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_ellipsisParameterDecl_in_formalParameterDecls3110);
                    ellipsisParameterDecl193=ellipsisParameterDecl();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, ellipsisParameterDecl193.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:672:9: normalParameterDecl ( ',' normalParameterDecl )*
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_normalParameterDecl_in_formalParameterDecls3120);
                    normalParameterDecl194=normalParameterDecl();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, normalParameterDecl194.getTree());
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:673:9: ( ',' normalParameterDecl )*
                    loop70:
                    do {
                        int alt70=2;
                        int LA70_0 = input.LA(1);

                        if ( (LA70_0==COMMA) ) {
                            alt70=1;
                        }


                        switch (alt70) {
                    	case 1 :
                    	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:673:10: ',' normalParameterDecl
                    	    {
                    	    char_literal195=(Token)input.LT(1);
                    	    match(input,COMMA,FOLLOW_COMMA_in_formalParameterDecls3131); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal195_tree = (Object)adaptor.create(char_literal195);
                    	    adaptor.addChild(root_0, char_literal195_tree);
                    	    }
                    	    pushFollow(FOLLOW_normalParameterDecl_in_formalParameterDecls3133);
                    	    normalParameterDecl196=normalParameterDecl();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) adaptor.addChild(root_0, normalParameterDecl196.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop70;
                        }
                    } while (true);


                    }
                    break;
                case 3 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:675:9: ( normalParameterDecl ',' )+ ellipsisParameterDecl
                    {
                    root_0 = (Object)adaptor.nil();

                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:675:9: ( normalParameterDecl ',' )+
                    int cnt71=0;
                    loop71:
                    do {
                        int alt71=2;
                        switch ( input.LA(1) ) {
                        case FINAL:
                            {
                            int LA71_1 = input.LA(2);

                            if ( (synpred99()) ) {
                                alt71=1;
                            }


                            }
                            break;
                        case MONKEYS_AT:
                            {
                            int LA71_2 = input.LA(2);

                            if ( (synpred99()) ) {
                                alt71=1;
                            }


                            }
                            break;
                        case IDENTIFIER:
                            {
                            int LA71_3 = input.LA(2);

                            if ( (synpred99()) ) {
                                alt71=1;
                            }


                            }
                            break;
                        case BOOLEAN:
                        case BYTE:
                        case CHAR:
                        case DOUBLE:
                        case FLOAT:
                        case INT:
                        case LONG:
                        case SHORT:
                            {
                            int LA71_4 = input.LA(2);

                            if ( (synpred99()) ) {
                                alt71=1;
                            }


                            }
                            break;

                        }

                        switch (alt71) {
                    	case 1 :
                    	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:675:10: normalParameterDecl ','
                    	    {
                    	    pushFollow(FOLLOW_normalParameterDecl_in_formalParameterDecls3155);
                    	    normalParameterDecl197=normalParameterDecl();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) adaptor.addChild(root_0, normalParameterDecl197.getTree());
                    	    char_literal198=(Token)input.LT(1);
                    	    match(input,COMMA,FOLLOW_COMMA_in_formalParameterDecls3165); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal198_tree = (Object)adaptor.create(char_literal198);
                    	    adaptor.addChild(root_0, char_literal198_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt71 >= 1 ) break loop71;
                    	    if (backtracking>0) {failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(71, input);
                                throw eee;
                        }
                        cnt71++;
                    } while (true);

                    pushFollow(FOLLOW_ellipsisParameterDecl_in_formalParameterDecls3187);
                    ellipsisParameterDecl199=ellipsisParameterDecl();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, ellipsisParameterDecl199.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 39, formalParameterDecls_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end formalParameterDecls

    public static class normalParameterDecl_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start normalParameterDecl
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:681:1: normalParameterDecl : variableModifiers type IDENTIFIER ( '[' ']' )* ;
    public final normalParameterDecl_return normalParameterDecl() throws RecognitionException {
        normalParameterDecl_return retval = new normalParameterDecl_return();
        retval.start = input.LT(1);
        int normalParameterDecl_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER202=null;
        Token char_literal203=null;
        Token char_literal204=null;
        variableModifiers_return variableModifiers200 = null;

        type_return type201 = null;


        Object IDENTIFIER202_tree=null;
        Object char_literal203_tree=null;
        Object char_literal204_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 40) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:682:5: ( variableModifiers type IDENTIFIER ( '[' ']' )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:682:9: variableModifiers type IDENTIFIER ( '[' ']' )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_normalParameterDecl3207);
            variableModifiers200=variableModifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, variableModifiers200.getTree());
            pushFollow(FOLLOW_type_in_normalParameterDecl3209);
            type201=type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, type201.getTree());
            IDENTIFIER202=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_normalParameterDecl3211); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER202_tree = (Object)adaptor.create(IDENTIFIER202);
            adaptor.addChild(root_0, IDENTIFIER202_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:683:9: ( '[' ']' )*
            loop73:
            do {
                int alt73=2;
                int LA73_0 = input.LA(1);

                if ( (LA73_0==LBRACKET) ) {
                    alt73=1;
                }


                switch (alt73) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:683:10: '[' ']'
            	    {
            	    char_literal203=(Token)input.LT(1);
            	    match(input,LBRACKET,FOLLOW_LBRACKET_in_normalParameterDecl3222); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal203_tree = (Object)adaptor.create(char_literal203);
            	    adaptor.addChild(root_0, char_literal203_tree);
            	    }
            	    char_literal204=(Token)input.LT(1);
            	    match(input,RBRACKET,FOLLOW_RBRACKET_in_normalParameterDecl3224); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal204_tree = (Object)adaptor.create(char_literal204);
            	    adaptor.addChild(root_0, char_literal204_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop73;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 40, normalParameterDecl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end normalParameterDecl

    public static class ellipsisParameterDecl_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start ellipsisParameterDecl
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:687:1: ellipsisParameterDecl : variableModifiers type '...' IDENTIFIER ;
    public final ellipsisParameterDecl_return ellipsisParameterDecl() throws RecognitionException {
        ellipsisParameterDecl_return retval = new ellipsisParameterDecl_return();
        retval.start = input.LT(1);
        int ellipsisParameterDecl_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal207=null;
        Token IDENTIFIER208=null;
        variableModifiers_return variableModifiers205 = null;

        type_return type206 = null;


        Object string_literal207_tree=null;
        Object IDENTIFIER208_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 41) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:688:5: ( variableModifiers type '...' IDENTIFIER )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:688:9: variableModifiers type '...' IDENTIFIER
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_ellipsisParameterDecl3255);
            variableModifiers205=variableModifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, variableModifiers205.getTree());
            pushFollow(FOLLOW_type_in_ellipsisParameterDecl3265);
            type206=type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, type206.getTree());
            string_literal207=(Token)input.LT(1);
            match(input,ELLIPSIS,FOLLOW_ELLIPSIS_in_ellipsisParameterDecl3268); if (failed) return retval;
            if ( backtracking==0 ) {
            string_literal207_tree = (Object)adaptor.create(string_literal207);
            adaptor.addChild(root_0, string_literal207_tree);
            }
            IDENTIFIER208=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_ellipsisParameterDecl3278); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER208_tree = (Object)adaptor.create(IDENTIFIER208);
            adaptor.addChild(root_0, IDENTIFIER208_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 41, ellipsisParameterDecl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end ellipsisParameterDecl

    public static class explicitConstructorInvocation_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start explicitConstructorInvocation
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:694:1: explicitConstructorInvocation : ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' );
    public final explicitConstructorInvocation_return explicitConstructorInvocation() throws RecognitionException {
        explicitConstructorInvocation_return retval = new explicitConstructorInvocation_return();
        retval.start = input.LT(1);
        int explicitConstructorInvocation_StartIndex = input.index();
        Object root_0 = null;

        Token set210=null;
        Token char_literal212=null;
        Token char_literal214=null;
        Token string_literal216=null;
        Token char_literal218=null;
        nonWildcardTypeArguments_return nonWildcardTypeArguments209 = null;

        arguments_return arguments211 = null;

        primary_return primary213 = null;

        nonWildcardTypeArguments_return nonWildcardTypeArguments215 = null;

        arguments_return arguments217 = null;


        Object set210_tree=null;
        Object char_literal212_tree=null;
        Object char_literal214_tree=null;
        Object string_literal216_tree=null;
        Object char_literal218_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 42) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:695:5: ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' )
            int alt76=2;
            switch ( input.LA(1) ) {
            case LT:
                {
                alt76=1;
                }
                break;
            case THIS:
                {
                int LA76_2 = input.LA(2);

                if ( (synpred103()) ) {
                    alt76=1;
                }
                else if ( (true) ) {
                    alt76=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("694:1: explicitConstructorInvocation : ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' );", 76, 2, input);

                    throw nvae;
                }
                }
                break;
            case IDENTIFIER:
            case INTLITERAL:
            case LONGLITERAL:
            case FLOATLITERAL:
            case DOUBLELITERAL:
            case CHARLITERAL:
            case STRINGLITERAL:
            case TRUE:
            case FALSE:
            case NULL:
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case NEW:
            case SHORT:
            case VOID:
            case LPAREN:
                {
                alt76=2;
                }
                break;
            case SUPER:
                {
                int LA76_4 = input.LA(2);

                if ( (synpred103()) ) {
                    alt76=1;
                }
                else if ( (true) ) {
                    alt76=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("694:1: explicitConstructorInvocation : ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' );", 76, 4, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("694:1: explicitConstructorInvocation : ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' );", 76, 0, input);

                throw nvae;
            }

            switch (alt76) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:695:9: ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:695:9: ( nonWildcardTypeArguments )?
                    int alt74=2;
                    int LA74_0 = input.LA(1);

                    if ( (LA74_0==LT) ) {
                        alt74=1;
                    }
                    switch (alt74) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:695:10: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3300);
                            nonWildcardTypeArguments209=nonWildcardTypeArguments();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments209.getTree());

                            }
                            break;

                    }

                    set210=(Token)input.LT(1);
                    if ( input.LA(1)==SUPER||input.LA(1)==THIS ) {
                        input.consume();
                        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set210));
                        errorRecovery=false;failed=false;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_explicitConstructorInvocation3326);    throw mse;
                    }

                    pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation3358);
                    arguments211=arguments();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, arguments211.getTree());
                    char_literal212=(Token)input.LT(1);
                    match(input,SEMI,FOLLOW_SEMI_in_explicitConstructorInvocation3360); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal212_tree = (Object)adaptor.create(char_literal212);
                    adaptor.addChild(root_0, char_literal212_tree);
                    }

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:702:9: primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primary_in_explicitConstructorInvocation3371);
                    primary213=primary();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, primary213.getTree());
                    char_literal214=(Token)input.LT(1);
                    match(input,DOT,FOLLOW_DOT_in_explicitConstructorInvocation3381); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal214_tree = (Object)adaptor.create(char_literal214);
                    adaptor.addChild(root_0, char_literal214_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:704:9: ( nonWildcardTypeArguments )?
                    int alt75=2;
                    int LA75_0 = input.LA(1);

                    if ( (LA75_0==LT) ) {
                        alt75=1;
                    }
                    switch (alt75) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:704:10: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3392);
                            nonWildcardTypeArguments215=nonWildcardTypeArguments();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments215.getTree());

                            }
                            break;

                    }

                    string_literal216=(Token)input.LT(1);
                    match(input,SUPER,FOLLOW_SUPER_in_explicitConstructorInvocation3413); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal216_tree = (Object)adaptor.create(string_literal216);
                    adaptor.addChild(root_0, string_literal216_tree);
                    }
                    pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation3423);
                    arguments217=arguments();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, arguments217.getTree());
                    char_literal218=(Token)input.LT(1);
                    match(input,SEMI,FOLLOW_SEMI_in_explicitConstructorInvocation3425); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal218_tree = (Object)adaptor.create(char_literal218);
                    adaptor.addChild(root_0, char_literal218_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 42, explicitConstructorInvocation_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end explicitConstructorInvocation

    public static class qualifiedName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start qualifiedName
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:710:1: qualifiedName : IDENTIFIER ( '.' IDENTIFIER )* ;
    public final qualifiedName_return qualifiedName() throws RecognitionException {
        qualifiedName_return retval = new qualifiedName_return();
        retval.start = input.LT(1);
        int qualifiedName_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER219=null;
        Token char_literal220=null;
        Token IDENTIFIER221=null;

        Object IDENTIFIER219_tree=null;
        Object char_literal220_tree=null;
        Object IDENTIFIER221_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 43) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:711:5: ( IDENTIFIER ( '.' IDENTIFIER )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:711:9: IDENTIFIER ( '.' IDENTIFIER )*
            {
            root_0 = (Object)adaptor.nil();

            IDENTIFIER219=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedName3445); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER219_tree = (Object)adaptor.create(IDENTIFIER219);
            adaptor.addChild(root_0, IDENTIFIER219_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:712:9: ( '.' IDENTIFIER )*
            loop77:
            do {
                int alt77=2;
                int LA77_0 = input.LA(1);

                if ( (LA77_0==DOT) ) {
                    alt77=1;
                }


                switch (alt77) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:712:10: '.' IDENTIFIER
            	    {
            	    char_literal220=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_qualifiedName3456); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal220_tree = (Object)adaptor.create(char_literal220);
            	    adaptor.addChild(root_0, char_literal220_tree);
            	    }
            	    IDENTIFIER221=(Token)input.LT(1);
            	    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedName3458); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    IDENTIFIER221_tree = (Object)adaptor.create(IDENTIFIER221);
            	    adaptor.addChild(root_0, IDENTIFIER221_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop77;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 43, qualifiedName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end qualifiedName

    public static class annotations_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start annotations
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:716:1: annotations : ( annotation )+ ;
    public final annotations_return annotations() throws RecognitionException {
        annotations_return retval = new annotations_return();
        retval.start = input.LT(1);
        int annotations_StartIndex = input.index();
        Object root_0 = null;

        annotation_return annotation222 = null;



        try {
            if ( backtracking>0 && alreadyParsedRule(input, 44) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:717:5: ( ( annotation )+ )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:717:9: ( annotation )+
            {
            root_0 = (Object)adaptor.nil();

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:717:9: ( annotation )+
            int cnt78=0;
            loop78:
            do {
                int alt78=2;
                int LA78_0 = input.LA(1);

                if ( (LA78_0==MONKEYS_AT) ) {
                    alt78=1;
                }


                switch (alt78) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:717:10: annotation
            	    {
            	    pushFollow(FOLLOW_annotation_in_annotations3490);
            	    annotation222=annotation();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, annotation222.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt78 >= 1 ) break loop78;
            	    if (backtracking>0) {failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(78, input);
                        throw eee;
                }
                cnt78++;
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 44, annotations_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end annotations

    public static class annotation_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start annotation
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:721:1: annotation : '@' qualifiedName ( '(' ( elementValuePairs | elementValue )? ')' )? ;
    public final annotation_return annotation() throws RecognitionException {
        annotation_return retval = new annotation_return();
        retval.start = input.LT(1);
        int annotation_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal223=null;
        Token char_literal225=null;
        Token char_literal228=null;
        qualifiedName_return qualifiedName224 = null;

        elementValuePairs_return elementValuePairs226 = null;

        elementValue_return elementValue227 = null;


        Object char_literal223_tree=null;
        Object char_literal225_tree=null;
        Object char_literal228_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 45) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:726:5: ( '@' qualifiedName ( '(' ( elementValuePairs | elementValue )? ')' )? )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:726:9: '@' qualifiedName ( '(' ( elementValuePairs | elementValue )? ')' )?
            {
            root_0 = (Object)adaptor.nil();

            char_literal223=(Token)input.LT(1);
            match(input,MONKEYS_AT,FOLLOW_MONKEYS_AT_in_annotation3523); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal223_tree = (Object)adaptor.create(char_literal223);
            adaptor.addChild(root_0, char_literal223_tree);
            }
            pushFollow(FOLLOW_qualifiedName_in_annotation3525);
            qualifiedName224=qualifiedName();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, qualifiedName224.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:727:9: ( '(' ( elementValuePairs | elementValue )? ')' )?
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( (LA80_0==LPAREN) ) {
                alt80=1;
            }
            switch (alt80) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:727:13: '(' ( elementValuePairs | elementValue )? ')'
                    {
                    char_literal225=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_annotation3539); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal225_tree = (Object)adaptor.create(char_literal225);
                    adaptor.addChild(root_0, char_literal225_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:728:19: ( elementValuePairs | elementValue )?
                    int alt79=3;
                    int LA79_0 = input.LA(1);

                    if ( (LA79_0==IDENTIFIER) ) {
                        int LA79_1 = input.LA(2);

                        if ( (LA79_1==EQ) ) {
                            alt79=1;
                        }
                        else if ( (LA79_1==INSTANCEOF||(LA79_1>=LPAREN && LA79_1<=RPAREN)||LA79_1==LBRACKET||LA79_1==DOT||LA79_1==QUES||(LA79_1>=EQEQ && LA79_1<=PERCENT)||(LA79_1>=BANGEQ && LA79_1<=LT)) ) {
                            alt79=2;
                        }
                    }
                    else if ( ((LA79_0>=INTLITERAL && LA79_0<=NULL)||LA79_0==BOOLEAN||LA79_0==BYTE||LA79_0==CHAR||LA79_0==DOUBLE||LA79_0==FLOAT||LA79_0==INT||LA79_0==LONG||LA79_0==NEW||LA79_0==SHORT||LA79_0==SUPER||LA79_0==THIS||LA79_0==VOID||LA79_0==LPAREN||LA79_0==LBRACE||(LA79_0>=BANG && LA79_0<=TILDE)||(LA79_0>=PLUSPLUS && LA79_0<=SUB)||LA79_0==MONKEYS_AT) ) {
                        alt79=2;
                    }
                    switch (alt79) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:728:23: elementValuePairs
                            {
                            pushFollow(FOLLOW_elementValuePairs_in_annotation3566);
                            elementValuePairs226=elementValuePairs();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, elementValuePairs226.getTree());

                            }
                            break;
                        case 2 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:729:23: elementValue
                            {
                            pushFollow(FOLLOW_elementValue_in_annotation3590);
                            elementValue227=elementValue();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, elementValue227.getTree());

                            }
                            break;

                    }

                    char_literal228=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_annotation3626); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal228_tree = (Object)adaptor.create(char_literal228);
                    adaptor.addChild(root_0, char_literal228_tree);
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 45, annotation_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end annotation

    public static class elementValuePairs_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start elementValuePairs
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:735:1: elementValuePairs : elementValuePair ( ',' elementValuePair )* ;
    public final elementValuePairs_return elementValuePairs() throws RecognitionException {
        elementValuePairs_return retval = new elementValuePairs_return();
        retval.start = input.LT(1);
        int elementValuePairs_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal230=null;
        elementValuePair_return elementValuePair229 = null;

        elementValuePair_return elementValuePair231 = null;


        Object char_literal230_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 46) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:736:5: ( elementValuePair ( ',' elementValuePair )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:736:9: elementValuePair ( ',' elementValuePair )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_elementValuePair_in_elementValuePairs3658);
            elementValuePair229=elementValuePair();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, elementValuePair229.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:737:9: ( ',' elementValuePair )*
            loop81:
            do {
                int alt81=2;
                int LA81_0 = input.LA(1);

                if ( (LA81_0==COMMA) ) {
                    alt81=1;
                }


                switch (alt81) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:737:10: ',' elementValuePair
            	    {
            	    char_literal230=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_elementValuePairs3669); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal230_tree = (Object)adaptor.create(char_literal230);
            	    adaptor.addChild(root_0, char_literal230_tree);
            	    }
            	    pushFollow(FOLLOW_elementValuePair_in_elementValuePairs3671);
            	    elementValuePair231=elementValuePair();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, elementValuePair231.getTree());

            	    }
            	    break;

            	default :
            	    break loop81;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 46, elementValuePairs_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end elementValuePairs

    public static class elementValuePair_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start elementValuePair
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:741:1: elementValuePair : IDENTIFIER '=' elementValue ;
    public final elementValuePair_return elementValuePair() throws RecognitionException {
        elementValuePair_return retval = new elementValuePair_return();
        retval.start = input.LT(1);
        int elementValuePair_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER232=null;
        Token char_literal233=null;
        elementValue_return elementValue234 = null;


        Object IDENTIFIER232_tree=null;
        Object char_literal233_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 47) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:742:5: ( IDENTIFIER '=' elementValue )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:742:9: IDENTIFIER '=' elementValue
            {
            root_0 = (Object)adaptor.nil();

            IDENTIFIER232=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_elementValuePair3702); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER232_tree = (Object)adaptor.create(IDENTIFIER232);
            adaptor.addChild(root_0, IDENTIFIER232_tree);
            }
            char_literal233=(Token)input.LT(1);
            match(input,EQ,FOLLOW_EQ_in_elementValuePair3704); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal233_tree = (Object)adaptor.create(char_literal233);
            adaptor.addChild(root_0, char_literal233_tree);
            }
            pushFollow(FOLLOW_elementValue_in_elementValuePair3706);
            elementValue234=elementValue();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, elementValue234.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 47, elementValuePair_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end elementValuePair

    public static class elementValue_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start elementValue
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:745:1: elementValue : ( conditionalExpression | annotation | elementValueArrayInitializer );
    public final elementValue_return elementValue() throws RecognitionException {
        elementValue_return retval = new elementValue_return();
        retval.start = input.LT(1);
        int elementValue_StartIndex = input.index();
        Object root_0 = null;

        conditionalExpression_return conditionalExpression235 = null;

        annotation_return annotation236 = null;

        elementValueArrayInitializer_return elementValueArrayInitializer237 = null;



        try {
            if ( backtracking>0 && alreadyParsedRule(input, 48) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:746:5: ( conditionalExpression | annotation | elementValueArrayInitializer )
            int alt82=3;
            switch ( input.LA(1) ) {
            case IDENTIFIER:
            case INTLITERAL:
            case LONGLITERAL:
            case FLOATLITERAL:
            case DOUBLELITERAL:
            case CHARLITERAL:
            case STRINGLITERAL:
            case TRUE:
            case FALSE:
            case NULL:
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case NEW:
            case SHORT:
            case SUPER:
            case THIS:
            case VOID:
            case LPAREN:
            case BANG:
            case TILDE:
            case PLUSPLUS:
            case SUBSUB:
            case PLUS:
            case SUB:
                {
                alt82=1;
                }
                break;
            case MONKEYS_AT:
                {
                alt82=2;
                }
                break;
            case LBRACE:
                {
                alt82=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("745:1: elementValue : ( conditionalExpression | annotation | elementValueArrayInitializer );", 82, 0, input);

                throw nvae;
            }

            switch (alt82) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:746:9: conditionalExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_conditionalExpression_in_elementValue3726);
                    conditionalExpression235=conditionalExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, conditionalExpression235.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:747:9: annotation
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotation_in_elementValue3736);
                    annotation236=annotation();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, annotation236.getTree());

                    }
                    break;
                case 3 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:748:9: elementValueArrayInitializer
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_elementValueArrayInitializer_in_elementValue3746);
                    elementValueArrayInitializer237=elementValueArrayInitializer();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, elementValueArrayInitializer237.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 48, elementValue_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end elementValue

    public static class elementValueArrayInitializer_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start elementValueArrayInitializer
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:751:1: elementValueArrayInitializer : '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}' ;
    public final elementValueArrayInitializer_return elementValueArrayInitializer() throws RecognitionException {
        elementValueArrayInitializer_return retval = new elementValueArrayInitializer_return();
        retval.start = input.LT(1);
        int elementValueArrayInitializer_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal238=null;
        Token char_literal240=null;
        Token char_literal242=null;
        Token char_literal243=null;
        elementValue_return elementValue239 = null;

        elementValue_return elementValue241 = null;


        Object char_literal238_tree=null;
        Object char_literal240_tree=null;
        Object char_literal242_tree=null;
        Object char_literal243_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 49) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:752:5: ( '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:752:9: '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal238=(Token)input.LT(1);
            match(input,LBRACE,FOLLOW_LBRACE_in_elementValueArrayInitializer3766); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal238_tree = (Object)adaptor.create(char_literal238);
            adaptor.addChild(root_0, char_literal238_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:753:9: ( elementValue ( ',' elementValue )* )?
            int alt84=2;
            int LA84_0 = input.LA(1);

            if ( ((LA84_0>=IDENTIFIER && LA84_0<=NULL)||LA84_0==BOOLEAN||LA84_0==BYTE||LA84_0==CHAR||LA84_0==DOUBLE||LA84_0==FLOAT||LA84_0==INT||LA84_0==LONG||LA84_0==NEW||LA84_0==SHORT||LA84_0==SUPER||LA84_0==THIS||LA84_0==VOID||LA84_0==LPAREN||LA84_0==LBRACE||(LA84_0>=BANG && LA84_0<=TILDE)||(LA84_0>=PLUSPLUS && LA84_0<=SUB)||LA84_0==MONKEYS_AT) ) {
                alt84=1;
            }
            switch (alt84) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:753:10: elementValue ( ',' elementValue )*
                    {
                    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer3777);
                    elementValue239=elementValue();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, elementValue239.getTree());
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:754:13: ( ',' elementValue )*
                    loop83:
                    do {
                        int alt83=2;
                        int LA83_0 = input.LA(1);

                        if ( (LA83_0==COMMA) ) {
                            int LA83_1 = input.LA(2);

                            if ( ((LA83_1>=IDENTIFIER && LA83_1<=NULL)||LA83_1==BOOLEAN||LA83_1==BYTE||LA83_1==CHAR||LA83_1==DOUBLE||LA83_1==FLOAT||LA83_1==INT||LA83_1==LONG||LA83_1==NEW||LA83_1==SHORT||LA83_1==SUPER||LA83_1==THIS||LA83_1==VOID||LA83_1==LPAREN||LA83_1==LBRACE||(LA83_1>=BANG && LA83_1<=TILDE)||(LA83_1>=PLUSPLUS && LA83_1<=SUB)||LA83_1==MONKEYS_AT) ) {
                                alt83=1;
                            }


                        }


                        switch (alt83) {
                    	case 1 :
                    	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:754:14: ',' elementValue
                    	    {
                    	    char_literal240=(Token)input.LT(1);
                    	    match(input,COMMA,FOLLOW_COMMA_in_elementValueArrayInitializer3792); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal240_tree = (Object)adaptor.create(char_literal240);
                    	    adaptor.addChild(root_0, char_literal240_tree);
                    	    }
                    	    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer3794);
                    	    elementValue241=elementValue();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) adaptor.addChild(root_0, elementValue241.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop83;
                        }
                    } while (true);


                    }
                    break;

            }

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:756:12: ( ',' )?
            int alt85=2;
            int LA85_0 = input.LA(1);

            if ( (LA85_0==COMMA) ) {
                alt85=1;
            }
            switch (alt85) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:756:13: ','
                    {
                    char_literal242=(Token)input.LT(1);
                    match(input,COMMA,FOLLOW_COMMA_in_elementValueArrayInitializer3823); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal242_tree = (Object)adaptor.create(char_literal242);
                    adaptor.addChild(root_0, char_literal242_tree);
                    }

                    }
                    break;

            }

            char_literal243=(Token)input.LT(1);
            match(input,RBRACE,FOLLOW_RBRACE_in_elementValueArrayInitializer3827); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal243_tree = (Object)adaptor.create(char_literal243);
            adaptor.addChild(root_0, char_literal243_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 49, elementValueArrayInitializer_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end elementValueArrayInitializer

    public static class annotationTypeDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start annotationTypeDeclaration
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:760:1: annotationTypeDeclaration : modifiers '@' 'interface' IDENTIFIER annotationTypeBody ;
    public final annotationTypeDeclaration_return annotationTypeDeclaration() throws RecognitionException {
        annotationTypeDeclaration_return retval = new annotationTypeDeclaration_return();
        retval.start = input.LT(1);
        int annotationTypeDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal245=null;
        Token string_literal246=null;
        Token IDENTIFIER247=null;
        modifiers_return modifiers244 = null;

        annotationTypeBody_return annotationTypeBody248 = null;


        Object char_literal245_tree=null;
        Object string_literal246_tree=null;
        Object IDENTIFIER247_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 50) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:764:5: ( modifiers '@' 'interface' IDENTIFIER annotationTypeBody )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:764:9: modifiers '@' 'interface' IDENTIFIER annotationTypeBody
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_annotationTypeDeclaration3850);
            modifiers244=modifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, modifiers244.getTree());
            char_literal245=(Token)input.LT(1);
            match(input,MONKEYS_AT,FOLLOW_MONKEYS_AT_in_annotationTypeDeclaration3852); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal245_tree = (Object)adaptor.create(char_literal245);
            adaptor.addChild(root_0, char_literal245_tree);
            }
            string_literal246=(Token)input.LT(1);
            match(input,INTERFACE,FOLLOW_INTERFACE_in_annotationTypeDeclaration3862); if (failed) return retval;
            if ( backtracking==0 ) {
            string_literal246_tree = (Object)adaptor.create(string_literal246);
            adaptor.addChild(root_0, string_literal246_tree);
            }
            IDENTIFIER247=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_annotationTypeDeclaration3872); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER247_tree = (Object)adaptor.create(IDENTIFIER247);
            adaptor.addChild(root_0, IDENTIFIER247_tree);
            }
            pushFollow(FOLLOW_annotationTypeBody_in_annotationTypeDeclaration3882);
            annotationTypeBody248=annotationTypeBody();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, annotationTypeBody248.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 50, annotationTypeDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end annotationTypeDeclaration

    public static class annotationTypeBody_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start annotationTypeBody
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:771:1: annotationTypeBody : '{' ( annotationTypeElementDeclaration )* '}' ;
    public final annotationTypeBody_return annotationTypeBody() throws RecognitionException {
        annotationTypeBody_return retval = new annotationTypeBody_return();
        retval.start = input.LT(1);
        int annotationTypeBody_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal249=null;
        Token char_literal251=null;
        annotationTypeElementDeclaration_return annotationTypeElementDeclaration250 = null;


        Object char_literal249_tree=null;
        Object char_literal251_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 51) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:772:5: ( '{' ( annotationTypeElementDeclaration )* '}' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:772:9: '{' ( annotationTypeElementDeclaration )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal249=(Token)input.LT(1);
            match(input,LBRACE,FOLLOW_LBRACE_in_annotationTypeBody3903); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal249_tree = (Object)adaptor.create(char_literal249);
            adaptor.addChild(root_0, char_literal249_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:773:9: ( annotationTypeElementDeclaration )*
            loop86:
            do {
                int alt86=2;
                int LA86_0 = input.LA(1);

                if ( (LA86_0==IDENTIFIER||LA86_0==ABSTRACT||LA86_0==BOOLEAN||LA86_0==BYTE||(LA86_0>=CHAR && LA86_0<=CLASS)||LA86_0==DOUBLE||LA86_0==ENUM||LA86_0==FINAL||LA86_0==FLOAT||(LA86_0>=INT && LA86_0<=NATIVE)||(LA86_0>=PRIVATE && LA86_0<=PUBLIC)||(LA86_0>=SHORT && LA86_0<=STRICTFP)||LA86_0==SYNCHRONIZED||LA86_0==TRANSIENT||LA86_0==VOLATILE||LA86_0==SEMI||LA86_0==MONKEYS_AT) ) {
                    alt86=1;
                }


                switch (alt86) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:773:10: annotationTypeElementDeclaration
            	    {
            	    pushFollow(FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody3915);
            	    annotationTypeElementDeclaration250=annotationTypeElementDeclaration();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, annotationTypeElementDeclaration250.getTree());

            	    }
            	    break;

            	default :
            	    break loop86;
                }
            } while (true);

            char_literal251=(Token)input.LT(1);
            match(input,RBRACE,FOLLOW_RBRACE_in_annotationTypeBody3937); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal251_tree = (Object)adaptor.create(char_literal251);
            adaptor.addChild(root_0, char_literal251_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 51, annotationTypeBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end annotationTypeBody

    public static class annotationTypeElementDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start annotationTypeElementDeclaration
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:778:1: annotationTypeElementDeclaration : ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' );
    public final annotationTypeElementDeclaration_return annotationTypeElementDeclaration() throws RecognitionException {
        annotationTypeElementDeclaration_return retval = new annotationTypeElementDeclaration_return();
        retval.start = input.LT(1);
        int annotationTypeElementDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal258=null;
        annotationMethodDeclaration_return annotationMethodDeclaration252 = null;

        interfaceFieldDeclaration_return interfaceFieldDeclaration253 = null;

        normalClassDeclaration_return normalClassDeclaration254 = null;

        normalInterfaceDeclaration_return normalInterfaceDeclaration255 = null;

        enumDeclaration_return enumDeclaration256 = null;

        annotationTypeDeclaration_return annotationTypeDeclaration257 = null;


        Object char_literal258_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 52) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:782:5: ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' )
            int alt87=7;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA87_1 = input.LA(2);

                if ( (synpred117()) ) {
                    alt87=1;
                }
                else if ( (synpred118()) ) {
                    alt87=2;
                }
                else if ( (synpred119()) ) {
                    alt87=3;
                }
                else if ( (synpred120()) ) {
                    alt87=4;
                }
                else if ( (synpred121()) ) {
                    alt87=5;
                }
                else if ( (synpred122()) ) {
                    alt87=6;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("778:1: annotationTypeElementDeclaration : ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' );", 87, 1, input);

                    throw nvae;
                }
                }
                break;
            case PUBLIC:
                {
                int LA87_2 = input.LA(2);

                if ( (synpred117()) ) {
                    alt87=1;
                }
                else if ( (synpred118()) ) {
                    alt87=2;
                }
                else if ( (synpred119()) ) {
                    alt87=3;
                }
                else if ( (synpred120()) ) {
                    alt87=4;
                }
                else if ( (synpred121()) ) {
                    alt87=5;
                }
                else if ( (synpred122()) ) {
                    alt87=6;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("778:1: annotationTypeElementDeclaration : ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' );", 87, 2, input);

                    throw nvae;
                }
                }
                break;
            case PROTECTED:
                {
                int LA87_3 = input.LA(2);

                if ( (synpred117()) ) {
                    alt87=1;
                }
                else if ( (synpred118()) ) {
                    alt87=2;
                }
                else if ( (synpred119()) ) {
                    alt87=3;
                }
                else if ( (synpred120()) ) {
                    alt87=4;
                }
                else if ( (synpred121()) ) {
                    alt87=5;
                }
                else if ( (synpred122()) ) {
                    alt87=6;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("778:1: annotationTypeElementDeclaration : ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' );", 87, 3, input);

                    throw nvae;
                }
                }
                break;
            case PRIVATE:
                {
                int LA87_4 = input.LA(2);

                if ( (synpred117()) ) {
                    alt87=1;
                }
                else if ( (synpred118()) ) {
                    alt87=2;
                }
                else if ( (synpred119()) ) {
                    alt87=3;
                }
                else if ( (synpred120()) ) {
                    alt87=4;
                }
                else if ( (synpred121()) ) {
                    alt87=5;
                }
                else if ( (synpred122()) ) {
                    alt87=6;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("778:1: annotationTypeElementDeclaration : ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' );", 87, 4, input);

                    throw nvae;
                }
                }
                break;
            case STATIC:
                {
                int LA87_5 = input.LA(2);

                if ( (synpred117()) ) {
                    alt87=1;
                }
                else if ( (synpred118()) ) {
                    alt87=2;
                }
                else if ( (synpred119()) ) {
                    alt87=3;
                }
                else if ( (synpred120()) ) {
                    alt87=4;
                }
                else if ( (synpred121()) ) {
                    alt87=5;
                }
                else if ( (synpred122()) ) {
                    alt87=6;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("778:1: annotationTypeElementDeclaration : ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' );", 87, 5, input);

                    throw nvae;
                }
                }
                break;
            case ABSTRACT:
                {
                int LA87_6 = input.LA(2);

                if ( (synpred117()) ) {
                    alt87=1;
                }
                else if ( (synpred118()) ) {
                    alt87=2;
                }
                else if ( (synpred119()) ) {
                    alt87=3;
                }
                else if ( (synpred120()) ) {
                    alt87=4;
                }
                else if ( (synpred121()) ) {
                    alt87=5;
                }
                else if ( (synpred122()) ) {
                    alt87=6;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("778:1: annotationTypeElementDeclaration : ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' );", 87, 6, input);

                    throw nvae;
                }
                }
                break;
            case FINAL:
                {
                int LA87_7 = input.LA(2);

                if ( (synpred117()) ) {
                    alt87=1;
                }
                else if ( (synpred118()) ) {
                    alt87=2;
                }
                else if ( (synpred119()) ) {
                    alt87=3;
                }
                else if ( (synpred120()) ) {
                    alt87=4;
                }
                else if ( (synpred121()) ) {
                    alt87=5;
                }
                else if ( (synpred122()) ) {
                    alt87=6;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("778:1: annotationTypeElementDeclaration : ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' );", 87, 7, input);

                    throw nvae;
                }
                }
                break;
            case NATIVE:
                {
                int LA87_8 = input.LA(2);

                if ( (synpred117()) ) {
                    alt87=1;
                }
                else if ( (synpred118()) ) {
                    alt87=2;
                }
                else if ( (synpred119()) ) {
                    alt87=3;
                }
                else if ( (synpred120()) ) {
                    alt87=4;
                }
                else if ( (synpred121()) ) {
                    alt87=5;
                }
                else if ( (synpred122()) ) {
                    alt87=6;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("778:1: annotationTypeElementDeclaration : ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' );", 87, 8, input);

                    throw nvae;
                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA87_9 = input.LA(2);

                if ( (synpred117()) ) {
                    alt87=1;
                }
                else if ( (synpred118()) ) {
                    alt87=2;
                }
                else if ( (synpred119()) ) {
                    alt87=3;
                }
                else if ( (synpred120()) ) {
                    alt87=4;
                }
                else if ( (synpred121()) ) {
                    alt87=5;
                }
                else if ( (synpred122()) ) {
                    alt87=6;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("778:1: annotationTypeElementDeclaration : ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' );", 87, 9, input);

                    throw nvae;
                }
                }
                break;
            case TRANSIENT:
                {
                int LA87_10 = input.LA(2);

                if ( (synpred117()) ) {
                    alt87=1;
                }
                else if ( (synpred118()) ) {
                    alt87=2;
                }
                else if ( (synpred119()) ) {
                    alt87=3;
                }
                else if ( (synpred120()) ) {
                    alt87=4;
                }
                else if ( (synpred121()) ) {
                    alt87=5;
                }
                else if ( (synpred122()) ) {
                    alt87=6;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("778:1: annotationTypeElementDeclaration : ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' );", 87, 10, input);

                    throw nvae;
                }
                }
                break;
            case VOLATILE:
                {
                int LA87_11 = input.LA(2);

                if ( (synpred117()) ) {
                    alt87=1;
                }
                else if ( (synpred118()) ) {
                    alt87=2;
                }
                else if ( (synpred119()) ) {
                    alt87=3;
                }
                else if ( (synpred120()) ) {
                    alt87=4;
                }
                else if ( (synpred121()) ) {
                    alt87=5;
                }
                else if ( (synpred122()) ) {
                    alt87=6;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("778:1: annotationTypeElementDeclaration : ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' );", 87, 11, input);

                    throw nvae;
                }
                }
                break;
            case STRICTFP:
                {
                int LA87_12 = input.LA(2);

                if ( (synpred117()) ) {
                    alt87=1;
                }
                else if ( (synpred118()) ) {
                    alt87=2;
                }
                else if ( (synpred119()) ) {
                    alt87=3;
                }
                else if ( (synpred120()) ) {
                    alt87=4;
                }
                else if ( (synpred121()) ) {
                    alt87=5;
                }
                else if ( (synpred122()) ) {
                    alt87=6;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("778:1: annotationTypeElementDeclaration : ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' );", 87, 12, input);

                    throw nvae;
                }
                }
                break;
            case IDENTIFIER:
                {
                int LA87_13 = input.LA(2);

                if ( (synpred117()) ) {
                    alt87=1;
                }
                else if ( (synpred118()) ) {
                    alt87=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("778:1: annotationTypeElementDeclaration : ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' );", 87, 13, input);

                    throw nvae;
                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                {
                int LA87_14 = input.LA(2);

                if ( (synpred117()) ) {
                    alt87=1;
                }
                else if ( (synpred118()) ) {
                    alt87=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("778:1: annotationTypeElementDeclaration : ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' );", 87, 14, input);

                    throw nvae;
                }
                }
                break;
            case CLASS:
                {
                alt87=3;
                }
                break;
            case INTERFACE:
                {
                alt87=4;
                }
                break;
            case ENUM:
                {
                alt87=5;
                }
                break;
            case SEMI:
                {
                alt87=7;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("778:1: annotationTypeElementDeclaration : ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' );", 87, 0, input);

                throw nvae;
            }

            switch (alt87) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:782:9: annotationMethodDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotationMethodDeclaration_in_annotationTypeElementDeclaration3959);
                    annotationMethodDeclaration252=annotationMethodDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, annotationMethodDeclaration252.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:783:9: interfaceFieldDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_interfaceFieldDeclaration_in_annotationTypeElementDeclaration3969);
                    interfaceFieldDeclaration253=interfaceFieldDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, interfaceFieldDeclaration253.getTree());

                    }
                    break;
                case 3 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:784:9: normalClassDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_normalClassDeclaration_in_annotationTypeElementDeclaration3979);
                    normalClassDeclaration254=normalClassDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, normalClassDeclaration254.getTree());

                    }
                    break;
                case 4 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:785:9: normalInterfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementDeclaration3989);
                    normalInterfaceDeclaration255=normalInterfaceDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, normalInterfaceDeclaration255.getTree());

                    }
                    break;
                case 5 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:786:9: enumDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_enumDeclaration_in_annotationTypeElementDeclaration3999);
                    enumDeclaration256=enumDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, enumDeclaration256.getTree());

                    }
                    break;
                case 6 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:787:9: annotationTypeDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_annotationTypeDeclaration_in_annotationTypeElementDeclaration4009);
                    annotationTypeDeclaration257=annotationTypeDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, annotationTypeDeclaration257.getTree());

                    }
                    break;
                case 7 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:788:9: ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal258=(Token)input.LT(1);
                    match(input,SEMI,FOLLOW_SEMI_in_annotationTypeElementDeclaration4019); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal258_tree = (Object)adaptor.create(char_literal258);
                    adaptor.addChild(root_0, char_literal258_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 52, annotationTypeElementDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end annotationTypeElementDeclaration

    public static class annotationMethodDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start annotationMethodDeclaration
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:791:1: annotationMethodDeclaration : modifiers type IDENTIFIER '(' ')' ( 'default' elementValue )? ';' ;
    public final annotationMethodDeclaration_return annotationMethodDeclaration() throws RecognitionException {
        annotationMethodDeclaration_return retval = new annotationMethodDeclaration_return();
        retval.start = input.LT(1);
        int annotationMethodDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER261=null;
        Token char_literal262=null;
        Token char_literal263=null;
        Token string_literal264=null;
        Token char_literal266=null;
        modifiers_return modifiers259 = null;

        type_return type260 = null;

        elementValue_return elementValue265 = null;


        Object IDENTIFIER261_tree=null;
        Object char_literal262_tree=null;
        Object char_literal263_tree=null;
        Object string_literal264_tree=null;
        Object char_literal266_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 53) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:792:5: ( modifiers type IDENTIFIER '(' ')' ( 'default' elementValue )? ';' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:792:9: modifiers type IDENTIFIER '(' ')' ( 'default' elementValue )? ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_annotationMethodDeclaration4039);
            modifiers259=modifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, modifiers259.getTree());
            pushFollow(FOLLOW_type_in_annotationMethodDeclaration4041);
            type260=type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, type260.getTree());
            IDENTIFIER261=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_annotationMethodDeclaration4043); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER261_tree = (Object)adaptor.create(IDENTIFIER261);
            adaptor.addChild(root_0, IDENTIFIER261_tree);
            }
            char_literal262=(Token)input.LT(1);
            match(input,LPAREN,FOLLOW_LPAREN_in_annotationMethodDeclaration4053); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal262_tree = (Object)adaptor.create(char_literal262);
            adaptor.addChild(root_0, char_literal262_tree);
            }
            char_literal263=(Token)input.LT(1);
            match(input,RPAREN,FOLLOW_RPAREN_in_annotationMethodDeclaration4055); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal263_tree = (Object)adaptor.create(char_literal263);
            adaptor.addChild(root_0, char_literal263_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:793:17: ( 'default' elementValue )?
            int alt88=2;
            int LA88_0 = input.LA(1);

            if ( (LA88_0==DEFAULT) ) {
                alt88=1;
            }
            switch (alt88) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:793:18: 'default' elementValue
                    {
                    string_literal264=(Token)input.LT(1);
                    match(input,DEFAULT,FOLLOW_DEFAULT_in_annotationMethodDeclaration4058); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal264_tree = (Object)adaptor.create(string_literal264);
                    adaptor.addChild(root_0, string_literal264_tree);
                    }
                    pushFollow(FOLLOW_elementValue_in_annotationMethodDeclaration4060);
                    elementValue265=elementValue();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, elementValue265.getTree());

                    }
                    break;

            }

            char_literal266=(Token)input.LT(1);
            match(input,SEMI,FOLLOW_SEMI_in_annotationMethodDeclaration4089); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal266_tree = (Object)adaptor.create(char_literal266);
            adaptor.addChild(root_0, char_literal266_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 53, annotationMethodDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end annotationMethodDeclaration

    public static class block_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start block
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:798:1: block : '{' ( blockStatement )* '}' ;
    public final block_return block() throws RecognitionException {
        block_return retval = new block_return();
        retval.start = input.LT(1);
        int block_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal267=null;
        Token char_literal269=null;
        blockStatement_return blockStatement268 = null;


        Object char_literal267_tree=null;
        Object char_literal269_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 54) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:799:5: ( '{' ( blockStatement )* '}' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:799:9: '{' ( blockStatement )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal267=(Token)input.LT(1);
            match(input,LBRACE,FOLLOW_LBRACE_in_block4113); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal267_tree = (Object)adaptor.create(char_literal267);
            adaptor.addChild(root_0, char_literal267_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:800:9: ( blockStatement )*
            loop89:
            do {
                int alt89=2;
                int LA89_0 = input.LA(1);

                if ( ((LA89_0>=IDENTIFIER && LA89_0<=NULL)||(LA89_0>=ABSTRACT && LA89_0<=BYTE)||(LA89_0>=CHAR && LA89_0<=CLASS)||LA89_0==CONTINUE||(LA89_0>=DO && LA89_0<=DOUBLE)||LA89_0==ENUM||LA89_0==FINAL||(LA89_0>=FLOAT && LA89_0<=FOR)||LA89_0==IF||(LA89_0>=INT && LA89_0<=NEW)||(LA89_0>=PRIVATE && LA89_0<=THROW)||(LA89_0>=TRANSIENT && LA89_0<=LPAREN)||LA89_0==LBRACE||LA89_0==SEMI||(LA89_0>=BANG && LA89_0<=TILDE)||(LA89_0>=PLUSPLUS && LA89_0<=SUB)||LA89_0==MONKEYS_AT) ) {
                    alt89=1;
                }


                switch (alt89) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:800:10: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_block4124);
            	    blockStatement268=blockStatement();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, blockStatement268.getTree());

            	    }
            	    break;

            	default :
            	    break loop89;
                }
            } while (true);

            char_literal269=(Token)input.LT(1);
            match(input,RBRACE,FOLLOW_RBRACE_in_block4145); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal269_tree = (Object)adaptor.create(char_literal269);
            adaptor.addChild(root_0, char_literal269_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 54, block_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end block

    public static class blockStatement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start blockStatement
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:829:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );
    public final blockStatement_return blockStatement() throws RecognitionException {
        blockStatement_return retval = new blockStatement_return();
        retval.start = input.LT(1);
        int blockStatement_StartIndex = input.index();
        Object root_0 = null;

        localVariableDeclarationStatement_return localVariableDeclarationStatement270 = null;

        classOrInterfaceDeclaration_return classOrInterfaceDeclaration271 = null;

        statement_return statement272 = null;



        try {
            if ( backtracking>0 && alreadyParsedRule(input, 55) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:830:5: ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement )
            int alt90=3;
            switch ( input.LA(1) ) {
            case FINAL:
                {
                int LA90_1 = input.LA(2);

                if ( (synpred125()) ) {
                    alt90=1;
                }
                else if ( (synpred126()) ) {
                    alt90=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("829:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );", 90, 1, input);

                    throw nvae;
                }
                }
                break;
            case MONKEYS_AT:
                {
                int LA90_2 = input.LA(2);

                if ( (synpred125()) ) {
                    alt90=1;
                }
                else if ( (synpred126()) ) {
                    alt90=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("829:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );", 90, 2, input);

                    throw nvae;
                }
                }
                break;
            case IDENTIFIER:
                {
                int LA90_3 = input.LA(2);

                if ( (synpred125()) ) {
                    alt90=1;
                }
                else if ( (true) ) {
                    alt90=3;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("829:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );", 90, 3, input);

                    throw nvae;
                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                {
                int LA90_4 = input.LA(2);

                if ( (synpred125()) ) {
                    alt90=1;
                }
                else if ( (true) ) {
                    alt90=3;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("829:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );", 90, 4, input);

                    throw nvae;
                }
                }
                break;
            case ABSTRACT:
            case CLASS:
            case ENUM:
            case INTERFACE:
            case NATIVE:
            case PRIVATE:
            case PROTECTED:
            case PUBLIC:
            case STATIC:
            case STRICTFP:
            case TRANSIENT:
            case VOLATILE:
                {
                alt90=2;
                }
                break;
            case SYNCHRONIZED:
                {
                int LA90_11 = input.LA(2);

                if ( (synpred126()) ) {
                    alt90=2;
                }
                else if ( (true) ) {
                    alt90=3;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("829:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );", 90, 11, input);

                    throw nvae;
                }
                }
                break;
            case INTLITERAL:
            case LONGLITERAL:
            case FLOATLITERAL:
            case DOUBLELITERAL:
            case CHARLITERAL:
            case STRINGLITERAL:
            case TRUE:
            case FALSE:
            case NULL:
            case ASSERT:
            case BREAK:
            case CONTINUE:
            case DO:
            case FOR:
            case IF:
            case NEW:
            case RETURN:
            case SUPER:
            case SWITCH:
            case THIS:
            case THROW:
            case TRY:
            case VOID:
            case WHILE:
            case LPAREN:
            case LBRACE:
            case SEMI:
            case BANG:
            case TILDE:
            case PLUSPLUS:
            case SUBSUB:
            case PLUS:
            case SUB:
                {
                alt90=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("829:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );", 90, 0, input);

                throw nvae;
            }

            switch (alt90) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:830:9: localVariableDeclarationStatement
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_localVariableDeclarationStatement_in_blockStatement4167);
                    localVariableDeclarationStatement270=localVariableDeclarationStatement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, localVariableDeclarationStatement270.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:831:9: classOrInterfaceDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_blockStatement4177);
                    classOrInterfaceDeclaration271=classOrInterfaceDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceDeclaration271.getTree());

                    }
                    break;
                case 3 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:832:9: statement
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_statement_in_blockStatement4187);
                    statement272=statement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, statement272.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 55, blockStatement_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end blockStatement

    public static class localVariableDeclarationStatement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start localVariableDeclarationStatement
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:836:1: localVariableDeclarationStatement : localVariableDeclaration ';' ;
    public final localVariableDeclarationStatement_return localVariableDeclarationStatement() throws RecognitionException {
        localVariableDeclarationStatement_return retval = new localVariableDeclarationStatement_return();
        retval.start = input.LT(1);
        int localVariableDeclarationStatement_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal274=null;
        localVariableDeclaration_return localVariableDeclaration273 = null;


        Object char_literal274_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 56) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:837:5: ( localVariableDeclaration ';' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:837:9: localVariableDeclaration ';'
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement4208);
            localVariableDeclaration273=localVariableDeclaration();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, localVariableDeclaration273.getTree());
            char_literal274=(Token)input.LT(1);
            match(input,SEMI,FOLLOW_SEMI_in_localVariableDeclarationStatement4218); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal274_tree = (Object)adaptor.create(char_literal274);
            adaptor.addChild(root_0, char_literal274_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 56, localVariableDeclarationStatement_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end localVariableDeclarationStatement

    public static class localVariableDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start localVariableDeclaration
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:841:1: localVariableDeclaration : variableModifiers type variableDeclarator ( ',' variableDeclarator )* ;
    public final localVariableDeclaration_return localVariableDeclaration() throws RecognitionException {
        localVariableDeclaration_return retval = new localVariableDeclaration_return();
        retval.start = input.LT(1);
        int localVariableDeclaration_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal278=null;
        variableModifiers_return variableModifiers275 = null;

        type_return type276 = null;

        variableDeclarator_return variableDeclarator277 = null;

        variableDeclarator_return variableDeclarator279 = null;


        Object char_literal278_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 57) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:842:5: ( variableModifiers type variableDeclarator ( ',' variableDeclarator )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:842:9: variableModifiers type variableDeclarator ( ',' variableDeclarator )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_localVariableDeclaration4238);
            variableModifiers275=variableModifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, variableModifiers275.getTree());
            pushFollow(FOLLOW_type_in_localVariableDeclaration4240);
            type276=type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, type276.getTree());
            pushFollow(FOLLOW_variableDeclarator_in_localVariableDeclaration4250);
            variableDeclarator277=variableDeclarator();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, variableDeclarator277.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:844:9: ( ',' variableDeclarator )*
            loop91:
            do {
                int alt91=2;
                int LA91_0 = input.LA(1);

                if ( (LA91_0==COMMA) ) {
                    alt91=1;
                }


                switch (alt91) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:844:10: ',' variableDeclarator
            	    {
            	    char_literal278=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_localVariableDeclaration4261); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal278_tree = (Object)adaptor.create(char_literal278);
            	    adaptor.addChild(root_0, char_literal278_tree);
            	    }
            	    pushFollow(FOLLOW_variableDeclarator_in_localVariableDeclaration4263);
            	    variableDeclarator279=variableDeclarator();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, variableDeclarator279.getTree());

            	    }
            	    break;

            	default :
            	    break loop91;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 57, localVariableDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end localVariableDeclaration

    public static class statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start statement
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:848:1: statement : ( block | ( 'assert' ) expression ( ':' expression )? ';' | 'assert' expression ( ':' expression )? ';' | 'if' parExpression statement ( 'else' statement )? | forstatement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | trystatement | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( IDENTIFIER )? ';' | 'continue' ( IDENTIFIER )? ';' | expression ';' | IDENTIFIER ':' statement | ';' );
    public final statement_return statement() throws RecognitionException {
        statement_return retval = new statement_return();
        retval.start = input.LT(1);
        int statement_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal281=null;
        Token char_literal283=null;
        Token char_literal285=null;
        Token string_literal286=null;
        Token char_literal288=null;
        Token char_literal290=null;
        Token string_literal291=null;
        Token string_literal294=null;
        Token string_literal297=null;
        Token string_literal300=null;
        Token string_literal302=null;
        Token char_literal304=null;
        Token string_literal306=null;
        Token char_literal308=null;
        Token char_literal310=null;
        Token string_literal311=null;
        Token string_literal314=null;
        Token char_literal316=null;
        Token string_literal317=null;
        Token char_literal319=null;
        Token string_literal320=null;
        Token IDENTIFIER321=null;
        Token char_literal322=null;
        Token string_literal323=null;
        Token IDENTIFIER324=null;
        Token char_literal325=null;
        Token char_literal327=null;
        Token IDENTIFIER328=null;
        Token char_literal329=null;
        Token char_literal331=null;
        block_return block280 = null;

        expression_return expression282 = null;

        expression_return expression284 = null;

        expression_return expression287 = null;

        expression_return expression289 = null;

        parExpression_return parExpression292 = null;

        statement_return statement293 = null;

        statement_return statement295 = null;

        forstatement_return forstatement296 = null;

        parExpression_return parExpression298 = null;

        statement_return statement299 = null;

        statement_return statement301 = null;

        parExpression_return parExpression303 = null;

        trystatement_return trystatement305 = null;

        parExpression_return parExpression307 = null;

        switchBlockStatementGroups_return switchBlockStatementGroups309 = null;

        parExpression_return parExpression312 = null;

        block_return block313 = null;

        expression_return expression315 = null;

        expression_return expression318 = null;

        expression_return expression326 = null;

        statement_return statement330 = null;


        Object string_literal281_tree=null;
        Object char_literal283_tree=null;
        Object char_literal285_tree=null;
        Object string_literal286_tree=null;
        Object char_literal288_tree=null;
        Object char_literal290_tree=null;
        Object string_literal291_tree=null;
        Object string_literal294_tree=null;
        Object string_literal297_tree=null;
        Object string_literal300_tree=null;
        Object string_literal302_tree=null;
        Object char_literal304_tree=null;
        Object string_literal306_tree=null;
        Object char_literal308_tree=null;
        Object char_literal310_tree=null;
        Object string_literal311_tree=null;
        Object string_literal314_tree=null;
        Object char_literal316_tree=null;
        Object string_literal317_tree=null;
        Object char_literal319_tree=null;
        Object string_literal320_tree=null;
        Object IDENTIFIER321_tree=null;
        Object char_literal322_tree=null;
        Object string_literal323_tree=null;
        Object IDENTIFIER324_tree=null;
        Object char_literal325_tree=null;
        Object char_literal327_tree=null;
        Object IDENTIFIER328_tree=null;
        Object char_literal329_tree=null;
        Object char_literal331_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 58) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:849:5: ( block | ( 'assert' ) expression ( ':' expression )? ';' | 'assert' expression ( ':' expression )? ';' | 'if' parExpression statement ( 'else' statement )? | forstatement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | trystatement | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( IDENTIFIER )? ';' | 'continue' ( IDENTIFIER )? ';' | expression ';' | IDENTIFIER ':' statement | ';' )
            int alt98=17;
            switch ( input.LA(1) ) {
            case LBRACE:
                {
                alt98=1;
                }
                break;
            case ASSERT:
                {
                int LA98_2 = input.LA(2);

                if ( (synpred130()) ) {
                    alt98=2;
                }
                else if ( (synpred132()) ) {
                    alt98=3;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("848:1: statement : ( block | ( 'assert' ) expression ( ':' expression )? ';' | 'assert' expression ( ':' expression )? ';' | 'if' parExpression statement ( 'else' statement )? | forstatement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | trystatement | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( IDENTIFIER )? ';' | 'continue' ( IDENTIFIER )? ';' | expression ';' | IDENTIFIER ':' statement | ';' );", 98, 2, input);

                    throw nvae;
                }
                }
                break;
            case IF:
                {
                alt98=4;
                }
                break;
            case FOR:
                {
                alt98=5;
                }
                break;
            case WHILE:
                {
                alt98=6;
                }
                break;
            case DO:
                {
                alt98=7;
                }
                break;
            case TRY:
                {
                alt98=8;
                }
                break;
            case SWITCH:
                {
                alt98=9;
                }
                break;
            case SYNCHRONIZED:
                {
                alt98=10;
                }
                break;
            case RETURN:
                {
                alt98=11;
                }
                break;
            case THROW:
                {
                alt98=12;
                }
                break;
            case BREAK:
                {
                alt98=13;
                }
                break;
            case CONTINUE:
                {
                alt98=14;
                }
                break;
            case INTLITERAL:
            case LONGLITERAL:
            case FLOATLITERAL:
            case DOUBLELITERAL:
            case CHARLITERAL:
            case STRINGLITERAL:
            case TRUE:
            case FALSE:
            case NULL:
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case NEW:
            case SHORT:
            case SUPER:
            case THIS:
            case VOID:
            case LPAREN:
            case BANG:
            case TILDE:
            case PLUSPLUS:
            case SUBSUB:
            case PLUS:
            case SUB:
                {
                alt98=15;
                }
                break;
            case IDENTIFIER:
                {
                int LA98_22 = input.LA(2);

                if ( (synpred148()) ) {
                    alt98=15;
                }
                else if ( (synpred149()) ) {
                    alt98=16;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("848:1: statement : ( block | ( 'assert' ) expression ( ':' expression )? ';' | 'assert' expression ( ':' expression )? ';' | 'if' parExpression statement ( 'else' statement )? | forstatement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | trystatement | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( IDENTIFIER )? ';' | 'continue' ( IDENTIFIER )? ';' | expression ';' | IDENTIFIER ':' statement | ';' );", 98, 22, input);

                    throw nvae;
                }
                }
                break;
            case SEMI:
                {
                alt98=17;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("848:1: statement : ( block | ( 'assert' ) expression ( ':' expression )? ';' | 'assert' expression ( ':' expression )? ';' | 'if' parExpression statement ( 'else' statement )? | forstatement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | trystatement | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( IDENTIFIER )? ';' | 'continue' ( IDENTIFIER )? ';' | expression ';' | IDENTIFIER ':' statement | ';' );", 98, 0, input);

                throw nvae;
            }

            switch (alt98) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:849:9: block
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_block_in_statement4294);
                    block280=block();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, block280.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:851:9: ( 'assert' ) expression ( ':' expression )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:851:9: ( 'assert' )
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:851:10: 'assert'
                    {
                    string_literal281=(Token)input.LT(1);
                    match(input,ASSERT,FOLLOW_ASSERT_in_statement4318); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal281_tree = (Object)adaptor.create(string_literal281);
                    adaptor.addChild(root_0, string_literal281_tree);
                    }

                    }

                    pushFollow(FOLLOW_expression_in_statement4338);
                    expression282=expression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expression282.getTree());
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:853:20: ( ':' expression )?
                    int alt92=2;
                    int LA92_0 = input.LA(1);

                    if ( (LA92_0==COLON) ) {
                        alt92=1;
                    }
                    switch (alt92) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:853:21: ':' expression
                            {
                            char_literal283=(Token)input.LT(1);
                            match(input,COLON,FOLLOW_COLON_in_statement4341); if (failed) return retval;
                            if ( backtracking==0 ) {
                            char_literal283_tree = (Object)adaptor.create(char_literal283);
                            adaptor.addChild(root_0, char_literal283_tree);
                            }
                            pushFollow(FOLLOW_expression_in_statement4343);
                            expression284=expression();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, expression284.getTree());

                            }
                            break;

                    }

                    char_literal285=(Token)input.LT(1);
                    match(input,SEMI,FOLLOW_SEMI_in_statement4347); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal285_tree = (Object)adaptor.create(char_literal285);
                    adaptor.addChild(root_0, char_literal285_tree);
                    }

                    }
                    break;
                case 3 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:854:9: 'assert' expression ( ':' expression )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal286=(Token)input.LT(1);
                    match(input,ASSERT,FOLLOW_ASSERT_in_statement4357); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal286_tree = (Object)adaptor.create(string_literal286);
                    adaptor.addChild(root_0, string_literal286_tree);
                    }
                    pushFollow(FOLLOW_expression_in_statement4360);
                    expression287=expression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expression287.getTree());
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:854:30: ( ':' expression )?
                    int alt93=2;
                    int LA93_0 = input.LA(1);

                    if ( (LA93_0==COLON) ) {
                        alt93=1;
                    }
                    switch (alt93) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:854:31: ':' expression
                            {
                            char_literal288=(Token)input.LT(1);
                            match(input,COLON,FOLLOW_COLON_in_statement4363); if (failed) return retval;
                            if ( backtracking==0 ) {
                            char_literal288_tree = (Object)adaptor.create(char_literal288);
                            adaptor.addChild(root_0, char_literal288_tree);
                            }
                            pushFollow(FOLLOW_expression_in_statement4365);
                            expression289=expression();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, expression289.getTree());

                            }
                            break;

                    }

                    char_literal290=(Token)input.LT(1);
                    match(input,SEMI,FOLLOW_SEMI_in_statement4369); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal290_tree = (Object)adaptor.create(char_literal290);
                    adaptor.addChild(root_0, char_literal290_tree);
                    }

                    }
                    break;
                case 4 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:855:9: 'if' parExpression statement ( 'else' statement )?
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal291=(Token)input.LT(1);
                    match(input,IF,FOLLOW_IF_in_statement4391); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal291_tree = (Object)adaptor.create(string_literal291);
                    adaptor.addChild(root_0, string_literal291_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement4393);
                    parExpression292=parExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, parExpression292.getTree());
                    pushFollow(FOLLOW_statement_in_statement4395);
                    statement293=statement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, statement293.getTree());
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:855:38: ( 'else' statement )?
                    int alt94=2;
                    int LA94_0 = input.LA(1);

                    if ( (LA94_0==ELSE) ) {
                        int LA94_1 = input.LA(2);

                        if ( (synpred133()) ) {
                            alt94=1;
                        }
                    }
                    switch (alt94) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:855:39: 'else' statement
                            {
                            string_literal294=(Token)input.LT(1);
                            match(input,ELSE,FOLLOW_ELSE_in_statement4398); if (failed) return retval;
                            if ( backtracking==0 ) {
                            string_literal294_tree = (Object)adaptor.create(string_literal294);
                            adaptor.addChild(root_0, string_literal294_tree);
                            }
                            pushFollow(FOLLOW_statement_in_statement4400);
                            statement295=statement();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, statement295.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:856:9: forstatement
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_forstatement_in_statement4422);
                    forstatement296=forstatement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, forstatement296.getTree());

                    }
                    break;
                case 6 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:857:9: 'while' parExpression statement
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal297=(Token)input.LT(1);
                    match(input,WHILE,FOLLOW_WHILE_in_statement4432); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal297_tree = (Object)adaptor.create(string_literal297);
                    adaptor.addChild(root_0, string_literal297_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement4434);
                    parExpression298=parExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, parExpression298.getTree());
                    pushFollow(FOLLOW_statement_in_statement4436);
                    statement299=statement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, statement299.getTree());

                    }
                    break;
                case 7 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:858:9: 'do' statement 'while' parExpression ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal300=(Token)input.LT(1);
                    match(input,DO,FOLLOW_DO_in_statement4446); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal300_tree = (Object)adaptor.create(string_literal300);
                    adaptor.addChild(root_0, string_literal300_tree);
                    }
                    pushFollow(FOLLOW_statement_in_statement4448);
                    statement301=statement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, statement301.getTree());
                    string_literal302=(Token)input.LT(1);
                    match(input,WHILE,FOLLOW_WHILE_in_statement4450); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal302_tree = (Object)adaptor.create(string_literal302);
                    adaptor.addChild(root_0, string_literal302_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement4452);
                    parExpression303=parExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, parExpression303.getTree());
                    char_literal304=(Token)input.LT(1);
                    match(input,SEMI,FOLLOW_SEMI_in_statement4454); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal304_tree = (Object)adaptor.create(char_literal304);
                    adaptor.addChild(root_0, char_literal304_tree);
                    }

                    }
                    break;
                case 8 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:859:9: trystatement
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_trystatement_in_statement4464);
                    trystatement305=trystatement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, trystatement305.getTree());

                    }
                    break;
                case 9 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:860:9: 'switch' parExpression '{' switchBlockStatementGroups '}'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal306=(Token)input.LT(1);
                    match(input,SWITCH,FOLLOW_SWITCH_in_statement4474); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal306_tree = (Object)adaptor.create(string_literal306);
                    adaptor.addChild(root_0, string_literal306_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement4476);
                    parExpression307=parExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, parExpression307.getTree());
                    char_literal308=(Token)input.LT(1);
                    match(input,LBRACE,FOLLOW_LBRACE_in_statement4478); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal308_tree = (Object)adaptor.create(char_literal308);
                    adaptor.addChild(root_0, char_literal308_tree);
                    }
                    pushFollow(FOLLOW_switchBlockStatementGroups_in_statement4480);
                    switchBlockStatementGroups309=switchBlockStatementGroups();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, switchBlockStatementGroups309.getTree());
                    char_literal310=(Token)input.LT(1);
                    match(input,RBRACE,FOLLOW_RBRACE_in_statement4482); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal310_tree = (Object)adaptor.create(char_literal310);
                    adaptor.addChild(root_0, char_literal310_tree);
                    }

                    }
                    break;
                case 10 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:861:9: 'synchronized' parExpression block
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal311=(Token)input.LT(1);
                    match(input,SYNCHRONIZED,FOLLOW_SYNCHRONIZED_in_statement4492); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal311_tree = (Object)adaptor.create(string_literal311);
                    adaptor.addChild(root_0, string_literal311_tree);
                    }
                    pushFollow(FOLLOW_parExpression_in_statement4494);
                    parExpression312=parExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, parExpression312.getTree());
                    pushFollow(FOLLOW_block_in_statement4496);
                    block313=block();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, block313.getTree());

                    }
                    break;
                case 11 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:862:9: 'return' ( expression )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal314=(Token)input.LT(1);
                    match(input,RETURN,FOLLOW_RETURN_in_statement4506); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal314_tree = (Object)adaptor.create(string_literal314);
                    adaptor.addChild(root_0, string_literal314_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:862:18: ( expression )?
                    int alt95=2;
                    int LA95_0 = input.LA(1);

                    if ( ((LA95_0>=IDENTIFIER && LA95_0<=NULL)||LA95_0==BOOLEAN||LA95_0==BYTE||LA95_0==CHAR||LA95_0==DOUBLE||LA95_0==FLOAT||LA95_0==INT||LA95_0==LONG||LA95_0==NEW||LA95_0==SHORT||LA95_0==SUPER||LA95_0==THIS||LA95_0==VOID||LA95_0==LPAREN||(LA95_0>=BANG && LA95_0<=TILDE)||(LA95_0>=PLUSPLUS && LA95_0<=SUB)) ) {
                        alt95=1;
                    }
                    switch (alt95) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:862:19: expression
                            {
                            pushFollow(FOLLOW_expression_in_statement4509);
                            expression315=expression();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, expression315.getTree());

                            }
                            break;

                    }

                    char_literal316=(Token)input.LT(1);
                    match(input,SEMI,FOLLOW_SEMI_in_statement4514); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal316_tree = (Object)adaptor.create(char_literal316);
                    adaptor.addChild(root_0, char_literal316_tree);
                    }

                    }
                    break;
                case 12 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:863:9: 'throw' expression ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal317=(Token)input.LT(1);
                    match(input,THROW,FOLLOW_THROW_in_statement4524); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal317_tree = (Object)adaptor.create(string_literal317);
                    adaptor.addChild(root_0, string_literal317_tree);
                    }
                    pushFollow(FOLLOW_expression_in_statement4526);
                    expression318=expression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expression318.getTree());
                    char_literal319=(Token)input.LT(1);
                    match(input,SEMI,FOLLOW_SEMI_in_statement4528); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal319_tree = (Object)adaptor.create(char_literal319);
                    adaptor.addChild(root_0, char_literal319_tree);
                    }

                    }
                    break;
                case 13 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:864:9: 'break' ( IDENTIFIER )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal320=(Token)input.LT(1);
                    match(input,BREAK,FOLLOW_BREAK_in_statement4538); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal320_tree = (Object)adaptor.create(string_literal320);
                    adaptor.addChild(root_0, string_literal320_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:865:13: ( IDENTIFIER )?
                    int alt96=2;
                    int LA96_0 = input.LA(1);

                    if ( (LA96_0==IDENTIFIER) ) {
                        alt96=1;
                    }
                    switch (alt96) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:865:14: IDENTIFIER
                            {
                            IDENTIFIER321=(Token)input.LT(1);
                            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_statement4553); if (failed) return retval;
                            if ( backtracking==0 ) {
                            IDENTIFIER321_tree = (Object)adaptor.create(IDENTIFIER321);
                            adaptor.addChild(root_0, IDENTIFIER321_tree);
                            }

                            }
                            break;

                    }

                    char_literal322=(Token)input.LT(1);
                    match(input,SEMI,FOLLOW_SEMI_in_statement4570); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal322_tree = (Object)adaptor.create(char_literal322);
                    adaptor.addChild(root_0, char_literal322_tree);
                    }

                    }
                    break;
                case 14 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:867:9: 'continue' ( IDENTIFIER )? ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal323=(Token)input.LT(1);
                    match(input,CONTINUE,FOLLOW_CONTINUE_in_statement4580); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal323_tree = (Object)adaptor.create(string_literal323);
                    adaptor.addChild(root_0, string_literal323_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:868:13: ( IDENTIFIER )?
                    int alt97=2;
                    int LA97_0 = input.LA(1);

                    if ( (LA97_0==IDENTIFIER) ) {
                        alt97=1;
                    }
                    switch (alt97) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:868:14: IDENTIFIER
                            {
                            IDENTIFIER324=(Token)input.LT(1);
                            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_statement4595); if (failed) return retval;
                            if ( backtracking==0 ) {
                            IDENTIFIER324_tree = (Object)adaptor.create(IDENTIFIER324);
                            adaptor.addChild(root_0, IDENTIFIER324_tree);
                            }

                            }
                            break;

                    }

                    char_literal325=(Token)input.LT(1);
                    match(input,SEMI,FOLLOW_SEMI_in_statement4612); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal325_tree = (Object)adaptor.create(char_literal325);
                    adaptor.addChild(root_0, char_literal325_tree);
                    }

                    }
                    break;
                case 15 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:870:9: expression ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expression_in_statement4622);
                    expression326=expression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expression326.getTree());
                    char_literal327=(Token)input.LT(1);
                    match(input,SEMI,FOLLOW_SEMI_in_statement4625); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal327_tree = (Object)adaptor.create(char_literal327);
                    adaptor.addChild(root_0, char_literal327_tree);
                    }

                    }
                    break;
                case 16 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:871:9: IDENTIFIER ':' statement
                    {
                    root_0 = (Object)adaptor.nil();

                    IDENTIFIER328=(Token)input.LT(1);
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_statement4640); if (failed) return retval;
                    if ( backtracking==0 ) {
                    IDENTIFIER328_tree = (Object)adaptor.create(IDENTIFIER328);
                    adaptor.addChild(root_0, IDENTIFIER328_tree);
                    }
                    char_literal329=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_statement4642); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal329_tree = (Object)adaptor.create(char_literal329);
                    adaptor.addChild(root_0, char_literal329_tree);
                    }
                    pushFollow(FOLLOW_statement_in_statement4644);
                    statement330=statement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, statement330.getTree());

                    }
                    break;
                case 17 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:872:9: ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal331=(Token)input.LT(1);
                    match(input,SEMI,FOLLOW_SEMI_in_statement4654); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal331_tree = (Object)adaptor.create(char_literal331);
                    adaptor.addChild(root_0, char_literal331_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 58, statement_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end statement

    public static class switchBlockStatementGroups_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start switchBlockStatementGroups
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:876:1: switchBlockStatementGroups : ( switchBlockStatementGroup )* ;
    public final switchBlockStatementGroups_return switchBlockStatementGroups() throws RecognitionException {
        switchBlockStatementGroups_return retval = new switchBlockStatementGroups_return();
        retval.start = input.LT(1);
        int switchBlockStatementGroups_StartIndex = input.index();
        Object root_0 = null;

        switchBlockStatementGroup_return switchBlockStatementGroup332 = null;



        try {
            if ( backtracking>0 && alreadyParsedRule(input, 59) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:877:5: ( ( switchBlockStatementGroup )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:877:9: ( switchBlockStatementGroup )*
            {
            root_0 = (Object)adaptor.nil();

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:877:9: ( switchBlockStatementGroup )*
            loop99:
            do {
                int alt99=2;
                int LA99_0 = input.LA(1);

                if ( (LA99_0==CASE||LA99_0==DEFAULT) ) {
                    alt99=1;
                }


                switch (alt99) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:877:10: switchBlockStatementGroup
            	    {
            	    pushFollow(FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups4676);
            	    switchBlockStatementGroup332=switchBlockStatementGroup();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, switchBlockStatementGroup332.getTree());

            	    }
            	    break;

            	default :
            	    break loop99;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 59, switchBlockStatementGroups_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end switchBlockStatementGroups

    public static class switchBlockStatementGroup_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start switchBlockStatementGroup
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:880:1: switchBlockStatementGroup : switchLabel ( blockStatement )* ;
    public final switchBlockStatementGroup_return switchBlockStatementGroup() throws RecognitionException {
        switchBlockStatementGroup_return retval = new switchBlockStatementGroup_return();
        retval.start = input.LT(1);
        int switchBlockStatementGroup_StartIndex = input.index();
        Object root_0 = null;

        switchLabel_return switchLabel333 = null;

        blockStatement_return blockStatement334 = null;



        try {
            if ( backtracking>0 && alreadyParsedRule(input, 60) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:881:5: ( switchLabel ( blockStatement )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:882:9: switchLabel ( blockStatement )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_switchLabel_in_switchBlockStatementGroup4705);
            switchLabel333=switchLabel();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, switchLabel333.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:883:9: ( blockStatement )*
            loop100:
            do {
                int alt100=2;
                int LA100_0 = input.LA(1);

                if ( ((LA100_0>=IDENTIFIER && LA100_0<=NULL)||(LA100_0>=ABSTRACT && LA100_0<=BYTE)||(LA100_0>=CHAR && LA100_0<=CLASS)||LA100_0==CONTINUE||(LA100_0>=DO && LA100_0<=DOUBLE)||LA100_0==ENUM||LA100_0==FINAL||(LA100_0>=FLOAT && LA100_0<=FOR)||LA100_0==IF||(LA100_0>=INT && LA100_0<=NEW)||(LA100_0>=PRIVATE && LA100_0<=THROW)||(LA100_0>=TRANSIENT && LA100_0<=LPAREN)||LA100_0==LBRACE||LA100_0==SEMI||(LA100_0>=BANG && LA100_0<=TILDE)||(LA100_0>=PLUSPLUS && LA100_0<=SUB)||LA100_0==MONKEYS_AT) ) {
                    alt100=1;
                }


                switch (alt100) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:883:10: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_switchBlockStatementGroup4716);
            	    blockStatement334=blockStatement();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, blockStatement334.getTree());

            	    }
            	    break;

            	default :
            	    break loop100;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 60, switchBlockStatementGroup_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end switchBlockStatementGroup

    public static class switchLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start switchLabel
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:887:1: switchLabel : ( 'case' expression ':' | 'default' ':' );
    public final switchLabel_return switchLabel() throws RecognitionException {
        switchLabel_return retval = new switchLabel_return();
        retval.start = input.LT(1);
        int switchLabel_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal335=null;
        Token char_literal337=null;
        Token string_literal338=null;
        Token char_literal339=null;
        expression_return expression336 = null;


        Object string_literal335_tree=null;
        Object char_literal337_tree=null;
        Object string_literal338_tree=null;
        Object char_literal339_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 61) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:888:5: ( 'case' expression ':' | 'default' ':' )
            int alt101=2;
            int LA101_0 = input.LA(1);

            if ( (LA101_0==CASE) ) {
                alt101=1;
            }
            else if ( (LA101_0==DEFAULT) ) {
                alt101=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("887:1: switchLabel : ( 'case' expression ':' | 'default' ':' );", 101, 0, input);

                throw nvae;
            }
            switch (alt101) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:888:9: 'case' expression ':'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal335=(Token)input.LT(1);
                    match(input,CASE,FOLLOW_CASE_in_switchLabel4747); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal335_tree = (Object)adaptor.create(string_literal335);
                    adaptor.addChild(root_0, string_literal335_tree);
                    }
                    pushFollow(FOLLOW_expression_in_switchLabel4749);
                    expression336=expression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expression336.getTree());
                    char_literal337=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_switchLabel4751); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal337_tree = (Object)adaptor.create(char_literal337);
                    adaptor.addChild(root_0, char_literal337_tree);
                    }

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:889:9: 'default' ':'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal338=(Token)input.LT(1);
                    match(input,DEFAULT,FOLLOW_DEFAULT_in_switchLabel4761); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal338_tree = (Object)adaptor.create(string_literal338);
                    adaptor.addChild(root_0, string_literal338_tree);
                    }
                    char_literal339=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_switchLabel4763); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal339_tree = (Object)adaptor.create(char_literal339);
                    adaptor.addChild(root_0, char_literal339_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 61, switchLabel_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end switchLabel

    public static class trystatement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start trystatement
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:893:1: trystatement : 'try' block ( catches 'finally' block | catches | 'finally' block ) ;
    public final trystatement_return trystatement() throws RecognitionException {
        trystatement_return retval = new trystatement_return();
        retval.start = input.LT(1);
        int trystatement_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal340=null;
        Token string_literal343=null;
        Token string_literal346=null;
        block_return block341 = null;

        catches_return catches342 = null;

        block_return block344 = null;

        catches_return catches345 = null;

        block_return block347 = null;


        Object string_literal340_tree=null;
        Object string_literal343_tree=null;
        Object string_literal346_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 62) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:894:5: ( 'try' block ( catches 'finally' block | catches | 'finally' block ) )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:894:9: 'try' block ( catches 'finally' block | catches | 'finally' block )
            {
            root_0 = (Object)adaptor.nil();

            string_literal340=(Token)input.LT(1);
            match(input,TRY,FOLLOW_TRY_in_trystatement4784); if (failed) return retval;
            if ( backtracking==0 ) {
            string_literal340_tree = (Object)adaptor.create(string_literal340);
            adaptor.addChild(root_0, string_literal340_tree);
            }
            pushFollow(FOLLOW_block_in_trystatement4786);
            block341=block();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, block341.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:895:9: ( catches 'finally' block | catches | 'finally' block )
            int alt102=3;
            int LA102_0 = input.LA(1);

            if ( (LA102_0==CATCH) ) {
                int LA102_1 = input.LA(2);

                if ( (synpred153()) ) {
                    alt102=1;
                }
                else if ( (synpred154()) ) {
                    alt102=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("895:9: ( catches 'finally' block | catches | 'finally' block )", 102, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA102_0==FINALLY) ) {
                alt102=3;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("895:9: ( catches 'finally' block | catches | 'finally' block )", 102, 0, input);

                throw nvae;
            }
            switch (alt102) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:895:13: catches 'finally' block
                    {
                    pushFollow(FOLLOW_catches_in_trystatement4800);
                    catches342=catches();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, catches342.getTree());
                    string_literal343=(Token)input.LT(1);
                    match(input,FINALLY,FOLLOW_FINALLY_in_trystatement4802); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal343_tree = (Object)adaptor.create(string_literal343);
                    adaptor.addChild(root_0, string_literal343_tree);
                    }
                    pushFollow(FOLLOW_block_in_trystatement4804);
                    block344=block();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, block344.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:896:13: catches
                    {
                    pushFollow(FOLLOW_catches_in_trystatement4818);
                    catches345=catches();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, catches345.getTree());

                    }
                    break;
                case 3 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:897:13: 'finally' block
                    {
                    string_literal346=(Token)input.LT(1);
                    match(input,FINALLY,FOLLOW_FINALLY_in_trystatement4832); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal346_tree = (Object)adaptor.create(string_literal346);
                    adaptor.addChild(root_0, string_literal346_tree);
                    }
                    pushFollow(FOLLOW_block_in_trystatement4834);
                    block347=block();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, block347.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 62, trystatement_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end trystatement

    public static class catches_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start catches
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:901:1: catches : catchClause ( catchClause )* ;
    public final catches_return catches() throws RecognitionException {
        catches_return retval = new catches_return();
        retval.start = input.LT(1);
        int catches_StartIndex = input.index();
        Object root_0 = null;

        catchClause_return catchClause348 = null;

        catchClause_return catchClause349 = null;



        try {
            if ( backtracking>0 && alreadyParsedRule(input, 63) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:902:5: ( catchClause ( catchClause )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:902:9: catchClause ( catchClause )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_catchClause_in_catches4865);
            catchClause348=catchClause();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, catchClause348.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:903:9: ( catchClause )*
            loop103:
            do {
                int alt103=2;
                int LA103_0 = input.LA(1);

                if ( (LA103_0==CATCH) ) {
                    alt103=1;
                }


                switch (alt103) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:903:10: catchClause
            	    {
            	    pushFollow(FOLLOW_catchClause_in_catches4876);
            	    catchClause349=catchClause();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, catchClause349.getTree());

            	    }
            	    break;

            	default :
            	    break loop103;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 63, catches_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end catches

    public static class catchClause_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start catchClause
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:907:1: catchClause : 'catch' '(' formalParameter ')' block ;
    public final catchClause_return catchClause() throws RecognitionException {
        catchClause_return retval = new catchClause_return();
        retval.start = input.LT(1);
        int catchClause_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal350=null;
        Token char_literal351=null;
        Token char_literal353=null;
        formalParameter_return formalParameter352 = null;

        block_return block354 = null;


        Object string_literal350_tree=null;
        Object char_literal351_tree=null;
        Object char_literal353_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 64) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:908:5: ( 'catch' '(' formalParameter ')' block )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:908:9: 'catch' '(' formalParameter ')' block
            {
            root_0 = (Object)adaptor.nil();

            string_literal350=(Token)input.LT(1);
            match(input,CATCH,FOLLOW_CATCH_in_catchClause4907); if (failed) return retval;
            if ( backtracking==0 ) {
            string_literal350_tree = (Object)adaptor.create(string_literal350);
            adaptor.addChild(root_0, string_literal350_tree);
            }
            char_literal351=(Token)input.LT(1);
            match(input,LPAREN,FOLLOW_LPAREN_in_catchClause4909); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal351_tree = (Object)adaptor.create(char_literal351);
            adaptor.addChild(root_0, char_literal351_tree);
            }
            pushFollow(FOLLOW_formalParameter_in_catchClause4911);
            formalParameter352=formalParameter();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, formalParameter352.getTree());
            char_literal353=(Token)input.LT(1);
            match(input,RPAREN,FOLLOW_RPAREN_in_catchClause4921); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal353_tree = (Object)adaptor.create(char_literal353);
            adaptor.addChild(root_0, char_literal353_tree);
            }
            pushFollow(FOLLOW_block_in_catchClause4923);
            block354=block();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, block354.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 64, catchClause_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end catchClause

    public static class formalParameter_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start formalParameter
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:912:1: formalParameter : variableModifiers type IDENTIFIER ( '[' ']' )* ;
    public final formalParameter_return formalParameter() throws RecognitionException {
        formalParameter_return retval = new formalParameter_return();
        retval.start = input.LT(1);
        int formalParameter_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER357=null;
        Token char_literal358=null;
        Token char_literal359=null;
        variableModifiers_return variableModifiers355 = null;

        type_return type356 = null;


        Object IDENTIFIER357_tree=null;
        Object char_literal358_tree=null;
        Object char_literal359_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 65) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:913:5: ( variableModifiers type IDENTIFIER ( '[' ']' )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:913:9: variableModifiers type IDENTIFIER ( '[' ']' )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_formalParameter4944);
            variableModifiers355=variableModifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, variableModifiers355.getTree());
            pushFollow(FOLLOW_type_in_formalParameter4946);
            type356=type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, type356.getTree());
            IDENTIFIER357=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_formalParameter4948); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER357_tree = (Object)adaptor.create(IDENTIFIER357);
            adaptor.addChild(root_0, IDENTIFIER357_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:914:9: ( '[' ']' )*
            loop104:
            do {
                int alt104=2;
                int LA104_0 = input.LA(1);

                if ( (LA104_0==LBRACKET) ) {
                    alt104=1;
                }


                switch (alt104) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:914:10: '[' ']'
            	    {
            	    char_literal358=(Token)input.LT(1);
            	    match(input,LBRACKET,FOLLOW_LBRACKET_in_formalParameter4959); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal358_tree = (Object)adaptor.create(char_literal358);
            	    adaptor.addChild(root_0, char_literal358_tree);
            	    }
            	    char_literal359=(Token)input.LT(1);
            	    match(input,RBRACKET,FOLLOW_RBRACKET_in_formalParameter4961); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal359_tree = (Object)adaptor.create(char_literal359);
            	    adaptor.addChild(root_0, char_literal359_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop104;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 65, formalParameter_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end formalParameter

    public static class forstatement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start forstatement
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:918:1: forstatement : ( 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement | 'for' '(' ( forInit )? ';' ( expression )? ';' ( expressionList )? ')' statement );
    public final forstatement_return forstatement() throws RecognitionException {
        forstatement_return retval = new forstatement_return();
        retval.start = input.LT(1);
        int forstatement_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal360=null;
        Token char_literal361=null;
        Token IDENTIFIER364=null;
        Token char_literal365=null;
        Token char_literal367=null;
        Token string_literal369=null;
        Token char_literal370=null;
        Token char_literal372=null;
        Token char_literal374=null;
        Token char_literal376=null;
        variableModifiers_return variableModifiers362 = null;

        type_return type363 = null;

        expression_return expression366 = null;

        statement_return statement368 = null;

        forInit_return forInit371 = null;

        expression_return expression373 = null;

        expressionList_return expressionList375 = null;

        statement_return statement377 = null;


        Object string_literal360_tree=null;
        Object char_literal361_tree=null;
        Object IDENTIFIER364_tree=null;
        Object char_literal365_tree=null;
        Object char_literal367_tree=null;
        Object string_literal369_tree=null;
        Object char_literal370_tree=null;
        Object char_literal372_tree=null;
        Object char_literal374_tree=null;
        Object char_literal376_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 66) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:919:5: ( 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement | 'for' '(' ( forInit )? ';' ( expression )? ';' ( expressionList )? ')' statement )
            int alt108=2;
            int LA108_0 = input.LA(1);

            if ( (LA108_0==FOR) ) {
                int LA108_1 = input.LA(2);

                if ( (synpred157()) ) {
                    alt108=1;
                }
                else if ( (true) ) {
                    alt108=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("918:1: forstatement : ( 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement | 'for' '(' ( forInit )? ';' ( expression )? ';' ( expressionList )? ')' statement );", 108, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("918:1: forstatement : ( 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement | 'for' '(' ( forInit )? ';' ( expression )? ';' ( expressionList )? ')' statement );", 108, 0, input);

                throw nvae;
            }
            switch (alt108) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:921:9: 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal360=(Token)input.LT(1);
                    match(input,FOR,FOLLOW_FOR_in_forstatement5010); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal360_tree = (Object)adaptor.create(string_literal360);
                    adaptor.addChild(root_0, string_literal360_tree);
                    }
                    char_literal361=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_forstatement5012); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal361_tree = (Object)adaptor.create(char_literal361);
                    adaptor.addChild(root_0, char_literal361_tree);
                    }
                    pushFollow(FOLLOW_variableModifiers_in_forstatement5014);
                    variableModifiers362=variableModifiers();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, variableModifiers362.getTree());
                    pushFollow(FOLLOW_type_in_forstatement5016);
                    type363=type();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, type363.getTree());
                    IDENTIFIER364=(Token)input.LT(1);
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_forstatement5018); if (failed) return retval;
                    if ( backtracking==0 ) {
                    IDENTIFIER364_tree = (Object)adaptor.create(IDENTIFIER364);
                    adaptor.addChild(root_0, IDENTIFIER364_tree);
                    }
                    char_literal365=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_forstatement5020); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal365_tree = (Object)adaptor.create(char_literal365);
                    adaptor.addChild(root_0, char_literal365_tree);
                    }
                    pushFollow(FOLLOW_expression_in_forstatement5031);
                    expression366=expression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expression366.getTree());
                    char_literal367=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_forstatement5033); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal367_tree = (Object)adaptor.create(char_literal367);
                    adaptor.addChild(root_0, char_literal367_tree);
                    }
                    pushFollow(FOLLOW_statement_in_forstatement5035);
                    statement368=statement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, statement368.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:925:9: 'for' '(' ( forInit )? ';' ( expression )? ';' ( expressionList )? ')' statement
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal369=(Token)input.LT(1);
                    match(input,FOR,FOLLOW_FOR_in_forstatement5067); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal369_tree = (Object)adaptor.create(string_literal369);
                    adaptor.addChild(root_0, string_literal369_tree);
                    }
                    char_literal370=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_forstatement5069); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal370_tree = (Object)adaptor.create(char_literal370);
                    adaptor.addChild(root_0, char_literal370_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:926:17: ( forInit )?
                    int alt105=2;
                    int LA105_0 = input.LA(1);

                    if ( ((LA105_0>=IDENTIFIER && LA105_0<=NULL)||LA105_0==BOOLEAN||LA105_0==BYTE||LA105_0==CHAR||LA105_0==DOUBLE||LA105_0==FINAL||LA105_0==FLOAT||LA105_0==INT||LA105_0==LONG||LA105_0==NEW||LA105_0==SHORT||LA105_0==SUPER||LA105_0==THIS||LA105_0==VOID||LA105_0==LPAREN||(LA105_0>=BANG && LA105_0<=TILDE)||(LA105_0>=PLUSPLUS && LA105_0<=SUB)||LA105_0==MONKEYS_AT) ) {
                        alt105=1;
                    }
                    switch (alt105) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:926:18: forInit
                            {
                            pushFollow(FOLLOW_forInit_in_forstatement5089);
                            forInit371=forInit();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, forInit371.getTree());

                            }
                            break;

                    }

                    char_literal372=(Token)input.LT(1);
                    match(input,SEMI,FOLLOW_SEMI_in_forstatement5110); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal372_tree = (Object)adaptor.create(char_literal372);
                    adaptor.addChild(root_0, char_literal372_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:928:17: ( expression )?
                    int alt106=2;
                    int LA106_0 = input.LA(1);

                    if ( ((LA106_0>=IDENTIFIER && LA106_0<=NULL)||LA106_0==BOOLEAN||LA106_0==BYTE||LA106_0==CHAR||LA106_0==DOUBLE||LA106_0==FLOAT||LA106_0==INT||LA106_0==LONG||LA106_0==NEW||LA106_0==SHORT||LA106_0==SUPER||LA106_0==THIS||LA106_0==VOID||LA106_0==LPAREN||(LA106_0>=BANG && LA106_0<=TILDE)||(LA106_0>=PLUSPLUS && LA106_0<=SUB)) ) {
                        alt106=1;
                    }
                    switch (alt106) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:928:18: expression
                            {
                            pushFollow(FOLLOW_expression_in_forstatement5130);
                            expression373=expression();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, expression373.getTree());

                            }
                            break;

                    }

                    char_literal374=(Token)input.LT(1);
                    match(input,SEMI,FOLLOW_SEMI_in_forstatement5151); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal374_tree = (Object)adaptor.create(char_literal374);
                    adaptor.addChild(root_0, char_literal374_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:930:17: ( expressionList )?
                    int alt107=2;
                    int LA107_0 = input.LA(1);

                    if ( ((LA107_0>=IDENTIFIER && LA107_0<=NULL)||LA107_0==BOOLEAN||LA107_0==BYTE||LA107_0==CHAR||LA107_0==DOUBLE||LA107_0==FLOAT||LA107_0==INT||LA107_0==LONG||LA107_0==NEW||LA107_0==SHORT||LA107_0==SUPER||LA107_0==THIS||LA107_0==VOID||LA107_0==LPAREN||(LA107_0>=BANG && LA107_0<=TILDE)||(LA107_0>=PLUSPLUS && LA107_0<=SUB)) ) {
                        alt107=1;
                    }
                    switch (alt107) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:930:18: expressionList
                            {
                            pushFollow(FOLLOW_expressionList_in_forstatement5171);
                            expressionList375=expressionList();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, expressionList375.getTree());

                            }
                            break;

                    }

                    char_literal376=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_forstatement5192); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal376_tree = (Object)adaptor.create(char_literal376);
                    adaptor.addChild(root_0, char_literal376_tree);
                    }
                    pushFollow(FOLLOW_statement_in_forstatement5194);
                    statement377=statement();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, statement377.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 66, forstatement_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end forstatement

    public static class forInit_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start forInit
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:934:1: forInit : ( localVariableDeclaration | expressionList );
    public final forInit_return forInit() throws RecognitionException {
        forInit_return retval = new forInit_return();
        retval.start = input.LT(1);
        int forInit_StartIndex = input.index();
        Object root_0 = null;

        localVariableDeclaration_return localVariableDeclaration378 = null;

        expressionList_return expressionList379 = null;



        try {
            if ( backtracking>0 && alreadyParsedRule(input, 67) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:935:5: ( localVariableDeclaration | expressionList )
            int alt109=2;
            switch ( input.LA(1) ) {
            case FINAL:
            case MONKEYS_AT:
                {
                alt109=1;
                }
                break;
            case IDENTIFIER:
                {
                int LA109_3 = input.LA(2);

                if ( (synpred161()) ) {
                    alt109=1;
                }
                else if ( (true) ) {
                    alt109=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("934:1: forInit : ( localVariableDeclaration | expressionList );", 109, 3, input);

                    throw nvae;
                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                {
                int LA109_4 = input.LA(2);

                if ( (synpred161()) ) {
                    alt109=1;
                }
                else if ( (true) ) {
                    alt109=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("934:1: forInit : ( localVariableDeclaration | expressionList );", 109, 4, input);

                    throw nvae;
                }
                }
                break;
            case INTLITERAL:
            case LONGLITERAL:
            case FLOATLITERAL:
            case DOUBLELITERAL:
            case CHARLITERAL:
            case STRINGLITERAL:
            case TRUE:
            case FALSE:
            case NULL:
            case NEW:
            case SUPER:
            case THIS:
            case VOID:
            case LPAREN:
            case BANG:
            case TILDE:
            case PLUSPLUS:
            case SUBSUB:
            case PLUS:
            case SUB:
                {
                alt109=2;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("934:1: forInit : ( localVariableDeclaration | expressionList );", 109, 0, input);

                throw nvae;
            }

            switch (alt109) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:935:9: localVariableDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_localVariableDeclaration_in_forInit5214);
                    localVariableDeclaration378=localVariableDeclaration();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, localVariableDeclaration378.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:936:9: expressionList
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expressionList_in_forInit5224);
                    expressionList379=expressionList();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expressionList379.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 67, forInit_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end forInit

    public static class parExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start parExpression
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:939:1: parExpression : '(' expression ')' ;
    public final parExpression_return parExpression() throws RecognitionException {
        parExpression_return retval = new parExpression_return();
        retval.start = input.LT(1);
        int parExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal380=null;
        Token char_literal382=null;
        expression_return expression381 = null;


        Object char_literal380_tree=null;
        Object char_literal382_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 68) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:940:5: ( '(' expression ')' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:940:9: '(' expression ')'
            {
            root_0 = (Object)adaptor.nil();

            char_literal380=(Token)input.LT(1);
            match(input,LPAREN,FOLLOW_LPAREN_in_parExpression5244); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal380_tree = (Object)adaptor.create(char_literal380);
            adaptor.addChild(root_0, char_literal380_tree);
            }
            pushFollow(FOLLOW_expression_in_parExpression5246);
            expression381=expression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, expression381.getTree());
            char_literal382=(Token)input.LT(1);
            match(input,RPAREN,FOLLOW_RPAREN_in_parExpression5248); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal382_tree = (Object)adaptor.create(char_literal382);
            adaptor.addChild(root_0, char_literal382_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 68, parExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end parExpression

    public static class expressionList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expressionList
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:943:1: expressionList : expression ( ',' expression )* ;
    public final expressionList_return expressionList() throws RecognitionException {
        expressionList_return retval = new expressionList_return();
        retval.start = input.LT(1);
        int expressionList_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal384=null;
        expression_return expression383 = null;

        expression_return expression385 = null;


        Object char_literal384_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 69) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:944:5: ( expression ( ',' expression )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:944:9: expression ( ',' expression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_expressionList5268);
            expression383=expression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, expression383.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:945:9: ( ',' expression )*
            loop110:
            do {
                int alt110=2;
                int LA110_0 = input.LA(1);

                if ( (LA110_0==COMMA) ) {
                    alt110=1;
                }


                switch (alt110) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:945:10: ',' expression
            	    {
            	    char_literal384=(Token)input.LT(1);
            	    match(input,COMMA,FOLLOW_COMMA_in_expressionList5279); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal384_tree = (Object)adaptor.create(char_literal384);
            	    adaptor.addChild(root_0, char_literal384_tree);
            	    }
            	    pushFollow(FOLLOW_expression_in_expressionList5281);
            	    expression385=expression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, expression385.getTree());

            	    }
            	    break;

            	default :
            	    break loop110;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 69, expressionList_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end expressionList

    public static class expression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expression
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:950:1: expression : conditionalExpression ( assignmentOperator expression )? ;
    public final expression_return expression() throws RecognitionException {
        expression_return retval = new expression_return();
        retval.start = input.LT(1);
        int expression_StartIndex = input.index();
        Object root_0 = null;

        conditionalExpression_return conditionalExpression386 = null;

        assignmentOperator_return assignmentOperator387 = null;

        expression_return expression388 = null;



        try {
            if ( backtracking>0 && alreadyParsedRule(input, 70) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:951:5: ( conditionalExpression ( assignmentOperator expression )? )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:951:9: conditionalExpression ( assignmentOperator expression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalExpression_in_expression5313);
            conditionalExpression386=conditionalExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, conditionalExpression386.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:952:9: ( assignmentOperator expression )?
            int alt111=2;
            int LA111_0 = input.LA(1);

            if ( (LA111_0==EQ||(LA111_0>=PLUSEQ && LA111_0<=PERCENTEQ)||(LA111_0>=GT && LA111_0<=LT)) ) {
                alt111=1;
            }
            switch (alt111) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:952:10: assignmentOperator expression
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression5324);
                    assignmentOperator387=assignmentOperator();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, assignmentOperator387.getTree());
                    pushFollow(FOLLOW_expression_in_expression5326);
                    expression388=expression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expression388.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 70, expression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end expression

    public static class assignmentOperator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start assignmentOperator
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:957:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '>' '=' | '>' '>' '=' );
    public final assignmentOperator_return assignmentOperator() throws RecognitionException {
        assignmentOperator_return retval = new assignmentOperator_return();
        retval.start = input.LT(1);
        int assignmentOperator_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal389=null;
        Token string_literal390=null;
        Token string_literal391=null;
        Token string_literal392=null;
        Token string_literal393=null;
        Token string_literal394=null;
        Token string_literal395=null;
        Token string_literal396=null;
        Token string_literal397=null;
        Token char_literal398=null;
        Token char_literal399=null;
        Token char_literal400=null;
        Token char_literal401=null;
        Token char_literal402=null;
        Token char_literal403=null;
        Token char_literal404=null;
        Token char_literal405=null;
        Token char_literal406=null;
        Token char_literal407=null;

        Object char_literal389_tree=null;
        Object string_literal390_tree=null;
        Object string_literal391_tree=null;
        Object string_literal392_tree=null;
        Object string_literal393_tree=null;
        Object string_literal394_tree=null;
        Object string_literal395_tree=null;
        Object string_literal396_tree=null;
        Object string_literal397_tree=null;
        Object char_literal398_tree=null;
        Object char_literal399_tree=null;
        Object char_literal400_tree=null;
        Object char_literal401_tree=null;
        Object char_literal402_tree=null;
        Object char_literal403_tree=null;
        Object char_literal404_tree=null;
        Object char_literal405_tree=null;
        Object char_literal406_tree=null;
        Object char_literal407_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 71) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:958:5: ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '>' '=' | '>' '>' '=' )
            int alt112=12;
            switch ( input.LA(1) ) {
            case EQ:
                {
                alt112=1;
                }
                break;
            case PLUSEQ:
                {
                alt112=2;
                }
                break;
            case SUBEQ:
                {
                alt112=3;
                }
                break;
            case STAREQ:
                {
                alt112=4;
                }
                break;
            case SLASHEQ:
                {
                alt112=5;
                }
                break;
            case AMPEQ:
                {
                alt112=6;
                }
                break;
            case BAREQ:
                {
                alt112=7;
                }
                break;
            case CARETEQ:
                {
                alt112=8;
                }
                break;
            case PERCENTEQ:
                {
                alt112=9;
                }
                break;
            case LT:
                {
                alt112=10;
                }
                break;
            case GT:
                {
                int LA112_11 = input.LA(2);

                if ( (LA112_11==GT) ) {
                    int LA112_12 = input.LA(3);

                    if ( (LA112_12==GT) ) {
                        alt112=11;
                    }
                    else if ( (LA112_12==EQ) ) {
                        alt112=12;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("957:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '>' '=' | '>' '>' '=' );", 112, 12, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("957:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '>' '=' | '>' '>' '=' );", 112, 11, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("957:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '>' '=' | '>' '>' '=' );", 112, 0, input);

                throw nvae;
            }

            switch (alt112) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:958:9: '='
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal389=(Token)input.LT(1);
                    match(input,EQ,FOLLOW_EQ_in_assignmentOperator5358); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal389_tree = (Object)adaptor.create(char_literal389);
                    adaptor.addChild(root_0, char_literal389_tree);
                    }

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:959:9: '+='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal390=(Token)input.LT(1);
                    match(input,PLUSEQ,FOLLOW_PLUSEQ_in_assignmentOperator5368); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal390_tree = (Object)adaptor.create(string_literal390);
                    adaptor.addChild(root_0, string_literal390_tree);
                    }

                    }
                    break;
                case 3 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:960:9: '-='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal391=(Token)input.LT(1);
                    match(input,SUBEQ,FOLLOW_SUBEQ_in_assignmentOperator5378); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal391_tree = (Object)adaptor.create(string_literal391);
                    adaptor.addChild(root_0, string_literal391_tree);
                    }

                    }
                    break;
                case 4 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:961:9: '*='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal392=(Token)input.LT(1);
                    match(input,STAREQ,FOLLOW_STAREQ_in_assignmentOperator5388); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal392_tree = (Object)adaptor.create(string_literal392);
                    adaptor.addChild(root_0, string_literal392_tree);
                    }

                    }
                    break;
                case 5 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:962:9: '/='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal393=(Token)input.LT(1);
                    match(input,SLASHEQ,FOLLOW_SLASHEQ_in_assignmentOperator5398); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal393_tree = (Object)adaptor.create(string_literal393);
                    adaptor.addChild(root_0, string_literal393_tree);
                    }

                    }
                    break;
                case 6 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:963:9: '&='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal394=(Token)input.LT(1);
                    match(input,AMPEQ,FOLLOW_AMPEQ_in_assignmentOperator5408); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal394_tree = (Object)adaptor.create(string_literal394);
                    adaptor.addChild(root_0, string_literal394_tree);
                    }

                    }
                    break;
                case 7 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:964:9: '|='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal395=(Token)input.LT(1);
                    match(input,BAREQ,FOLLOW_BAREQ_in_assignmentOperator5418); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal395_tree = (Object)adaptor.create(string_literal395);
                    adaptor.addChild(root_0, string_literal395_tree);
                    }

                    }
                    break;
                case 8 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:965:9: '^='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal396=(Token)input.LT(1);
                    match(input,CARETEQ,FOLLOW_CARETEQ_in_assignmentOperator5428); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal396_tree = (Object)adaptor.create(string_literal396);
                    adaptor.addChild(root_0, string_literal396_tree);
                    }

                    }
                    break;
                case 9 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:966:9: '%='
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal397=(Token)input.LT(1);
                    match(input,PERCENTEQ,FOLLOW_PERCENTEQ_in_assignmentOperator5438); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal397_tree = (Object)adaptor.create(string_literal397);
                    adaptor.addChild(root_0, string_literal397_tree);
                    }

                    }
                    break;
                case 10 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:967:10: '<' '<' '='
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal398=(Token)input.LT(1);
                    match(input,LT,FOLLOW_LT_in_assignmentOperator5449); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal398_tree = (Object)adaptor.create(char_literal398);
                    adaptor.addChild(root_0, char_literal398_tree);
                    }
                    char_literal399=(Token)input.LT(1);
                    match(input,LT,FOLLOW_LT_in_assignmentOperator5451); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal399_tree = (Object)adaptor.create(char_literal399);
                    adaptor.addChild(root_0, char_literal399_tree);
                    }
                    char_literal400=(Token)input.LT(1);
                    match(input,EQ,FOLLOW_EQ_in_assignmentOperator5453); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal400_tree = (Object)adaptor.create(char_literal400);
                    adaptor.addChild(root_0, char_literal400_tree);
                    }

                    }
                    break;
                case 11 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:968:10: '>' '>' '>' '='
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal401=(Token)input.LT(1);
                    match(input,GT,FOLLOW_GT_in_assignmentOperator5464); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal401_tree = (Object)adaptor.create(char_literal401);
                    adaptor.addChild(root_0, char_literal401_tree);
                    }
                    char_literal402=(Token)input.LT(1);
                    match(input,GT,FOLLOW_GT_in_assignmentOperator5466); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal402_tree = (Object)adaptor.create(char_literal402);
                    adaptor.addChild(root_0, char_literal402_tree);
                    }
                    char_literal403=(Token)input.LT(1);
                    match(input,GT,FOLLOW_GT_in_assignmentOperator5468); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal403_tree = (Object)adaptor.create(char_literal403);
                    adaptor.addChild(root_0, char_literal403_tree);
                    }
                    char_literal404=(Token)input.LT(1);
                    match(input,EQ,FOLLOW_EQ_in_assignmentOperator5470); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal404_tree = (Object)adaptor.create(char_literal404);
                    adaptor.addChild(root_0, char_literal404_tree);
                    }

                    }
                    break;
                case 12 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:969:10: '>' '>' '='
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal405=(Token)input.LT(1);
                    match(input,GT,FOLLOW_GT_in_assignmentOperator5481); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal405_tree = (Object)adaptor.create(char_literal405);
                    adaptor.addChild(root_0, char_literal405_tree);
                    }
                    char_literal406=(Token)input.LT(1);
                    match(input,GT,FOLLOW_GT_in_assignmentOperator5483); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal406_tree = (Object)adaptor.create(char_literal406);
                    adaptor.addChild(root_0, char_literal406_tree);
                    }
                    char_literal407=(Token)input.LT(1);
                    match(input,EQ,FOLLOW_EQ_in_assignmentOperator5485); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal407_tree = (Object)adaptor.create(char_literal407);
                    adaptor.addChild(root_0, char_literal407_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 71, assignmentOperator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end assignmentOperator

    public static class conditionalExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start conditionalExpression
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:973:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' conditionalExpression )? ;
    public final conditionalExpression_return conditionalExpression() throws RecognitionException {
        conditionalExpression_return retval = new conditionalExpression_return();
        retval.start = input.LT(1);
        int conditionalExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal409=null;
        Token char_literal411=null;
        conditionalOrExpression_return conditionalOrExpression408 = null;

        expression_return expression410 = null;

        conditionalExpression_return conditionalExpression412 = null;


        Object char_literal409_tree=null;
        Object char_literal411_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 72) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:974:5: ( conditionalOrExpression ( '?' expression ':' conditionalExpression )? )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:974:9: conditionalOrExpression ( '?' expression ':' conditionalExpression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression5506);
            conditionalOrExpression408=conditionalOrExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, conditionalOrExpression408.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:975:9: ( '?' expression ':' conditionalExpression )?
            int alt113=2;
            int LA113_0 = input.LA(1);

            if ( (LA113_0==QUES) ) {
                alt113=1;
            }
            switch (alt113) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:975:10: '?' expression ':' conditionalExpression
                    {
                    char_literal409=(Token)input.LT(1);
                    match(input,QUES,FOLLOW_QUES_in_conditionalExpression5517); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal409_tree = (Object)adaptor.create(char_literal409);
                    adaptor.addChild(root_0, char_literal409_tree);
                    }
                    pushFollow(FOLLOW_expression_in_conditionalExpression5519);
                    expression410=expression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expression410.getTree());
                    char_literal411=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_conditionalExpression5521); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal411_tree = (Object)adaptor.create(char_literal411);
                    adaptor.addChild(root_0, char_literal411_tree);
                    }
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalExpression5523);
                    conditionalExpression412=conditionalExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, conditionalExpression412.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 72, conditionalExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end conditionalExpression

    public static class conditionalOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start conditionalOrExpression
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:979:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
    public final conditionalOrExpression_return conditionalOrExpression() throws RecognitionException {
        conditionalOrExpression_return retval = new conditionalOrExpression_return();
        retval.start = input.LT(1);
        int conditionalOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal414=null;
        conditionalAndExpression_return conditionalAndExpression413 = null;

        conditionalAndExpression_return conditionalAndExpression415 = null;


        Object string_literal414_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 73) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:980:5: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:980:9: conditionalAndExpression ( '||' conditionalAndExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression5554);
            conditionalAndExpression413=conditionalAndExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression413.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:981:9: ( '||' conditionalAndExpression )*
            loop114:
            do {
                int alt114=2;
                int LA114_0 = input.LA(1);

                if ( (LA114_0==BARBAR) ) {
                    alt114=1;
                }


                switch (alt114) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:981:10: '||' conditionalAndExpression
            	    {
            	    string_literal414=(Token)input.LT(1);
            	    match(input,BARBAR,FOLLOW_BARBAR_in_conditionalOrExpression5565); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    string_literal414_tree = (Object)adaptor.create(string_literal414);
            	    adaptor.addChild(root_0, string_literal414_tree);
            	    }
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression5567);
            	    conditionalAndExpression415=conditionalAndExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression415.getTree());

            	    }
            	    break;

            	default :
            	    break loop114;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 73, conditionalOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end conditionalOrExpression

    public static class conditionalAndExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start conditionalAndExpression
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:985:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
    public final conditionalAndExpression_return conditionalAndExpression() throws RecognitionException {
        conditionalAndExpression_return retval = new conditionalAndExpression_return();
        retval.start = input.LT(1);
        int conditionalAndExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal417=null;
        inclusiveOrExpression_return inclusiveOrExpression416 = null;

        inclusiveOrExpression_return inclusiveOrExpression418 = null;


        Object string_literal417_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 74) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:986:5: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:986:9: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5598);
            inclusiveOrExpression416=inclusiveOrExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression416.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:987:9: ( '&&' inclusiveOrExpression )*
            loop115:
            do {
                int alt115=2;
                int LA115_0 = input.LA(1);

                if ( (LA115_0==AMPAMP) ) {
                    alt115=1;
                }


                switch (alt115) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:987:10: '&&' inclusiveOrExpression
            	    {
            	    string_literal417=(Token)input.LT(1);
            	    match(input,AMPAMP,FOLLOW_AMPAMP_in_conditionalAndExpression5609); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    string_literal417_tree = (Object)adaptor.create(string_literal417);
            	    adaptor.addChild(root_0, string_literal417_tree);
            	    }
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5611);
            	    inclusiveOrExpression418=inclusiveOrExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression418.getTree());

            	    }
            	    break;

            	default :
            	    break loop115;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 74, conditionalAndExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end conditionalAndExpression

    public static class inclusiveOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start inclusiveOrExpression
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:991:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
    public final inclusiveOrExpression_return inclusiveOrExpression() throws RecognitionException {
        inclusiveOrExpression_return retval = new inclusiveOrExpression_return();
        retval.start = input.LT(1);
        int inclusiveOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal420=null;
        exclusiveOrExpression_return exclusiveOrExpression419 = null;

        exclusiveOrExpression_return exclusiveOrExpression421 = null;


        Object char_literal420_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 75) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:992:5: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:992:9: exclusiveOrExpression ( '|' exclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5642);
            exclusiveOrExpression419=exclusiveOrExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression419.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:993:9: ( '|' exclusiveOrExpression )*
            loop116:
            do {
                int alt116=2;
                int LA116_0 = input.LA(1);

                if ( (LA116_0==BAR) ) {
                    alt116=1;
                }


                switch (alt116) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:993:10: '|' exclusiveOrExpression
            	    {
            	    char_literal420=(Token)input.LT(1);
            	    match(input,BAR,FOLLOW_BAR_in_inclusiveOrExpression5653); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal420_tree = (Object)adaptor.create(char_literal420);
            	    adaptor.addChild(root_0, char_literal420_tree);
            	    }
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5655);
            	    exclusiveOrExpression421=exclusiveOrExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression421.getTree());

            	    }
            	    break;

            	default :
            	    break loop116;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 75, inclusiveOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end inclusiveOrExpression

    public static class exclusiveOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start exclusiveOrExpression
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:997:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
    public final exclusiveOrExpression_return exclusiveOrExpression() throws RecognitionException {
        exclusiveOrExpression_return retval = new exclusiveOrExpression_return();
        retval.start = input.LT(1);
        int exclusiveOrExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal423=null;
        andExpression_return andExpression422 = null;

        andExpression_return andExpression424 = null;


        Object char_literal423_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 76) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:998:5: ( andExpression ( '^' andExpression )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:998:9: andExpression ( '^' andExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression5686);
            andExpression422=andExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, andExpression422.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:999:9: ( '^' andExpression )*
            loop117:
            do {
                int alt117=2;
                int LA117_0 = input.LA(1);

                if ( (LA117_0==CARET) ) {
                    alt117=1;
                }


                switch (alt117) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:999:10: '^' andExpression
            	    {
            	    char_literal423=(Token)input.LT(1);
            	    match(input,CARET,FOLLOW_CARET_in_exclusiveOrExpression5697); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal423_tree = (Object)adaptor.create(char_literal423);
            	    adaptor.addChild(root_0, char_literal423_tree);
            	    }
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression5699);
            	    andExpression424=andExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, andExpression424.getTree());

            	    }
            	    break;

            	default :
            	    break loop117;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 76, exclusiveOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end exclusiveOrExpression

    public static class andExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start andExpression
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1003:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
    public final andExpression_return andExpression() throws RecognitionException {
        andExpression_return retval = new andExpression_return();
        retval.start = input.LT(1);
        int andExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal426=null;
        equalityExpression_return equalityExpression425 = null;

        equalityExpression_return equalityExpression427 = null;


        Object char_literal426_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 77) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1004:5: ( equalityExpression ( '&' equalityExpression )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1004:9: equalityExpression ( '&' equalityExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_equalityExpression_in_andExpression5730);
            equalityExpression425=equalityExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, equalityExpression425.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1005:9: ( '&' equalityExpression )*
            loop118:
            do {
                int alt118=2;
                int LA118_0 = input.LA(1);

                if ( (LA118_0==AMP) ) {
                    alt118=1;
                }


                switch (alt118) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1005:10: '&' equalityExpression
            	    {
            	    char_literal426=(Token)input.LT(1);
            	    match(input,AMP,FOLLOW_AMP_in_andExpression5741); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal426_tree = (Object)adaptor.create(char_literal426);
            	    adaptor.addChild(root_0, char_literal426_tree);
            	    }
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression5743);
            	    equalityExpression427=equalityExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, equalityExpression427.getTree());

            	    }
            	    break;

            	default :
            	    break loop118;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 77, andExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end andExpression

    public static class equalityExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start equalityExpression
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1009:1: equalityExpression : instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* ;
    public final equalityExpression_return equalityExpression() throws RecognitionException {
        equalityExpression_return retval = new equalityExpression_return();
        retval.start = input.LT(1);
        int equalityExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set429=null;
        instanceOfExpression_return instanceOfExpression428 = null;

        instanceOfExpression_return instanceOfExpression430 = null;


        Object set429_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 78) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1010:5: ( instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1010:9: instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression5774);
            instanceOfExpression428=instanceOfExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, instanceOfExpression428.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1011:9: ( ( '==' | '!=' ) instanceOfExpression )*
            loop119:
            do {
                int alt119=2;
                int LA119_0 = input.LA(1);

                if ( (LA119_0==EQEQ||LA119_0==BANGEQ) ) {
                    alt119=1;
                }


                switch (alt119) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1012:13: ( '==' | '!=' ) instanceOfExpression
            	    {
            	    set429=(Token)input.LT(1);
            	    if ( input.LA(1)==EQEQ||input.LA(1)==BANGEQ ) {
            	        input.consume();
            	        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set429));
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_equalityExpression5801);    throw mse;
            	    }

            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression5851);
            	    instanceOfExpression430=instanceOfExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, instanceOfExpression430.getTree());

            	    }
            	    break;

            	default :
            	    break loop119;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 78, equalityExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end equalityExpression

    public static class instanceOfExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start instanceOfExpression
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1019:1: instanceOfExpression : relationalExpression ( 'instanceof' type )? ;
    public final instanceOfExpression_return instanceOfExpression() throws RecognitionException {
        instanceOfExpression_return retval = new instanceOfExpression_return();
        retval.start = input.LT(1);
        int instanceOfExpression_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal432=null;
        relationalExpression_return relationalExpression431 = null;

        type_return type433 = null;


        Object string_literal432_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 79) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1020:5: ( relationalExpression ( 'instanceof' type )? )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1020:9: relationalExpression ( 'instanceof' type )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_relationalExpression_in_instanceOfExpression5882);
            relationalExpression431=relationalExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, relationalExpression431.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1021:9: ( 'instanceof' type )?
            int alt120=2;
            int LA120_0 = input.LA(1);

            if ( (LA120_0==INSTANCEOF) ) {
                alt120=1;
            }
            switch (alt120) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1021:10: 'instanceof' type
                    {
                    string_literal432=(Token)input.LT(1);
                    match(input,INSTANCEOF,FOLLOW_INSTANCEOF_in_instanceOfExpression5893); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal432_tree = (Object)adaptor.create(string_literal432);
                    adaptor.addChild(root_0, string_literal432_tree);
                    }
                    pushFollow(FOLLOW_type_in_instanceOfExpression5895);
                    type433=type();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, type433.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 79, instanceOfExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end instanceOfExpression

    public static class relationalExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start relationalExpression
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1025:1: relationalExpression : shiftExpression ( relationalOp shiftExpression )* ;
    public final relationalExpression_return relationalExpression() throws RecognitionException {
        relationalExpression_return retval = new relationalExpression_return();
        retval.start = input.LT(1);
        int relationalExpression_StartIndex = input.index();
        Object root_0 = null;

        shiftExpression_return shiftExpression434 = null;

        relationalOp_return relationalOp435 = null;

        shiftExpression_return shiftExpression436 = null;



        try {
            if ( backtracking>0 && alreadyParsedRule(input, 80) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1026:5: ( shiftExpression ( relationalOp shiftExpression )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1026:9: shiftExpression ( relationalOp shiftExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_shiftExpression_in_relationalExpression5926);
            shiftExpression434=shiftExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, shiftExpression434.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1027:9: ( relationalOp shiftExpression )*
            loop121:
            do {
                int alt121=2;
                int LA121_0 = input.LA(1);

                if ( (LA121_0==LT) ) {
                    int LA121_2 = input.LA(2);

                    if ( ((LA121_2>=IDENTIFIER && LA121_2<=NULL)||LA121_2==BOOLEAN||LA121_2==BYTE||LA121_2==CHAR||LA121_2==DOUBLE||LA121_2==FLOAT||LA121_2==INT||LA121_2==LONG||LA121_2==NEW||LA121_2==SHORT||LA121_2==SUPER||LA121_2==THIS||LA121_2==VOID||LA121_2==LPAREN||(LA121_2>=EQ && LA121_2<=TILDE)||(LA121_2>=PLUSPLUS && LA121_2<=SUB)) ) {
                        alt121=1;
                    }


                }
                else if ( (LA121_0==GT) ) {
                    int LA121_3 = input.LA(2);

                    if ( ((LA121_3>=IDENTIFIER && LA121_3<=NULL)||LA121_3==BOOLEAN||LA121_3==BYTE||LA121_3==CHAR||LA121_3==DOUBLE||LA121_3==FLOAT||LA121_3==INT||LA121_3==LONG||LA121_3==NEW||LA121_3==SHORT||LA121_3==SUPER||LA121_3==THIS||LA121_3==VOID||LA121_3==LPAREN||(LA121_3>=EQ && LA121_3<=TILDE)||(LA121_3>=PLUSPLUS && LA121_3<=SUB)) ) {
                        alt121=1;
                    }


                }


                switch (alt121) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1027:10: relationalOp shiftExpression
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression5937);
            	    relationalOp435=relationalOp();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, relationalOp435.getTree());
            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression5939);
            	    shiftExpression436=shiftExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, shiftExpression436.getTree());

            	    }
            	    break;

            	default :
            	    break loop121;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 80, relationalExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end relationalExpression

    public static class relationalOp_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start relationalOp
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1031:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );
    public final relationalOp_return relationalOp() throws RecognitionException {
        relationalOp_return retval = new relationalOp_return();
        retval.start = input.LT(1);
        int relationalOp_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal437=null;
        Token char_literal438=null;
        Token char_literal439=null;
        Token char_literal440=null;
        Token char_literal441=null;
        Token char_literal442=null;

        Object char_literal437_tree=null;
        Object char_literal438_tree=null;
        Object char_literal439_tree=null;
        Object char_literal440_tree=null;
        Object char_literal441_tree=null;
        Object char_literal442_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 81) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1032:5: ( '<' '=' | '>' '=' | '<' | '>' )
            int alt122=4;
            int LA122_0 = input.LA(1);

            if ( (LA122_0==LT) ) {
                int LA122_1 = input.LA(2);

                if ( (LA122_1==EQ) ) {
                    alt122=1;
                }
                else if ( ((LA122_1>=IDENTIFIER && LA122_1<=NULL)||LA122_1==BOOLEAN||LA122_1==BYTE||LA122_1==CHAR||LA122_1==DOUBLE||LA122_1==FLOAT||LA122_1==INT||LA122_1==LONG||LA122_1==NEW||LA122_1==SHORT||LA122_1==SUPER||LA122_1==THIS||LA122_1==VOID||LA122_1==LPAREN||(LA122_1>=BANG && LA122_1<=TILDE)||(LA122_1>=PLUSPLUS && LA122_1<=SUB)) ) {
                    alt122=3;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("1031:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );", 122, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA122_0==GT) ) {
                int LA122_2 = input.LA(2);

                if ( (LA122_2==EQ) ) {
                    alt122=2;
                }
                else if ( ((LA122_2>=IDENTIFIER && LA122_2<=NULL)||LA122_2==BOOLEAN||LA122_2==BYTE||LA122_2==CHAR||LA122_2==DOUBLE||LA122_2==FLOAT||LA122_2==INT||LA122_2==LONG||LA122_2==NEW||LA122_2==SHORT||LA122_2==SUPER||LA122_2==THIS||LA122_2==VOID||LA122_2==LPAREN||(LA122_2>=BANG && LA122_2<=TILDE)||(LA122_2>=PLUSPLUS && LA122_2<=SUB)) ) {
                    alt122=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("1031:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );", 122, 2, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1031:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );", 122, 0, input);

                throw nvae;
            }
            switch (alt122) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1032:10: '<' '='
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal437=(Token)input.LT(1);
                    match(input,LT,FOLLOW_LT_in_relationalOp5971); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal437_tree = (Object)adaptor.create(char_literal437);
                    adaptor.addChild(root_0, char_literal437_tree);
                    }
                    char_literal438=(Token)input.LT(1);
                    match(input,EQ,FOLLOW_EQ_in_relationalOp5973); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal438_tree = (Object)adaptor.create(char_literal438);
                    adaptor.addChild(root_0, char_literal438_tree);
                    }

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1033:10: '>' '='
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal439=(Token)input.LT(1);
                    match(input,GT,FOLLOW_GT_in_relationalOp5984); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal439_tree = (Object)adaptor.create(char_literal439);
                    adaptor.addChild(root_0, char_literal439_tree);
                    }
                    char_literal440=(Token)input.LT(1);
                    match(input,EQ,FOLLOW_EQ_in_relationalOp5986); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal440_tree = (Object)adaptor.create(char_literal440);
                    adaptor.addChild(root_0, char_literal440_tree);
                    }

                    }
                    break;
                case 3 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1034:9: '<'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal441=(Token)input.LT(1);
                    match(input,LT,FOLLOW_LT_in_relationalOp5996); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal441_tree = (Object)adaptor.create(char_literal441);
                    adaptor.addChild(root_0, char_literal441_tree);
                    }

                    }
                    break;
                case 4 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1035:9: '>'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal442=(Token)input.LT(1);
                    match(input,GT,FOLLOW_GT_in_relationalOp6006); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal442_tree = (Object)adaptor.create(char_literal442);
                    adaptor.addChild(root_0, char_literal442_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 81, relationalOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end relationalOp

    public static class shiftExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start shiftExpression
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1038:1: shiftExpression : additiveExpression ( shiftOp additiveExpression )* ;
    public final shiftExpression_return shiftExpression() throws RecognitionException {
        shiftExpression_return retval = new shiftExpression_return();
        retval.start = input.LT(1);
        int shiftExpression_StartIndex = input.index();
        Object root_0 = null;

        additiveExpression_return additiveExpression443 = null;

        shiftOp_return shiftOp444 = null;

        additiveExpression_return additiveExpression445 = null;



        try {
            if ( backtracking>0 && alreadyParsedRule(input, 82) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1039:5: ( additiveExpression ( shiftOp additiveExpression )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1039:9: additiveExpression ( shiftOp additiveExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_additiveExpression_in_shiftExpression6026);
            additiveExpression443=additiveExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, additiveExpression443.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1040:9: ( shiftOp additiveExpression )*
            loop123:
            do {
                int alt123=2;
                int LA123_0 = input.LA(1);

                if ( (LA123_0==LT) ) {
                    int LA123_1 = input.LA(2);

                    if ( (LA123_1==LT) ) {
                        int LA123_4 = input.LA(3);

                        if ( ((LA123_4>=IDENTIFIER && LA123_4<=NULL)||LA123_4==BOOLEAN||LA123_4==BYTE||LA123_4==CHAR||LA123_4==DOUBLE||LA123_4==FLOAT||LA123_4==INT||LA123_4==LONG||LA123_4==NEW||LA123_4==SHORT||LA123_4==SUPER||LA123_4==THIS||LA123_4==VOID||LA123_4==LPAREN||(LA123_4>=BANG && LA123_4<=TILDE)||(LA123_4>=PLUSPLUS && LA123_4<=SUB)) ) {
                            alt123=1;
                        }


                    }


                }
                else if ( (LA123_0==GT) ) {
                    int LA123_2 = input.LA(2);

                    if ( (LA123_2==GT) ) {
                        int LA123_5 = input.LA(3);

                        if ( (LA123_5==GT) ) {
                            int LA123_7 = input.LA(4);

                            if ( ((LA123_7>=IDENTIFIER && LA123_7<=NULL)||LA123_7==BOOLEAN||LA123_7==BYTE||LA123_7==CHAR||LA123_7==DOUBLE||LA123_7==FLOAT||LA123_7==INT||LA123_7==LONG||LA123_7==NEW||LA123_7==SHORT||LA123_7==SUPER||LA123_7==THIS||LA123_7==VOID||LA123_7==LPAREN||(LA123_7>=BANG && LA123_7<=TILDE)||(LA123_7>=PLUSPLUS && LA123_7<=SUB)) ) {
                                alt123=1;
                            }


                        }
                        else if ( ((LA123_5>=IDENTIFIER && LA123_5<=NULL)||LA123_5==BOOLEAN||LA123_5==BYTE||LA123_5==CHAR||LA123_5==DOUBLE||LA123_5==FLOAT||LA123_5==INT||LA123_5==LONG||LA123_5==NEW||LA123_5==SHORT||LA123_5==SUPER||LA123_5==THIS||LA123_5==VOID||LA123_5==LPAREN||(LA123_5>=BANG && LA123_5<=TILDE)||(LA123_5>=PLUSPLUS && LA123_5<=SUB)) ) {
                            alt123=1;
                        }


                    }


                }


                switch (alt123) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1040:10: shiftOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression6037);
            	    shiftOp444=shiftOp();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, shiftOp444.getTree());
            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression6039);
            	    additiveExpression445=additiveExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, additiveExpression445.getTree());

            	    }
            	    break;

            	default :
            	    break loop123;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 82, shiftExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end shiftExpression

    public static class shiftOp_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start shiftOp
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1045:1: shiftOp : ( '<' '<' | '>' '>' '>' | '>' '>' );
    public final shiftOp_return shiftOp() throws RecognitionException {
        shiftOp_return retval = new shiftOp_return();
        retval.start = input.LT(1);
        int shiftOp_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal446=null;
        Token char_literal447=null;
        Token char_literal448=null;
        Token char_literal449=null;
        Token char_literal450=null;
        Token char_literal451=null;
        Token char_literal452=null;

        Object char_literal446_tree=null;
        Object char_literal447_tree=null;
        Object char_literal448_tree=null;
        Object char_literal449_tree=null;
        Object char_literal450_tree=null;
        Object char_literal451_tree=null;
        Object char_literal452_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 83) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1046:5: ( '<' '<' | '>' '>' '>' | '>' '>' )
            int alt124=3;
            int LA124_0 = input.LA(1);

            if ( (LA124_0==LT) ) {
                alt124=1;
            }
            else if ( (LA124_0==GT) ) {
                int LA124_2 = input.LA(2);

                if ( (LA124_2==GT) ) {
                    int LA124_3 = input.LA(3);

                    if ( (LA124_3==GT) ) {
                        alt124=2;
                    }
                    else if ( ((LA124_3>=IDENTIFIER && LA124_3<=NULL)||LA124_3==BOOLEAN||LA124_3==BYTE||LA124_3==CHAR||LA124_3==DOUBLE||LA124_3==FLOAT||LA124_3==INT||LA124_3==LONG||LA124_3==NEW||LA124_3==SHORT||LA124_3==SUPER||LA124_3==THIS||LA124_3==VOID||LA124_3==LPAREN||(LA124_3>=BANG && LA124_3<=TILDE)||(LA124_3>=PLUSPLUS && LA124_3<=SUB)) ) {
                        alt124=3;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("1045:1: shiftOp : ( '<' '<' | '>' '>' '>' | '>' '>' );", 124, 3, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("1045:1: shiftOp : ( '<' '<' | '>' '>' '>' | '>' '>' );", 124, 2, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1045:1: shiftOp : ( '<' '<' | '>' '>' '>' | '>' '>' );", 124, 0, input);

                throw nvae;
            }
            switch (alt124) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1046:10: '<' '<'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal446=(Token)input.LT(1);
                    match(input,LT,FOLLOW_LT_in_shiftOp6072); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal446_tree = (Object)adaptor.create(char_literal446);
                    adaptor.addChild(root_0, char_literal446_tree);
                    }
                    char_literal447=(Token)input.LT(1);
                    match(input,LT,FOLLOW_LT_in_shiftOp6074); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal447_tree = (Object)adaptor.create(char_literal447);
                    adaptor.addChild(root_0, char_literal447_tree);
                    }

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1047:10: '>' '>' '>'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal448=(Token)input.LT(1);
                    match(input,GT,FOLLOW_GT_in_shiftOp6085); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal448_tree = (Object)adaptor.create(char_literal448);
                    adaptor.addChild(root_0, char_literal448_tree);
                    }
                    char_literal449=(Token)input.LT(1);
                    match(input,GT,FOLLOW_GT_in_shiftOp6087); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal449_tree = (Object)adaptor.create(char_literal449);
                    adaptor.addChild(root_0, char_literal449_tree);
                    }
                    char_literal450=(Token)input.LT(1);
                    match(input,GT,FOLLOW_GT_in_shiftOp6089); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal450_tree = (Object)adaptor.create(char_literal450);
                    adaptor.addChild(root_0, char_literal450_tree);
                    }

                    }
                    break;
                case 3 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1048:10: '>' '>'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal451=(Token)input.LT(1);
                    match(input,GT,FOLLOW_GT_in_shiftOp6100); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal451_tree = (Object)adaptor.create(char_literal451);
                    adaptor.addChild(root_0, char_literal451_tree);
                    }
                    char_literal452=(Token)input.LT(1);
                    match(input,GT,FOLLOW_GT_in_shiftOp6102); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal452_tree = (Object)adaptor.create(char_literal452);
                    adaptor.addChild(root_0, char_literal452_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 83, shiftOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end shiftOp

    public static class additiveExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start additiveExpression
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1052:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
    public final additiveExpression_return additiveExpression() throws RecognitionException {
        additiveExpression_return retval = new additiveExpression_return();
        retval.start = input.LT(1);
        int additiveExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set454=null;
        multiplicativeExpression_return multiplicativeExpression453 = null;

        multiplicativeExpression_return multiplicativeExpression455 = null;


        Object set454_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 84) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1053:5: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1053:9: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression6123);
            multiplicativeExpression453=multiplicativeExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression453.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1054:9: ( ( '+' | '-' ) multiplicativeExpression )*
            loop125:
            do {
                int alt125=2;
                int LA125_0 = input.LA(1);

                if ( ((LA125_0>=PLUS && LA125_0<=SUB)) ) {
                    alt125=1;
                }


                switch (alt125) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1055:13: ( '+' | '-' ) multiplicativeExpression
            	    {
            	    set454=(Token)input.LT(1);
            	    if ( (input.LA(1)>=PLUS && input.LA(1)<=SUB) ) {
            	        input.consume();
            	        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set454));
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_additiveExpression6150);    throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression6200);
            	    multiplicativeExpression455=multiplicativeExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression455.getTree());

            	    }
            	    break;

            	default :
            	    break loop125;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 84, additiveExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end additiveExpression

    public static class multiplicativeExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start multiplicativeExpression
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1062:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
    public final multiplicativeExpression_return multiplicativeExpression() throws RecognitionException {
        multiplicativeExpression_return retval = new multiplicativeExpression_return();
        retval.start = input.LT(1);
        int multiplicativeExpression_StartIndex = input.index();
        Object root_0 = null;

        Token set457=null;
        unaryExpression_return unaryExpression456 = null;

        unaryExpression_return unaryExpression458 = null;


        Object set457_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 85) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1063:5: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1064:9: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression6238);
            unaryExpression456=unaryExpression();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, unaryExpression456.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1065:9: ( ( '*' | '/' | '%' ) unaryExpression )*
            loop126:
            do {
                int alt126=2;
                int LA126_0 = input.LA(1);

                if ( ((LA126_0>=STAR && LA126_0<=SLASH)||LA126_0==PERCENT) ) {
                    alt126=1;
                }


                switch (alt126) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1066:13: ( '*' | '/' | '%' ) unaryExpression
            	    {
            	    set457=(Token)input.LT(1);
            	    if ( (input.LA(1)>=STAR && input.LA(1)<=SLASH)||input.LA(1)==PERCENT ) {
            	        input.consume();
            	        if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set457));
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_multiplicativeExpression6265);    throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression6333);
            	    unaryExpression458=unaryExpression();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) adaptor.addChild(root_0, unaryExpression458.getTree());

            	    }
            	    break;

            	default :
            	    break loop126;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 85, multiplicativeExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end multiplicativeExpression

    public static class unaryExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start unaryExpression
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1074:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus );
    public final unaryExpression_return unaryExpression() throws RecognitionException {
        unaryExpression_return retval = new unaryExpression_return();
        retval.start = input.LT(1);
        int unaryExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal459=null;
        Token char_literal461=null;
        Token string_literal463=null;
        Token string_literal465=null;
        unaryExpression_return unaryExpression460 = null;

        unaryExpression_return unaryExpression462 = null;

        unaryExpression_return unaryExpression464 = null;

        unaryExpression_return unaryExpression466 = null;

        unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus467 = null;


        Object char_literal459_tree=null;
        Object char_literal461_tree=null;
        Object string_literal463_tree=null;
        Object string_literal465_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 86) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1079:5: ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus )
            int alt127=5;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt127=1;
                }
                break;
            case SUB:
                {
                alt127=2;
                }
                break;
            case PLUSPLUS:
                {
                alt127=3;
                }
                break;
            case SUBSUB:
                {
                alt127=4;
                }
                break;
            case IDENTIFIER:
            case INTLITERAL:
            case LONGLITERAL:
            case FLOATLITERAL:
            case DOUBLELITERAL:
            case CHARLITERAL:
            case STRINGLITERAL:
            case TRUE:
            case FALSE:
            case NULL:
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case NEW:
            case SHORT:
            case SUPER:
            case THIS:
            case VOID:
            case LPAREN:
            case BANG:
            case TILDE:
                {
                alt127=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1074:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus );", 127, 0, input);

                throw nvae;
            }

            switch (alt127) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1079:9: '+' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal459=(Token)input.LT(1);
                    match(input,PLUS,FOLLOW_PLUS_in_unaryExpression6366); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal459_tree = (Object)adaptor.create(char_literal459);
                    adaptor.addChild(root_0, char_literal459_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression6369);
                    unaryExpression460=unaryExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, unaryExpression460.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1080:9: '-' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal461=(Token)input.LT(1);
                    match(input,SUB,FOLLOW_SUB_in_unaryExpression6379); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal461_tree = (Object)adaptor.create(char_literal461);
                    adaptor.addChild(root_0, char_literal461_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression6381);
                    unaryExpression462=unaryExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, unaryExpression462.getTree());

                    }
                    break;
                case 3 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1081:9: '++' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal463=(Token)input.LT(1);
                    match(input,PLUSPLUS,FOLLOW_PLUSPLUS_in_unaryExpression6391); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal463_tree = (Object)adaptor.create(string_literal463);
                    adaptor.addChild(root_0, string_literal463_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression6393);
                    unaryExpression464=unaryExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, unaryExpression464.getTree());

                    }
                    break;
                case 4 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1082:9: '--' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal465=(Token)input.LT(1);
                    match(input,SUBSUB,FOLLOW_SUBSUB_in_unaryExpression6403); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal465_tree = (Object)adaptor.create(string_literal465);
                    adaptor.addChild(root_0, string_literal465_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression6405);
                    unaryExpression466=unaryExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, unaryExpression466.getTree());

                    }
                    break;
                case 5 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1083:9: unaryExpressionNotPlusMinus
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression6415);
                    unaryExpressionNotPlusMinus467=unaryExpressionNotPlusMinus();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, unaryExpressionNotPlusMinus467.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 86, unaryExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end unaryExpression

    public static class unaryExpressionNotPlusMinus_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start unaryExpressionNotPlusMinus
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1086:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );
    public final unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus() throws RecognitionException {
        unaryExpressionNotPlusMinus_return retval = new unaryExpressionNotPlusMinus_return();
        retval.start = input.LT(1);
        int unaryExpressionNotPlusMinus_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal468=null;
        Token char_literal470=null;
        Token set475=null;
        unaryExpression_return unaryExpression469 = null;

        unaryExpression_return unaryExpression471 = null;

        castExpression_return castExpression472 = null;

        primary_return primary473 = null;

        selector_return selector474 = null;


        Object char_literal468_tree=null;
        Object char_literal470_tree=null;
        Object set475_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 87) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1087:5: ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? )
            int alt130=4;
            switch ( input.LA(1) ) {
            case TILDE:
                {
                alt130=1;
                }
                break;
            case BANG:
                {
                alt130=2;
                }
                break;
            case LPAREN:
                {
                int LA130_3 = input.LA(2);

                if ( (synpred202()) ) {
                    alt130=3;
                }
                else if ( (true) ) {
                    alt130=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("1086:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 130, 3, input);

                    throw nvae;
                }
                }
                break;
            case IDENTIFIER:
            case INTLITERAL:
            case LONGLITERAL:
            case FLOATLITERAL:
            case DOUBLELITERAL:
            case CHARLITERAL:
            case STRINGLITERAL:
            case TRUE:
            case FALSE:
            case NULL:
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case NEW:
            case SHORT:
            case SUPER:
            case THIS:
            case VOID:
                {
                alt130=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1086:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 130, 0, input);

                throw nvae;
            }

            switch (alt130) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1087:9: '~' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal468=(Token)input.LT(1);
                    match(input,TILDE,FOLLOW_TILDE_in_unaryExpressionNotPlusMinus6435); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal468_tree = (Object)adaptor.create(char_literal468);
                    adaptor.addChild(root_0, char_literal468_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6437);
                    unaryExpression469=unaryExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, unaryExpression469.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1088:9: '!' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal470=(Token)input.LT(1);
                    match(input,BANG,FOLLOW_BANG_in_unaryExpressionNotPlusMinus6447); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal470_tree = (Object)adaptor.create(char_literal470);
                    adaptor.addChild(root_0, char_literal470_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6449);
                    unaryExpression471=unaryExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, unaryExpression471.getTree());

                    }
                    break;
                case 3 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1089:9: castExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus6459);
                    castExpression472=castExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, castExpression472.getTree());

                    }
                    break;
                case 4 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1090:9: primary ( selector )* ( '++' | '--' )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus6469);
                    primary473=primary();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, primary473.getTree());
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1091:9: ( selector )*
                    loop128:
                    do {
                        int alt128=2;
                        int LA128_0 = input.LA(1);

                        if ( (LA128_0==LBRACKET||LA128_0==DOT) ) {
                            alt128=1;
                        }


                        switch (alt128) {
                    	case 1 :
                    	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1091:10: selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus6480);
                    	    selector474=selector();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) adaptor.addChild(root_0, selector474.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop128;
                        }
                    } while (true);

                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1093:9: ( '++' | '--' )?
                    int alt129=2;
                    int LA129_0 = input.LA(1);

                    if ( ((LA129_0>=PLUSPLUS && LA129_0<=SUBSUB)) ) {
                        alt129=1;
                    }
                    switch (alt129) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:
                            {
                            set475=(Token)input.LT(1);
                            if ( (input.LA(1)>=PLUSPLUS && input.LA(1)<=SUBSUB) ) {
                                input.consume();
                                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set475));
                                errorRecovery=false;failed=false;
                            }
                            else {
                                if (backtracking>0) {failed=true; return retval;}
                                MismatchedSetException mse =
                                    new MismatchedSetException(null,input);
                                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_unaryExpressionNotPlusMinus6501);    throw mse;
                            }


                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 87, unaryExpressionNotPlusMinus_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end unaryExpressionNotPlusMinus

    public static class castExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start castExpression
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1098:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' type ')' unaryExpressionNotPlusMinus );
    public final castExpression_return castExpression() throws RecognitionException {
        castExpression_return retval = new castExpression_return();
        retval.start = input.LT(1);
        int castExpression_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal476=null;
        Token char_literal478=null;
        Token char_literal480=null;
        Token char_literal482=null;
        primitiveType_return primitiveType477 = null;

        unaryExpression_return unaryExpression479 = null;

        type_return type481 = null;

        unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus483 = null;


        Object char_literal476_tree=null;
        Object char_literal478_tree=null;
        Object char_literal480_tree=null;
        Object char_literal482_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 88) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1099:5: ( '(' primitiveType ')' unaryExpression | '(' type ')' unaryExpressionNotPlusMinus )
            int alt131=2;
            int LA131_0 = input.LA(1);

            if ( (LA131_0==LPAREN) ) {
                int LA131_1 = input.LA(2);

                if ( (synpred206()) ) {
                    alt131=1;
                }
                else if ( (true) ) {
                    alt131=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("1098:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' type ')' unaryExpressionNotPlusMinus );", 131, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1098:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' type ')' unaryExpressionNotPlusMinus );", 131, 0, input);

                throw nvae;
            }
            switch (alt131) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1099:9: '(' primitiveType ')' unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal476=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_castExpression6550); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal476_tree = (Object)adaptor.create(char_literal476);
                    adaptor.addChild(root_0, char_literal476_tree);
                    }
                    pushFollow(FOLLOW_primitiveType_in_castExpression6552);
                    primitiveType477=primitiveType();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, primitiveType477.getTree());
                    char_literal478=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_castExpression6554); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal478_tree = (Object)adaptor.create(char_literal478);
                    adaptor.addChild(root_0, char_literal478_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_castExpression6556);
                    unaryExpression479=unaryExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, unaryExpression479.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1100:9: '(' type ')' unaryExpressionNotPlusMinus
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal480=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_castExpression6566); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal480_tree = (Object)adaptor.create(char_literal480);
                    adaptor.addChild(root_0, char_literal480_tree);
                    }
                    pushFollow(FOLLOW_type_in_castExpression6568);
                    type481=type();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, type481.getTree());
                    char_literal482=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_castExpression6570); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal482_tree = (Object)adaptor.create(char_literal482);
                    adaptor.addChild(root_0, char_literal482_tree);
                    }
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression6572);
                    unaryExpressionNotPlusMinus483=unaryExpressionNotPlusMinus();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, unaryExpressionNotPlusMinus483.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 88, castExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end castExpression

    public static class primary_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start primary
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1103:1: primary : ( parExpression | 'this' ( '.' IDENTIFIER )* ( identifierSuffix )? | IDENTIFIER ( '.' IDENTIFIER )* ( identifierSuffix )? | 'super' superSuffix | literal | creator | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );
    public final primary_return primary() throws RecognitionException {
        primary_return retval = new primary_return();
        retval.start = input.LT(1);
        int primary_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal485=null;
        Token char_literal486=null;
        Token IDENTIFIER487=null;
        Token IDENTIFIER489=null;
        Token char_literal490=null;
        Token IDENTIFIER491=null;
        Token string_literal493=null;
        Token char_literal498=null;
        Token char_literal499=null;
        Token char_literal500=null;
        Token string_literal501=null;
        Token string_literal502=null;
        Token char_literal503=null;
        Token string_literal504=null;
        parExpression_return parExpression484 = null;

        identifierSuffix_return identifierSuffix488 = null;

        identifierSuffix_return identifierSuffix492 = null;

        superSuffix_return superSuffix494 = null;

        literal_return literal495 = null;

        creator_return creator496 = null;

        primitiveType_return primitiveType497 = null;


        Object string_literal485_tree=null;
        Object char_literal486_tree=null;
        Object IDENTIFIER487_tree=null;
        Object IDENTIFIER489_tree=null;
        Object char_literal490_tree=null;
        Object IDENTIFIER491_tree=null;
        Object string_literal493_tree=null;
        Object char_literal498_tree=null;
        Object char_literal499_tree=null;
        Object char_literal500_tree=null;
        Object string_literal501_tree=null;
        Object string_literal502_tree=null;
        Object char_literal503_tree=null;
        Object string_literal504_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 89) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1107:5: ( parExpression | 'this' ( '.' IDENTIFIER )* ( identifierSuffix )? | IDENTIFIER ( '.' IDENTIFIER )* ( identifierSuffix )? | 'super' superSuffix | literal | creator | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' )
            int alt137=8;
            switch ( input.LA(1) ) {
            case LPAREN:
                {
                alt137=1;
                }
                break;
            case THIS:
                {
                alt137=2;
                }
                break;
            case IDENTIFIER:
                {
                alt137=3;
                }
                break;
            case SUPER:
                {
                alt137=4;
                }
                break;
            case INTLITERAL:
            case LONGLITERAL:
            case FLOATLITERAL:
            case DOUBLELITERAL:
            case CHARLITERAL:
            case STRINGLITERAL:
            case TRUE:
            case FALSE:
            case NULL:
                {
                alt137=5;
                }
                break;
            case NEW:
                {
                alt137=6;
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                {
                alt137=7;
                }
                break;
            case VOID:
                {
                alt137=8;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1103:1: primary : ( parExpression | 'this' ( '.' IDENTIFIER )* ( identifierSuffix )? | IDENTIFIER ( '.' IDENTIFIER )* ( identifierSuffix )? | 'super' superSuffix | literal | creator | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );", 137, 0, input);

                throw nvae;
            }

            switch (alt137) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1107:9: parExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_parExpression_in_primary6594);
                    parExpression484=parExpression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, parExpression484.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1108:9: 'this' ( '.' IDENTIFIER )* ( identifierSuffix )?
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal485=(Token)input.LT(1);
                    match(input,THIS,FOLLOW_THIS_in_primary6616); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal485_tree = (Object)adaptor.create(string_literal485);
                    adaptor.addChild(root_0, string_literal485_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1109:9: ( '.' IDENTIFIER )*
                    loop132:
                    do {
                        int alt132=2;
                        int LA132_0 = input.LA(1);

                        if ( (LA132_0==DOT) ) {
                            int LA132_2 = input.LA(2);

                            if ( (LA132_2==IDENTIFIER) ) {
                                int LA132_3 = input.LA(3);

                                if ( (synpred208()) ) {
                                    alt132=1;
                                }


                            }


                        }


                        switch (alt132) {
                    	case 1 :
                    	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1109:10: '.' IDENTIFIER
                    	    {
                    	    char_literal486=(Token)input.LT(1);
                    	    match(input,DOT,FOLLOW_DOT_in_primary6627); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal486_tree = (Object)adaptor.create(char_literal486);
                    	    adaptor.addChild(root_0, char_literal486_tree);
                    	    }
                    	    IDENTIFIER487=(Token)input.LT(1);
                    	    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primary6629); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    IDENTIFIER487_tree = (Object)adaptor.create(IDENTIFIER487);
                    	    adaptor.addChild(root_0, IDENTIFIER487_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop132;
                        }
                    } while (true);

                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1111:9: ( identifierSuffix )?
                    int alt133=2;
                    switch ( input.LA(1) ) {
                        case LBRACKET:
                            {
                            int LA133_1 = input.LA(2);

                            if ( (synpred209()) ) {
                                alt133=1;
                            }
                            }
                            break;
                        case LPAREN:
                            {
                            alt133=1;
                            }
                            break;
                        case DOT:
                            {
                            int LA133_3 = input.LA(2);

                            if ( (synpred209()) ) {
                                alt133=1;
                            }
                            }
                            break;
                    }

                    switch (alt133) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1111:10: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary6651);
                            identifierSuffix488=identifierSuffix();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, identifierSuffix488.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1113:9: IDENTIFIER ( '.' IDENTIFIER )* ( identifierSuffix )?
                    {
                    root_0 = (Object)adaptor.nil();

                    IDENTIFIER489=(Token)input.LT(1);
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primary6672); if (failed) return retval;
                    if ( backtracking==0 ) {
                    IDENTIFIER489_tree = (Object)adaptor.create(IDENTIFIER489);
                    adaptor.addChild(root_0, IDENTIFIER489_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1114:9: ( '.' IDENTIFIER )*
                    loop134:
                    do {
                        int alt134=2;
                        int LA134_0 = input.LA(1);

                        if ( (LA134_0==DOT) ) {
                            int LA134_2 = input.LA(2);

                            if ( (LA134_2==IDENTIFIER) ) {
                                int LA134_3 = input.LA(3);

                                if ( (synpred211()) ) {
                                    alt134=1;
                                }


                            }


                        }


                        switch (alt134) {
                    	case 1 :
                    	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1114:10: '.' IDENTIFIER
                    	    {
                    	    char_literal490=(Token)input.LT(1);
                    	    match(input,DOT,FOLLOW_DOT_in_primary6683); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal490_tree = (Object)adaptor.create(char_literal490);
                    	    adaptor.addChild(root_0, char_literal490_tree);
                    	    }
                    	    IDENTIFIER491=(Token)input.LT(1);
                    	    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primary6685); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    IDENTIFIER491_tree = (Object)adaptor.create(IDENTIFIER491);
                    	    adaptor.addChild(root_0, IDENTIFIER491_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop134;
                        }
                    } while (true);

                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1116:9: ( identifierSuffix )?
                    int alt135=2;
                    switch ( input.LA(1) ) {
                        case LBRACKET:
                            {
                            int LA135_1 = input.LA(2);

                            if ( (synpred212()) ) {
                                alt135=1;
                            }
                            }
                            break;
                        case LPAREN:
                            {
                            alt135=1;
                            }
                            break;
                        case DOT:
                            {
                            int LA135_3 = input.LA(2);

                            if ( (synpred212()) ) {
                                alt135=1;
                            }
                            }
                            break;
                    }

                    switch (alt135) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1116:10: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary6707);
                            identifierSuffix492=identifierSuffix();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, identifierSuffix492.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1118:9: 'super' superSuffix
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal493=(Token)input.LT(1);
                    match(input,SUPER,FOLLOW_SUPER_in_primary6728); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal493_tree = (Object)adaptor.create(string_literal493);
                    adaptor.addChild(root_0, string_literal493_tree);
                    }
                    pushFollow(FOLLOW_superSuffix_in_primary6738);
                    superSuffix494=superSuffix();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, superSuffix494.getTree());

                    }
                    break;
                case 5 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1120:9: literal
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_literal_in_primary6748);
                    literal495=literal();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, literal495.getTree());

                    }
                    break;
                case 6 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1121:9: creator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_creator_in_primary6758);
                    creator496=creator();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, creator496.getTree());

                    }
                    break;
                case 7 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1122:9: primitiveType ( '[' ']' )* '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_primary6768);
                    primitiveType497=primitiveType();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, primitiveType497.getTree());
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1123:9: ( '[' ']' )*
                    loop136:
                    do {
                        int alt136=2;
                        int LA136_0 = input.LA(1);

                        if ( (LA136_0==LBRACKET) ) {
                            alt136=1;
                        }


                        switch (alt136) {
                    	case 1 :
                    	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1123:10: '[' ']'
                    	    {
                    	    char_literal498=(Token)input.LT(1);
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_primary6779); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal498_tree = (Object)adaptor.create(char_literal498);
                    	    adaptor.addChild(root_0, char_literal498_tree);
                    	    }
                    	    char_literal499=(Token)input.LT(1);
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_primary6781); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal499_tree = (Object)adaptor.create(char_literal499);
                    	    adaptor.addChild(root_0, char_literal499_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop136;
                        }
                    } while (true);

                    char_literal500=(Token)input.LT(1);
                    match(input,DOT,FOLLOW_DOT_in_primary6802); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal500_tree = (Object)adaptor.create(char_literal500);
                    adaptor.addChild(root_0, char_literal500_tree);
                    }
                    string_literal501=(Token)input.LT(1);
                    match(input,CLASS,FOLLOW_CLASS_in_primary6804); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal501_tree = (Object)adaptor.create(string_literal501);
                    adaptor.addChild(root_0, string_literal501_tree);
                    }

                    }
                    break;
                case 8 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1126:9: 'void' '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal502=(Token)input.LT(1);
                    match(input,VOID,FOLLOW_VOID_in_primary6814); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal502_tree = (Object)adaptor.create(string_literal502);
                    adaptor.addChild(root_0, string_literal502_tree);
                    }
                    char_literal503=(Token)input.LT(1);
                    match(input,DOT,FOLLOW_DOT_in_primary6816); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal503_tree = (Object)adaptor.create(char_literal503);
                    adaptor.addChild(root_0, char_literal503_tree);
                    }
                    string_literal504=(Token)input.LT(1);
                    match(input,CLASS,FOLLOW_CLASS_in_primary6818); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal504_tree = (Object)adaptor.create(string_literal504);
                    adaptor.addChild(root_0, string_literal504_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 89, primary_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end primary

    public static class superSuffix_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start superSuffix
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1130:1: superSuffix : ( arguments | '.' ( typeArguments )? IDENTIFIER ( arguments )? );
    public final superSuffix_return superSuffix() throws RecognitionException {
        superSuffix_return retval = new superSuffix_return();
        retval.start = input.LT(1);
        int superSuffix_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal506=null;
        Token IDENTIFIER508=null;
        arguments_return arguments505 = null;

        typeArguments_return typeArguments507 = null;

        arguments_return arguments509 = null;


        Object char_literal506_tree=null;
        Object IDENTIFIER508_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 90) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1131:5: ( arguments | '.' ( typeArguments )? IDENTIFIER ( arguments )? )
            int alt140=2;
            int LA140_0 = input.LA(1);

            if ( (LA140_0==LPAREN) ) {
                alt140=1;
            }
            else if ( (LA140_0==DOT) ) {
                alt140=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1130:1: superSuffix : ( arguments | '.' ( typeArguments )? IDENTIFIER ( arguments )? );", 140, 0, input);

                throw nvae;
            }
            switch (alt140) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1131:9: arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arguments_in_superSuffix6844);
                    arguments505=arguments();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, arguments505.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1132:9: '.' ( typeArguments )? IDENTIFIER ( arguments )?
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal506=(Token)input.LT(1);
                    match(input,DOT,FOLLOW_DOT_in_superSuffix6854); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal506_tree = (Object)adaptor.create(char_literal506);
                    adaptor.addChild(root_0, char_literal506_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1132:13: ( typeArguments )?
                    int alt138=2;
                    int LA138_0 = input.LA(1);

                    if ( (LA138_0==LT) ) {
                        alt138=1;
                    }
                    switch (alt138) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1132:14: typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_superSuffix6857);
                            typeArguments507=typeArguments();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, typeArguments507.getTree());

                            }
                            break;

                    }

                    IDENTIFIER508=(Token)input.LT(1);
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_superSuffix6878); if (failed) return retval;
                    if ( backtracking==0 ) {
                    IDENTIFIER508_tree = (Object)adaptor.create(IDENTIFIER508);
                    adaptor.addChild(root_0, IDENTIFIER508_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1135:9: ( arguments )?
                    int alt139=2;
                    int LA139_0 = input.LA(1);

                    if ( (LA139_0==LPAREN) ) {
                        alt139=1;
                    }
                    switch (alt139) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1135:10: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix6889);
                            arguments509=arguments();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, arguments509.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 90, superSuffix_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end superSuffix

    public static class identifierSuffix_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start identifierSuffix
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1140:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' nonWildcardTypeArguments IDENTIFIER arguments | '.' 'this' | '.' 'super' arguments | innerCreator );
    public final identifierSuffix_return identifierSuffix() throws RecognitionException {
        identifierSuffix_return retval = new identifierSuffix_return();
        retval.start = input.LT(1);
        int identifierSuffix_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal510=null;
        Token char_literal511=null;
        Token char_literal512=null;
        Token string_literal513=null;
        Token char_literal514=null;
        Token char_literal516=null;
        Token char_literal518=null;
        Token string_literal519=null;
        Token char_literal520=null;
        Token IDENTIFIER522=null;
        Token char_literal524=null;
        Token string_literal525=null;
        Token char_literal526=null;
        Token string_literal527=null;
        expression_return expression515 = null;

        arguments_return arguments517 = null;

        nonWildcardTypeArguments_return nonWildcardTypeArguments521 = null;

        arguments_return arguments523 = null;

        arguments_return arguments528 = null;

        innerCreator_return innerCreator529 = null;


        Object char_literal510_tree=null;
        Object char_literal511_tree=null;
        Object char_literal512_tree=null;
        Object string_literal513_tree=null;
        Object char_literal514_tree=null;
        Object char_literal516_tree=null;
        Object char_literal518_tree=null;
        Object string_literal519_tree=null;
        Object char_literal520_tree=null;
        Object IDENTIFIER522_tree=null;
        Object char_literal524_tree=null;
        Object string_literal525_tree=null;
        Object char_literal526_tree=null;
        Object string_literal527_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 91) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1141:5: ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' nonWildcardTypeArguments IDENTIFIER arguments | '.' 'this' | '.' 'super' arguments | innerCreator )
            int alt143=8;
            switch ( input.LA(1) ) {
            case LBRACKET:
                {
                int LA143_1 = input.LA(2);

                if ( (LA143_1==RBRACKET) ) {
                    alt143=1;
                }
                else if ( ((LA143_1>=IDENTIFIER && LA143_1<=NULL)||LA143_1==BOOLEAN||LA143_1==BYTE||LA143_1==CHAR||LA143_1==DOUBLE||LA143_1==FLOAT||LA143_1==INT||LA143_1==LONG||LA143_1==NEW||LA143_1==SHORT||LA143_1==SUPER||LA143_1==THIS||LA143_1==VOID||LA143_1==LPAREN||(LA143_1>=BANG && LA143_1<=TILDE)||(LA143_1>=PLUSPLUS && LA143_1<=SUB)) ) {
                    alt143=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("1140:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' nonWildcardTypeArguments IDENTIFIER arguments | '.' 'this' | '.' 'super' arguments | innerCreator );", 143, 1, input);

                    throw nvae;
                }
                }
                break;
            case LPAREN:
                {
                alt143=3;
                }
                break;
            case DOT:
                {
                switch ( input.LA(2) ) {
                case CLASS:
                    {
                    alt143=4;
                    }
                    break;
                case THIS:
                    {
                    alt143=6;
                    }
                    break;
                case NEW:
                    {
                    alt143=8;
                    }
                    break;
                case SUPER:
                    {
                    alt143=7;
                    }
                    break;
                case LT:
                    {
                    alt143=5;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("1140:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' nonWildcardTypeArguments IDENTIFIER arguments | '.' 'this' | '.' 'super' arguments | innerCreator );", 143, 3, input);

                    throw nvae;
                }

                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1140:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' nonWildcardTypeArguments IDENTIFIER arguments | '.' 'this' | '.' 'super' arguments | innerCreator );", 143, 0, input);

                throw nvae;
            }

            switch (alt143) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1141:9: ( '[' ']' )+ '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1141:9: ( '[' ']' )+
                    int cnt141=0;
                    loop141:
                    do {
                        int alt141=2;
                        int LA141_0 = input.LA(1);

                        if ( (LA141_0==LBRACKET) ) {
                            alt141=1;
                        }


                        switch (alt141) {
                    	case 1 :
                    	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1141:10: '[' ']'
                    	    {
                    	    char_literal510=(Token)input.LT(1);
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_identifierSuffix6922); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal510_tree = (Object)adaptor.create(char_literal510);
                    	    adaptor.addChild(root_0, char_literal510_tree);
                    	    }
                    	    char_literal511=(Token)input.LT(1);
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_identifierSuffix6924); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal511_tree = (Object)adaptor.create(char_literal511);
                    	    adaptor.addChild(root_0, char_literal511_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt141 >= 1 ) break loop141;
                    	    if (backtracking>0) {failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(141, input);
                                throw eee;
                        }
                        cnt141++;
                    } while (true);

                    char_literal512=(Token)input.LT(1);
                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix6945); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal512_tree = (Object)adaptor.create(char_literal512);
                    adaptor.addChild(root_0, char_literal512_tree);
                    }
                    string_literal513=(Token)input.LT(1);
                    match(input,CLASS,FOLLOW_CLASS_in_identifierSuffix6947); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal513_tree = (Object)adaptor.create(string_literal513);
                    adaptor.addChild(root_0, string_literal513_tree);
                    }

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1144:9: ( '[' expression ']' )+
                    {
                    root_0 = (Object)adaptor.nil();

                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1144:9: ( '[' expression ']' )+
                    int cnt142=0;
                    loop142:
                    do {
                        int alt142=2;
                        int LA142_0 = input.LA(1);

                        if ( (LA142_0==LBRACKET) ) {
                            int LA142_2 = input.LA(2);

                            if ( (synpred224()) ) {
                                alt142=1;
                            }


                        }


                        switch (alt142) {
                    	case 1 :
                    	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1144:10: '[' expression ']'
                    	    {
                    	    char_literal514=(Token)input.LT(1);
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_identifierSuffix6958); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal514_tree = (Object)adaptor.create(char_literal514);
                    	    adaptor.addChild(root_0, char_literal514_tree);
                    	    }
                    	    pushFollow(FOLLOW_expression_in_identifierSuffix6960);
                    	    expression515=expression();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) adaptor.addChild(root_0, expression515.getTree());
                    	    char_literal516=(Token)input.LT(1);
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_identifierSuffix6962); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal516_tree = (Object)adaptor.create(char_literal516);
                    	    adaptor.addChild(root_0, char_literal516_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt142 >= 1 ) break loop142;
                    	    if (backtracking>0) {failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(142, input);
                                throw eee;
                        }
                        cnt142++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1146:9: arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arguments_in_identifierSuffix6983);
                    arguments517=arguments();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, arguments517.getTree());

                    }
                    break;
                case 4 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1147:9: '.' 'class'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal518=(Token)input.LT(1);
                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix6993); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal518_tree = (Object)adaptor.create(char_literal518);
                    adaptor.addChild(root_0, char_literal518_tree);
                    }
                    string_literal519=(Token)input.LT(1);
                    match(input,CLASS,FOLLOW_CLASS_in_identifierSuffix6995); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal519_tree = (Object)adaptor.create(string_literal519);
                    adaptor.addChild(root_0, string_literal519_tree);
                    }

                    }
                    break;
                case 5 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1148:9: '.' nonWildcardTypeArguments IDENTIFIER arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal520=(Token)input.LT(1);
                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix7005); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal520_tree = (Object)adaptor.create(char_literal520);
                    adaptor.addChild(root_0, char_literal520_tree);
                    }
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_identifierSuffix7007);
                    nonWildcardTypeArguments521=nonWildcardTypeArguments();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments521.getTree());
                    IDENTIFIER522=(Token)input.LT(1);
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_identifierSuffix7009); if (failed) return retval;
                    if ( backtracking==0 ) {
                    IDENTIFIER522_tree = (Object)adaptor.create(IDENTIFIER522);
                    adaptor.addChild(root_0, IDENTIFIER522_tree);
                    }
                    pushFollow(FOLLOW_arguments_in_identifierSuffix7011);
                    arguments523=arguments();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, arguments523.getTree());

                    }
                    break;
                case 6 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1149:9: '.' 'this'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal524=(Token)input.LT(1);
                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix7021); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal524_tree = (Object)adaptor.create(char_literal524);
                    adaptor.addChild(root_0, char_literal524_tree);
                    }
                    string_literal525=(Token)input.LT(1);
                    match(input,THIS,FOLLOW_THIS_in_identifierSuffix7023); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal525_tree = (Object)adaptor.create(string_literal525);
                    adaptor.addChild(root_0, string_literal525_tree);
                    }

                    }
                    break;
                case 7 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1150:9: '.' 'super' arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal526=(Token)input.LT(1);
                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix7033); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal526_tree = (Object)adaptor.create(char_literal526);
                    adaptor.addChild(root_0, char_literal526_tree);
                    }
                    string_literal527=(Token)input.LT(1);
                    match(input,SUPER,FOLLOW_SUPER_in_identifierSuffix7035); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal527_tree = (Object)adaptor.create(string_literal527);
                    adaptor.addChild(root_0, string_literal527_tree);
                    }
                    pushFollow(FOLLOW_arguments_in_identifierSuffix7037);
                    arguments528=arguments();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, arguments528.getTree());

                    }
                    break;
                case 8 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1151:9: innerCreator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_innerCreator_in_identifierSuffix7047);
                    innerCreator529=innerCreator();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, innerCreator529.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 91, identifierSuffix_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end identifierSuffix

    public static class selector_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start selector
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1155:1: selector : ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | innerCreator | '[' expression ']' );
    public final selector_return selector() throws RecognitionException {
        selector_return retval = new selector_return();
        retval.start = input.LT(1);
        int selector_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal530=null;
        Token IDENTIFIER531=null;
        Token char_literal533=null;
        Token string_literal534=null;
        Token char_literal535=null;
        Token string_literal536=null;
        Token char_literal539=null;
        Token char_literal541=null;
        arguments_return arguments532 = null;

        superSuffix_return superSuffix537 = null;

        innerCreator_return innerCreator538 = null;

        expression_return expression540 = null;


        Object char_literal530_tree=null;
        Object IDENTIFIER531_tree=null;
        Object char_literal533_tree=null;
        Object string_literal534_tree=null;
        Object char_literal535_tree=null;
        Object string_literal536_tree=null;
        Object char_literal539_tree=null;
        Object char_literal541_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 92) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1156:5: ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | innerCreator | '[' expression ']' )
            int alt145=5;
            int LA145_0 = input.LA(1);

            if ( (LA145_0==DOT) ) {
                switch ( input.LA(2) ) {
                case NEW:
                    {
                    alt145=4;
                    }
                    break;
                case IDENTIFIER:
                    {
                    alt145=1;
                    }
                    break;
                case THIS:
                    {
                    alt145=2;
                    }
                    break;
                case SUPER:
                    {
                    alt145=3;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("1155:1: selector : ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | innerCreator | '[' expression ']' );", 145, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA145_0==LBRACKET) ) {
                alt145=5;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1155:1: selector : ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | innerCreator | '[' expression ']' );", 145, 0, input);

                throw nvae;
            }
            switch (alt145) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1156:9: '.' IDENTIFIER ( arguments )?
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal530=(Token)input.LT(1);
                    match(input,DOT,FOLLOW_DOT_in_selector7069); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal530_tree = (Object)adaptor.create(char_literal530);
                    adaptor.addChild(root_0, char_literal530_tree);
                    }
                    IDENTIFIER531=(Token)input.LT(1);
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_selector7071); if (failed) return retval;
                    if ( backtracking==0 ) {
                    IDENTIFIER531_tree = (Object)adaptor.create(IDENTIFIER531);
                    adaptor.addChild(root_0, IDENTIFIER531_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1157:9: ( arguments )?
                    int alt144=2;
                    int LA144_0 = input.LA(1);

                    if ( (LA144_0==LPAREN) ) {
                        alt144=1;
                    }
                    switch (alt144) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1157:10: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector7082);
                            arguments532=arguments();
                            _fsp--;
                            if (failed) return retval;
                            if ( backtracking==0 ) adaptor.addChild(root_0, arguments532.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1159:9: '.' 'this'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal533=(Token)input.LT(1);
                    match(input,DOT,FOLLOW_DOT_in_selector7103); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal533_tree = (Object)adaptor.create(char_literal533);
                    adaptor.addChild(root_0, char_literal533_tree);
                    }
                    string_literal534=(Token)input.LT(1);
                    match(input,THIS,FOLLOW_THIS_in_selector7105); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal534_tree = (Object)adaptor.create(string_literal534);
                    adaptor.addChild(root_0, string_literal534_tree);
                    }

                    }
                    break;
                case 3 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1160:9: '.' 'super' superSuffix
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal535=(Token)input.LT(1);
                    match(input,DOT,FOLLOW_DOT_in_selector7115); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal535_tree = (Object)adaptor.create(char_literal535);
                    adaptor.addChild(root_0, char_literal535_tree);
                    }
                    string_literal536=(Token)input.LT(1);
                    match(input,SUPER,FOLLOW_SUPER_in_selector7117); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal536_tree = (Object)adaptor.create(string_literal536);
                    adaptor.addChild(root_0, string_literal536_tree);
                    }
                    pushFollow(FOLLOW_superSuffix_in_selector7127);
                    superSuffix537=superSuffix();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, superSuffix537.getTree());

                    }
                    break;
                case 4 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1162:9: innerCreator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_innerCreator_in_selector7137);
                    innerCreator538=innerCreator();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, innerCreator538.getTree());

                    }
                    break;
                case 5 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1163:9: '[' expression ']'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal539=(Token)input.LT(1);
                    match(input,LBRACKET,FOLLOW_LBRACKET_in_selector7147); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal539_tree = (Object)adaptor.create(char_literal539);
                    adaptor.addChild(root_0, char_literal539_tree);
                    }
                    pushFollow(FOLLOW_expression_in_selector7149);
                    expression540=expression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expression540.getTree());
                    char_literal541=(Token)input.LT(1);
                    match(input,RBRACKET,FOLLOW_RBRACKET_in_selector7151); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal541_tree = (Object)adaptor.create(char_literal541);
                    adaptor.addChild(root_0, char_literal541_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 92, selector_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end selector

    public static class creator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start creator
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1166:1: creator : ( 'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest | 'new' classOrInterfaceType classCreatorRest | arrayCreator );
    public final creator_return creator() throws RecognitionException {
        creator_return retval = new creator_return();
        retval.start = input.LT(1);
        int creator_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal542=null;
        Token string_literal546=null;
        nonWildcardTypeArguments_return nonWildcardTypeArguments543 = null;

        classOrInterfaceType_return classOrInterfaceType544 = null;

        classCreatorRest_return classCreatorRest545 = null;

        classOrInterfaceType_return classOrInterfaceType547 = null;

        classCreatorRest_return classCreatorRest548 = null;

        arrayCreator_return arrayCreator549 = null;


        Object string_literal542_tree=null;
        Object string_literal546_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 93) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1167:5: ( 'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest | 'new' classOrInterfaceType classCreatorRest | arrayCreator )
            int alt146=3;
            alt146 = dfa146.predict(input);
            switch (alt146) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1167:9: 'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal542=(Token)input.LT(1);
                    match(input,NEW,FOLLOW_NEW_in_creator7171); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal542_tree = (Object)adaptor.create(string_literal542);
                    adaptor.addChild(root_0, string_literal542_tree);
                    }
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator7173);
                    nonWildcardTypeArguments543=nonWildcardTypeArguments();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments543.getTree());
                    pushFollow(FOLLOW_classOrInterfaceType_in_creator7175);
                    classOrInterfaceType544=classOrInterfaceType();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceType544.getTree());
                    pushFollow(FOLLOW_classCreatorRest_in_creator7177);
                    classCreatorRest545=classCreatorRest();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, classCreatorRest545.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1168:9: 'new' classOrInterfaceType classCreatorRest
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal546=(Token)input.LT(1);
                    match(input,NEW,FOLLOW_NEW_in_creator7187); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal546_tree = (Object)adaptor.create(string_literal546);
                    adaptor.addChild(root_0, string_literal546_tree);
                    }
                    pushFollow(FOLLOW_classOrInterfaceType_in_creator7189);
                    classOrInterfaceType547=classOrInterfaceType();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceType547.getTree());
                    pushFollow(FOLLOW_classCreatorRest_in_creator7191);
                    classCreatorRest548=classCreatorRest();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, classCreatorRest548.getTree());

                    }
                    break;
                case 3 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1169:9: arrayCreator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arrayCreator_in_creator7201);
                    arrayCreator549=arrayCreator();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, arrayCreator549.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 93, creator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end creator

    public static class arrayCreator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start arrayCreator
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1172:1: arrayCreator : ( 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer | 'new' createdName '[' expression ']' ( '[' expression ']' )* ( '[' ']' )* );
    public final arrayCreator_return arrayCreator() throws RecognitionException {
        arrayCreator_return retval = new arrayCreator_return();
        retval.start = input.LT(1);
        int arrayCreator_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal550=null;
        Token char_literal552=null;
        Token char_literal553=null;
        Token char_literal554=null;
        Token char_literal555=null;
        Token string_literal557=null;
        Token char_literal559=null;
        Token char_literal561=null;
        Token char_literal562=null;
        Token char_literal564=null;
        Token char_literal565=null;
        Token char_literal566=null;
        createdName_return createdName551 = null;

        arrayInitializer_return arrayInitializer556 = null;

        createdName_return createdName558 = null;

        expression_return expression560 = null;

        expression_return expression563 = null;


        Object string_literal550_tree=null;
        Object char_literal552_tree=null;
        Object char_literal553_tree=null;
        Object char_literal554_tree=null;
        Object char_literal555_tree=null;
        Object string_literal557_tree=null;
        Object char_literal559_tree=null;
        Object char_literal561_tree=null;
        Object char_literal562_tree=null;
        Object char_literal564_tree=null;
        Object char_literal565_tree=null;
        Object char_literal566_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 94) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1173:5: ( 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer | 'new' createdName '[' expression ']' ( '[' expression ']' )* ( '[' ']' )* )
            int alt150=2;
            alt150 = dfa150.predict(input);
            switch (alt150) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1173:9: 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal550=(Token)input.LT(1);
                    match(input,NEW,FOLLOW_NEW_in_arrayCreator7221); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal550_tree = (Object)adaptor.create(string_literal550);
                    adaptor.addChild(root_0, string_literal550_tree);
                    }
                    pushFollow(FOLLOW_createdName_in_arrayCreator7223);
                    createdName551=createdName();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, createdName551.getTree());
                    char_literal552=(Token)input.LT(1);
                    match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7233); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal552_tree = (Object)adaptor.create(char_literal552);
                    adaptor.addChild(root_0, char_literal552_tree);
                    }
                    char_literal553=(Token)input.LT(1);
                    match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7235); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal553_tree = (Object)adaptor.create(char_literal553);
                    adaptor.addChild(root_0, char_literal553_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1175:9: ( '[' ']' )*
                    loop147:
                    do {
                        int alt147=2;
                        int LA147_0 = input.LA(1);

                        if ( (LA147_0==LBRACKET) ) {
                            alt147=1;
                        }


                        switch (alt147) {
                    	case 1 :
                    	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1175:10: '[' ']'
                    	    {
                    	    char_literal554=(Token)input.LT(1);
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7246); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal554_tree = (Object)adaptor.create(char_literal554);
                    	    adaptor.addChild(root_0, char_literal554_tree);
                    	    }
                    	    char_literal555=(Token)input.LT(1);
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7248); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal555_tree = (Object)adaptor.create(char_literal555);
                    	    adaptor.addChild(root_0, char_literal555_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop147;
                        }
                    } while (true);

                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreator7269);
                    arrayInitializer556=arrayInitializer();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, arrayInitializer556.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1179:9: 'new' createdName '[' expression ']' ( '[' expression ']' )* ( '[' ']' )*
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal557=(Token)input.LT(1);
                    match(input,NEW,FOLLOW_NEW_in_arrayCreator7280); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal557_tree = (Object)adaptor.create(string_literal557);
                    adaptor.addChild(root_0, string_literal557_tree);
                    }
                    pushFollow(FOLLOW_createdName_in_arrayCreator7282);
                    createdName558=createdName();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, createdName558.getTree());
                    char_literal559=(Token)input.LT(1);
                    match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7292); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal559_tree = (Object)adaptor.create(char_literal559);
                    adaptor.addChild(root_0, char_literal559_tree);
                    }
                    pushFollow(FOLLOW_expression_in_arrayCreator7294);
                    expression560=expression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expression560.getTree());
                    char_literal561=(Token)input.LT(1);
                    match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7304); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal561_tree = (Object)adaptor.create(char_literal561);
                    adaptor.addChild(root_0, char_literal561_tree);
                    }
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1182:9: ( '[' expression ']' )*
                    loop148:
                    do {
                        int alt148=2;
                        int LA148_0 = input.LA(1);

                        if ( (LA148_0==LBRACKET) ) {
                            int LA148_1 = input.LA(2);

                            if ( (synpred240()) ) {
                                alt148=1;
                            }


                        }


                        switch (alt148) {
                    	case 1 :
                    	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1182:13: '[' expression ']'
                    	    {
                    	    char_literal562=(Token)input.LT(1);
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7318); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal562_tree = (Object)adaptor.create(char_literal562);
                    	    adaptor.addChild(root_0, char_literal562_tree);
                    	    }
                    	    pushFollow(FOLLOW_expression_in_arrayCreator7320);
                    	    expression563=expression();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) adaptor.addChild(root_0, expression563.getTree());
                    	    char_literal564=(Token)input.LT(1);
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7334); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal564_tree = (Object)adaptor.create(char_literal564);
                    	    adaptor.addChild(root_0, char_literal564_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop148;
                        }
                    } while (true);

                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1185:9: ( '[' ']' )*
                    loop149:
                    do {
                        int alt149=2;
                        int LA149_0 = input.LA(1);

                        if ( (LA149_0==LBRACKET) ) {
                            int LA149_2 = input.LA(2);

                            if ( (LA149_2==RBRACKET) ) {
                                alt149=1;
                            }


                        }


                        switch (alt149) {
                    	case 1 :
                    	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1185:10: '[' ']'
                    	    {
                    	    char_literal565=(Token)input.LT(1);
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7356); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal565_tree = (Object)adaptor.create(char_literal565);
                    	    adaptor.addChild(root_0, char_literal565_tree);
                    	    }
                    	    char_literal566=(Token)input.LT(1);
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7358); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal566_tree = (Object)adaptor.create(char_literal566);
                    	    adaptor.addChild(root_0, char_literal566_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop149;
                        }
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 94, arrayCreator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end arrayCreator

    public static class variableInitializer_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start variableInitializer
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1189:1: variableInitializer : ( arrayInitializer | expression );
    public final variableInitializer_return variableInitializer() throws RecognitionException {
        variableInitializer_return retval = new variableInitializer_return();
        retval.start = input.LT(1);
        int variableInitializer_StartIndex = input.index();
        Object root_0 = null;

        arrayInitializer_return arrayInitializer567 = null;

        expression_return expression568 = null;



        try {
            if ( backtracking>0 && alreadyParsedRule(input, 95) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1190:5: ( arrayInitializer | expression )
            int alt151=2;
            int LA151_0 = input.LA(1);

            if ( (LA151_0==LBRACE) ) {
                alt151=1;
            }
            else if ( ((LA151_0>=IDENTIFIER && LA151_0<=NULL)||LA151_0==BOOLEAN||LA151_0==BYTE||LA151_0==CHAR||LA151_0==DOUBLE||LA151_0==FLOAT||LA151_0==INT||LA151_0==LONG||LA151_0==NEW||LA151_0==SHORT||LA151_0==SUPER||LA151_0==THIS||LA151_0==VOID||LA151_0==LPAREN||(LA151_0>=BANG && LA151_0<=TILDE)||(LA151_0>=PLUSPLUS && LA151_0<=SUB)) ) {
                alt151=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1189:1: variableInitializer : ( arrayInitializer | expression );", 151, 0, input);

                throw nvae;
            }
            switch (alt151) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1190:9: arrayInitializer
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer7389);
                    arrayInitializer567=arrayInitializer();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, arrayInitializer567.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1191:9: expression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expression_in_variableInitializer7399);
                    expression568=expression();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expression568.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 95, variableInitializer_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end variableInitializer

    public static class arrayInitializer_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start arrayInitializer
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1194:1: arrayInitializer : '{' ( variableInitializer ( ',' variableInitializer )* )? ( ',' )? '}' ;
    public final arrayInitializer_return arrayInitializer() throws RecognitionException {
        arrayInitializer_return retval = new arrayInitializer_return();
        retval.start = input.LT(1);
        int arrayInitializer_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal569=null;
        Token char_literal571=null;
        Token char_literal573=null;
        Token char_literal574=null;
        variableInitializer_return variableInitializer570 = null;

        variableInitializer_return variableInitializer572 = null;


        Object char_literal569_tree=null;
        Object char_literal571_tree=null;
        Object char_literal573_tree=null;
        Object char_literal574_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 96) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1195:5: ( '{' ( variableInitializer ( ',' variableInitializer )* )? ( ',' )? '}' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1195:9: '{' ( variableInitializer ( ',' variableInitializer )* )? ( ',' )? '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal569=(Token)input.LT(1);
            match(input,LBRACE,FOLLOW_LBRACE_in_arrayInitializer7419); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal569_tree = (Object)adaptor.create(char_literal569);
            adaptor.addChild(root_0, char_literal569_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1196:13: ( variableInitializer ( ',' variableInitializer )* )?
            int alt153=2;
            int LA153_0 = input.LA(1);

            if ( ((LA153_0>=IDENTIFIER && LA153_0<=NULL)||LA153_0==BOOLEAN||LA153_0==BYTE||LA153_0==CHAR||LA153_0==DOUBLE||LA153_0==FLOAT||LA153_0==INT||LA153_0==LONG||LA153_0==NEW||LA153_0==SHORT||LA153_0==SUPER||LA153_0==THIS||LA153_0==VOID||LA153_0==LPAREN||LA153_0==LBRACE||(LA153_0>=BANG && LA153_0<=TILDE)||(LA153_0>=PLUSPLUS && LA153_0<=SUB)) ) {
                alt153=1;
            }
            switch (alt153) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1196:14: variableInitializer ( ',' variableInitializer )*
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer7435);
                    variableInitializer570=variableInitializer();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, variableInitializer570.getTree());
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1197:17: ( ',' variableInitializer )*
                    loop152:
                    do {
                        int alt152=2;
                        int LA152_0 = input.LA(1);

                        if ( (LA152_0==COMMA) ) {
                            int LA152_1 = input.LA(2);

                            if ( ((LA152_1>=IDENTIFIER && LA152_1<=NULL)||LA152_1==BOOLEAN||LA152_1==BYTE||LA152_1==CHAR||LA152_1==DOUBLE||LA152_1==FLOAT||LA152_1==INT||LA152_1==LONG||LA152_1==NEW||LA152_1==SHORT||LA152_1==SUPER||LA152_1==THIS||LA152_1==VOID||LA152_1==LPAREN||LA152_1==LBRACE||(LA152_1>=BANG && LA152_1<=TILDE)||(LA152_1>=PLUSPLUS && LA152_1<=SUB)) ) {
                                alt152=1;
                            }


                        }


                        switch (alt152) {
                    	case 1 :
                    	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1197:18: ',' variableInitializer
                    	    {
                    	    char_literal571=(Token)input.LT(1);
                    	    match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer7454); if (failed) return retval;
                    	    if ( backtracking==0 ) {
                    	    char_literal571_tree = (Object)adaptor.create(char_literal571);
                    	    adaptor.addChild(root_0, char_literal571_tree);
                    	    }
                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer7456);
                    	    variableInitializer572=variableInitializer();
                    	    _fsp--;
                    	    if (failed) return retval;
                    	    if ( backtracking==0 ) adaptor.addChild(root_0, variableInitializer572.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop152;
                        }
                    } while (true);


                    }
                    break;

            }

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1200:13: ( ',' )?
            int alt154=2;
            int LA154_0 = input.LA(1);

            if ( (LA154_0==COMMA) ) {
                alt154=1;
            }
            switch (alt154) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1200:14: ','
                    {
                    char_literal573=(Token)input.LT(1);
                    match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer7506); if (failed) return retval;
                    if ( backtracking==0 ) {
                    char_literal573_tree = (Object)adaptor.create(char_literal573);
                    adaptor.addChild(root_0, char_literal573_tree);
                    }

                    }
                    break;

            }

            char_literal574=(Token)input.LT(1);
            match(input,RBRACE,FOLLOW_RBRACE_in_arrayInitializer7519); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal574_tree = (Object)adaptor.create(char_literal574);
            adaptor.addChild(root_0, char_literal574_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 96, arrayInitializer_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end arrayInitializer

    public static class createdName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start createdName
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1205:1: createdName : ( classOrInterfaceType | primitiveType );
    public final createdName_return createdName() throws RecognitionException {
        createdName_return retval = new createdName_return();
        retval.start = input.LT(1);
        int createdName_StartIndex = input.index();
        Object root_0 = null;

        classOrInterfaceType_return classOrInterfaceType575 = null;

        primitiveType_return primitiveType576 = null;



        try {
            if ( backtracking>0 && alreadyParsedRule(input, 97) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1206:5: ( classOrInterfaceType | primitiveType )
            int alt155=2;
            int LA155_0 = input.LA(1);

            if ( (LA155_0==IDENTIFIER) ) {
                alt155=1;
            }
            else if ( (LA155_0==BOOLEAN||LA155_0==BYTE||LA155_0==CHAR||LA155_0==DOUBLE||LA155_0==FLOAT||LA155_0==INT||LA155_0==LONG||LA155_0==SHORT) ) {
                alt155=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1205:1: createdName : ( classOrInterfaceType | primitiveType );", 155, 0, input);

                throw nvae;
            }
            switch (alt155) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1206:9: classOrInterfaceType
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_classOrInterfaceType_in_createdName7553);
                    classOrInterfaceType575=classOrInterfaceType();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceType575.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1207:9: primitiveType
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_createdName7563);
                    primitiveType576=primitiveType();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, primitiveType576.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 97, createdName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end createdName

    public static class innerCreator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start innerCreator
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1210:1: innerCreator : '.' 'new' ( nonWildcardTypeArguments )? IDENTIFIER ( typeArguments )? classCreatorRest ;
    public final innerCreator_return innerCreator() throws RecognitionException {
        innerCreator_return retval = new innerCreator_return();
        retval.start = input.LT(1);
        int innerCreator_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal577=null;
        Token string_literal578=null;
        Token IDENTIFIER580=null;
        nonWildcardTypeArguments_return nonWildcardTypeArguments579 = null;

        typeArguments_return typeArguments581 = null;

        classCreatorRest_return classCreatorRest582 = null;


        Object char_literal577_tree=null;
        Object string_literal578_tree=null;
        Object IDENTIFIER580_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 98) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1211:5: ( '.' 'new' ( nonWildcardTypeArguments )? IDENTIFIER ( typeArguments )? classCreatorRest )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1211:9: '.' 'new' ( nonWildcardTypeArguments )? IDENTIFIER ( typeArguments )? classCreatorRest
            {
            root_0 = (Object)adaptor.nil();

            char_literal577=(Token)input.LT(1);
            match(input,DOT,FOLLOW_DOT_in_innerCreator7584); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal577_tree = (Object)adaptor.create(char_literal577);
            adaptor.addChild(root_0, char_literal577_tree);
            }
            string_literal578=(Token)input.LT(1);
            match(input,NEW,FOLLOW_NEW_in_innerCreator7586); if (failed) return retval;
            if ( backtracking==0 ) {
            string_literal578_tree = (Object)adaptor.create(string_literal578);
            adaptor.addChild(root_0, string_literal578_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1212:9: ( nonWildcardTypeArguments )?
            int alt156=2;
            int LA156_0 = input.LA(1);

            if ( (LA156_0==LT) ) {
                alt156=1;
            }
            switch (alt156) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1212:10: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_innerCreator7597);
                    nonWildcardTypeArguments579=nonWildcardTypeArguments();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments579.getTree());

                    }
                    break;

            }

            IDENTIFIER580=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_innerCreator7618); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER580_tree = (Object)adaptor.create(IDENTIFIER580);
            adaptor.addChild(root_0, IDENTIFIER580_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1215:9: ( typeArguments )?
            int alt157=2;
            int LA157_0 = input.LA(1);

            if ( (LA157_0==LT) ) {
                alt157=1;
            }
            switch (alt157) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1215:10: typeArguments
                    {
                    pushFollow(FOLLOW_typeArguments_in_innerCreator7629);
                    typeArguments581=typeArguments();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, typeArguments581.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_classCreatorRest_in_innerCreator7650);
            classCreatorRest582=classCreatorRest();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, classCreatorRest582.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 98, innerCreator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end innerCreator

    public static class classCreatorRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start classCreatorRest
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1221:1: classCreatorRest : arguments ( classBody )? ;
    public final classCreatorRest_return classCreatorRest() throws RecognitionException {
        classCreatorRest_return retval = new classCreatorRest_return();
        retval.start = input.LT(1);
        int classCreatorRest_StartIndex = input.index();
        Object root_0 = null;

        arguments_return arguments583 = null;

        classBody_return classBody584 = null;



        try {
            if ( backtracking>0 && alreadyParsedRule(input, 99) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1222:5: ( arguments ( classBody )? )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1222:9: arguments ( classBody )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_arguments_in_classCreatorRest7671);
            arguments583=arguments();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, arguments583.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1223:9: ( classBody )?
            int alt158=2;
            int LA158_0 = input.LA(1);

            if ( (LA158_0==LBRACE) ) {
                alt158=1;
            }
            switch (alt158) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1223:10: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_classCreatorRest7682);
                    classBody584=classBody();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, classBody584.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 99, classCreatorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end classCreatorRest

    public static class nonWildcardTypeArguments_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start nonWildcardTypeArguments
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1228:1: nonWildcardTypeArguments : '<' typeList '>' ;
    public final nonWildcardTypeArguments_return nonWildcardTypeArguments() throws RecognitionException {
        nonWildcardTypeArguments_return retval = new nonWildcardTypeArguments_return();
        retval.start = input.LT(1);
        int nonWildcardTypeArguments_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal585=null;
        Token char_literal587=null;
        typeList_return typeList586 = null;


        Object char_literal585_tree=null;
        Object char_literal587_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 100) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1229:5: ( '<' typeList '>' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1229:9: '<' typeList '>'
            {
            root_0 = (Object)adaptor.nil();

            char_literal585=(Token)input.LT(1);
            match(input,LT,FOLLOW_LT_in_nonWildcardTypeArguments7714); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal585_tree = (Object)adaptor.create(char_literal585);
            adaptor.addChild(root_0, char_literal585_tree);
            }
            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments7716);
            typeList586=typeList();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, typeList586.getTree());
            char_literal587=(Token)input.LT(1);
            match(input,GT,FOLLOW_GT_in_nonWildcardTypeArguments7726); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal587_tree = (Object)adaptor.create(char_literal587);
            adaptor.addChild(root_0, char_literal587_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 100, nonWildcardTypeArguments_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end nonWildcardTypeArguments

    public static class arguments_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start arguments
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1233:1: arguments : '(' ( expressionList )? ')' ;
    public final arguments_return arguments() throws RecognitionException {
        arguments_return retval = new arguments_return();
        retval.start = input.LT(1);
        int arguments_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal588=null;
        Token char_literal590=null;
        expressionList_return expressionList589 = null;


        Object char_literal588_tree=null;
        Object char_literal590_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 101) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1234:5: ( '(' ( expressionList )? ')' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1234:9: '(' ( expressionList )? ')'
            {
            root_0 = (Object)adaptor.nil();

            char_literal588=(Token)input.LT(1);
            match(input,LPAREN,FOLLOW_LPAREN_in_arguments7746); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal588_tree = (Object)adaptor.create(char_literal588);
            adaptor.addChild(root_0, char_literal588_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1234:13: ( expressionList )?
            int alt159=2;
            int LA159_0 = input.LA(1);

            if ( ((LA159_0>=IDENTIFIER && LA159_0<=NULL)||LA159_0==BOOLEAN||LA159_0==BYTE||LA159_0==CHAR||LA159_0==DOUBLE||LA159_0==FLOAT||LA159_0==INT||LA159_0==LONG||LA159_0==NEW||LA159_0==SHORT||LA159_0==SUPER||LA159_0==THIS||LA159_0==VOID||LA159_0==LPAREN||(LA159_0>=BANG && LA159_0<=TILDE)||(LA159_0>=PLUSPLUS && LA159_0<=SUB)) ) {
                alt159=1;
            }
            switch (alt159) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1234:14: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments7749);
                    expressionList589=expressionList();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expressionList589.getTree());

                    }
                    break;

            }

            char_literal590=(Token)input.LT(1);
            match(input,RPAREN,FOLLOW_RPAREN_in_arguments7762); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal590_tree = (Object)adaptor.create(char_literal590);
            adaptor.addChild(root_0, char_literal590_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 101, arguments_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end arguments

    public static class literal_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start literal
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1238:1: literal : ( INTLITERAL | LONGLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | TRUE | FALSE | NULL );
    public final literal_return literal() throws RecognitionException {
        literal_return retval = new literal_return();
        retval.start = input.LT(1);
        int literal_StartIndex = input.index();
        Object root_0 = null;

        Token set591=null;

        Object set591_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 102) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1239:5: ( INTLITERAL | LONGLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | TRUE | FALSE | NULL )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:
            {
            root_0 = (Object)adaptor.nil();

            set591=(Token)input.LT(1);
            if ( (input.LA(1)>=INTLITERAL && input.LA(1)<=NULL) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set591));
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_literal0);    throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 102, literal_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end literal

    public static class classHeader_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start classHeader
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1250:1: classHeader : modifiers 'class' IDENTIFIER ;
    public final classHeader_return classHeader() throws RecognitionException {
        classHeader_return retval = new classHeader_return();
        retval.start = input.LT(1);
        int classHeader_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal593=null;
        Token IDENTIFIER594=null;
        modifiers_return modifiers592 = null;


        Object string_literal593_tree=null;
        Object IDENTIFIER594_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 103) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1255:5: ( modifiers 'class' IDENTIFIER )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1255:9: modifiers 'class' IDENTIFIER
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_classHeader7886);
            modifiers592=modifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, modifiers592.getTree());
            string_literal593=(Token)input.LT(1);
            match(input,CLASS,FOLLOW_CLASS_in_classHeader7888); if (failed) return retval;
            if ( backtracking==0 ) {
            string_literal593_tree = (Object)adaptor.create(string_literal593);
            adaptor.addChild(root_0, string_literal593_tree);
            }
            IDENTIFIER594=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classHeader7890); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER594_tree = (Object)adaptor.create(IDENTIFIER594);
            adaptor.addChild(root_0, IDENTIFIER594_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 103, classHeader_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end classHeader

    public static class enumHeader_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start enumHeader
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1258:1: enumHeader : modifiers ( 'enum' | IDENTIFIER ) IDENTIFIER ;
    public final enumHeader_return enumHeader() throws RecognitionException {
        enumHeader_return retval = new enumHeader_return();
        retval.start = input.LT(1);
        int enumHeader_StartIndex = input.index();
        Object root_0 = null;

        Token set596=null;
        Token IDENTIFIER597=null;
        modifiers_return modifiers595 = null;


        Object set596_tree=null;
        Object IDENTIFIER597_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 104) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1259:5: ( modifiers ( 'enum' | IDENTIFIER ) IDENTIFIER )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1259:9: modifiers ( 'enum' | IDENTIFIER ) IDENTIFIER
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_enumHeader7910);
            modifiers595=modifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, modifiers595.getTree());
            set596=(Token)input.LT(1);
            if ( input.LA(1)==IDENTIFIER||input.LA(1)==ENUM ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set596));
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_enumHeader7912);    throw mse;
            }

            IDENTIFIER597=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_enumHeader7918); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER597_tree = (Object)adaptor.create(IDENTIFIER597);
            adaptor.addChild(root_0, IDENTIFIER597_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 104, enumHeader_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end enumHeader

    public static class interfaceHeader_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start interfaceHeader
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1262:1: interfaceHeader : modifiers 'interface' IDENTIFIER ;
    public final interfaceHeader_return interfaceHeader() throws RecognitionException {
        interfaceHeader_return retval = new interfaceHeader_return();
        retval.start = input.LT(1);
        int interfaceHeader_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal599=null;
        Token IDENTIFIER600=null;
        modifiers_return modifiers598 = null;


        Object string_literal599_tree=null;
        Object IDENTIFIER600_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 105) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1263:5: ( modifiers 'interface' IDENTIFIER )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1263:9: modifiers 'interface' IDENTIFIER
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_interfaceHeader7938);
            modifiers598=modifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, modifiers598.getTree());
            string_literal599=(Token)input.LT(1);
            match(input,INTERFACE,FOLLOW_INTERFACE_in_interfaceHeader7940); if (failed) return retval;
            if ( backtracking==0 ) {
            string_literal599_tree = (Object)adaptor.create(string_literal599);
            adaptor.addChild(root_0, string_literal599_tree);
            }
            IDENTIFIER600=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_interfaceHeader7942); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER600_tree = (Object)adaptor.create(IDENTIFIER600);
            adaptor.addChild(root_0, IDENTIFIER600_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 105, interfaceHeader_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end interfaceHeader

    public static class annotationHeader_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start annotationHeader
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1266:1: annotationHeader : modifiers '@' 'interface' IDENTIFIER ;
    public final annotationHeader_return annotationHeader() throws RecognitionException {
        annotationHeader_return retval = new annotationHeader_return();
        retval.start = input.LT(1);
        int annotationHeader_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal602=null;
        Token string_literal603=null;
        Token IDENTIFIER604=null;
        modifiers_return modifiers601 = null;


        Object char_literal602_tree=null;
        Object string_literal603_tree=null;
        Object IDENTIFIER604_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 106) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1267:5: ( modifiers '@' 'interface' IDENTIFIER )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1267:9: modifiers '@' 'interface' IDENTIFIER
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_annotationHeader7962);
            modifiers601=modifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, modifiers601.getTree());
            char_literal602=(Token)input.LT(1);
            match(input,MONKEYS_AT,FOLLOW_MONKEYS_AT_in_annotationHeader7964); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal602_tree = (Object)adaptor.create(char_literal602);
            adaptor.addChild(root_0, char_literal602_tree);
            }
            string_literal603=(Token)input.LT(1);
            match(input,INTERFACE,FOLLOW_INTERFACE_in_annotationHeader7966); if (failed) return retval;
            if ( backtracking==0 ) {
            string_literal603_tree = (Object)adaptor.create(string_literal603);
            adaptor.addChild(root_0, string_literal603_tree);
            }
            IDENTIFIER604=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_annotationHeader7968); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER604_tree = (Object)adaptor.create(IDENTIFIER604);
            adaptor.addChild(root_0, IDENTIFIER604_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 106, annotationHeader_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end annotationHeader

    public static class typeHeader_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start typeHeader
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1270:1: typeHeader : modifiers ( 'class' | 'enum' | ( ( '@' )? 'interface' ) ) IDENTIFIER ;
    public final typeHeader_return typeHeader() throws RecognitionException {
        typeHeader_return retval = new typeHeader_return();
        retval.start = input.LT(1);
        int typeHeader_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal606=null;
        Token string_literal607=null;
        Token char_literal608=null;
        Token string_literal609=null;
        Token IDENTIFIER610=null;
        modifiers_return modifiers605 = null;


        Object string_literal606_tree=null;
        Object string_literal607_tree=null;
        Object char_literal608_tree=null;
        Object string_literal609_tree=null;
        Object IDENTIFIER610_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 107) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1271:5: ( modifiers ( 'class' | 'enum' | ( ( '@' )? 'interface' ) ) IDENTIFIER )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1271:9: modifiers ( 'class' | 'enum' | ( ( '@' )? 'interface' ) ) IDENTIFIER
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_typeHeader7988);
            modifiers605=modifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, modifiers605.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1271:19: ( 'class' | 'enum' | ( ( '@' )? 'interface' ) )
            int alt161=3;
            switch ( input.LA(1) ) {
            case CLASS:
                {
                alt161=1;
                }
                break;
            case ENUM:
                {
                alt161=2;
                }
                break;
            case INTERFACE:
            case MONKEYS_AT:
                {
                alt161=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1271:19: ( 'class' | 'enum' | ( ( '@' )? 'interface' ) )", 161, 0, input);

                throw nvae;
            }

            switch (alt161) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1271:20: 'class'
                    {
                    string_literal606=(Token)input.LT(1);
                    match(input,CLASS,FOLLOW_CLASS_in_typeHeader7991); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal606_tree = (Object)adaptor.create(string_literal606);
                    adaptor.addChild(root_0, string_literal606_tree);
                    }

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1271:28: 'enum'
                    {
                    string_literal607=(Token)input.LT(1);
                    match(input,ENUM,FOLLOW_ENUM_in_typeHeader7993); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal607_tree = (Object)adaptor.create(string_literal607);
                    adaptor.addChild(root_0, string_literal607_tree);
                    }

                    }
                    break;
                case 3 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1271:35: ( ( '@' )? 'interface' )
                    {
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1271:35: ( ( '@' )? 'interface' )
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1271:36: ( '@' )? 'interface'
                    {
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1271:36: ( '@' )?
                    int alt160=2;
                    int LA160_0 = input.LA(1);

                    if ( (LA160_0==MONKEYS_AT) ) {
                        alt160=1;
                    }
                    switch (alt160) {
                        case 1 :
                            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:0:0: '@'
                            {
                            char_literal608=(Token)input.LT(1);
                            match(input,MONKEYS_AT,FOLLOW_MONKEYS_AT_in_typeHeader7996); if (failed) return retval;
                            if ( backtracking==0 ) {
                            char_literal608_tree = (Object)adaptor.create(char_literal608);
                            adaptor.addChild(root_0, char_literal608_tree);
                            }

                            }
                            break;

                    }

                    string_literal609=(Token)input.LT(1);
                    match(input,INTERFACE,FOLLOW_INTERFACE_in_typeHeader8000); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal609_tree = (Object)adaptor.create(string_literal609);
                    adaptor.addChild(root_0, string_literal609_tree);
                    }

                    }


                    }
                    break;

            }

            IDENTIFIER610=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_typeHeader8004); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER610_tree = (Object)adaptor.create(IDENTIFIER610);
            adaptor.addChild(root_0, IDENTIFIER610_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 107, typeHeader_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end typeHeader

    public static class methodHeader_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start methodHeader
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1274:1: methodHeader : modifiers ( typeParameters )? ( type | 'void' )? IDENTIFIER '(' ;
    public final methodHeader_return methodHeader() throws RecognitionException {
        methodHeader_return retval = new methodHeader_return();
        retval.start = input.LT(1);
        int methodHeader_StartIndex = input.index();
        Object root_0 = null;

        Token string_literal614=null;
        Token IDENTIFIER615=null;
        Token char_literal616=null;
        modifiers_return modifiers611 = null;

        typeParameters_return typeParameters612 = null;

        type_return type613 = null;


        Object string_literal614_tree=null;
        Object IDENTIFIER615_tree=null;
        Object char_literal616_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 108) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1275:5: ( modifiers ( typeParameters )? ( type | 'void' )? IDENTIFIER '(' )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1275:9: modifiers ( typeParameters )? ( type | 'void' )? IDENTIFIER '('
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_methodHeader8024);
            modifiers611=modifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, modifiers611.getTree());
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1275:19: ( typeParameters )?
            int alt162=2;
            int LA162_0 = input.LA(1);

            if ( (LA162_0==LT) ) {
                alt162=1;
            }
            switch (alt162) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:0:0: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_methodHeader8026);
                    typeParameters612=typeParameters();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, typeParameters612.getTree());

                    }
                    break;

            }

            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1275:35: ( type | 'void' )?
            int alt163=3;
            switch ( input.LA(1) ) {
                case IDENTIFIER:
                    {
                    int LA163_1 = input.LA(2);

                    if ( (LA163_1==IDENTIFIER||LA163_1==LBRACKET||LA163_1==DOT||LA163_1==LT) ) {
                        alt163=1;
                    }
                    }
                    break;
                case BOOLEAN:
                case BYTE:
                case CHAR:
                case DOUBLE:
                case FLOAT:
                case INT:
                case LONG:
                case SHORT:
                    {
                    alt163=1;
                    }
                    break;
                case VOID:
                    {
                    alt163=2;
                    }
                    break;
            }

            switch (alt163) {
                case 1 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1275:36: type
                    {
                    pushFollow(FOLLOW_type_in_methodHeader8030);
                    type613=type();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, type613.getTree());

                    }
                    break;
                case 2 :
                    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1275:41: 'void'
                    {
                    string_literal614=(Token)input.LT(1);
                    match(input,VOID,FOLLOW_VOID_in_methodHeader8032); if (failed) return retval;
                    if ( backtracking==0 ) {
                    string_literal614_tree = (Object)adaptor.create(string_literal614);
                    adaptor.addChild(root_0, string_literal614_tree);
                    }

                    }
                    break;

            }

            IDENTIFIER615=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodHeader8036); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER615_tree = (Object)adaptor.create(IDENTIFIER615);
            adaptor.addChild(root_0, IDENTIFIER615_tree);
            }
            char_literal616=(Token)input.LT(1);
            match(input,LPAREN,FOLLOW_LPAREN_in_methodHeader8038); if (failed) return retval;
            if ( backtracking==0 ) {
            char_literal616_tree = (Object)adaptor.create(char_literal616);
            adaptor.addChild(root_0, char_literal616_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 108, methodHeader_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end methodHeader

    public static class fieldHeader_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start fieldHeader
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1278:1: fieldHeader : modifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' ) ;
    public final fieldHeader_return fieldHeader() throws RecognitionException {
        fieldHeader_return retval = new fieldHeader_return();
        retval.start = input.LT(1);
        int fieldHeader_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER619=null;
        Token char_literal620=null;
        Token char_literal621=null;
        Token set622=null;
        modifiers_return modifiers617 = null;

        type_return type618 = null;


        Object IDENTIFIER619_tree=null;
        Object char_literal620_tree=null;
        Object char_literal621_tree=null;
        Object set622_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 109) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1279:5: ( modifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' ) )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1279:9: modifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_modifiers_in_fieldHeader8058);
            modifiers617=modifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, modifiers617.getTree());
            pushFollow(FOLLOW_type_in_fieldHeader8060);
            type618=type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, type618.getTree());
            IDENTIFIER619=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_fieldHeader8062); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER619_tree = (Object)adaptor.create(IDENTIFIER619);
            adaptor.addChild(root_0, IDENTIFIER619_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1279:35: ( '[' ']' )*
            loop164:
            do {
                int alt164=2;
                int LA164_0 = input.LA(1);

                if ( (LA164_0==LBRACKET) ) {
                    alt164=1;
                }


                switch (alt164) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1279:36: '[' ']'
            	    {
            	    char_literal620=(Token)input.LT(1);
            	    match(input,LBRACKET,FOLLOW_LBRACKET_in_fieldHeader8065); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal620_tree = (Object)adaptor.create(char_literal620);
            	    adaptor.addChild(root_0, char_literal620_tree);
            	    }
            	    char_literal621=(Token)input.LT(1);
            	    match(input,RBRACKET,FOLLOW_RBRACKET_in_fieldHeader8066); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal621_tree = (Object)adaptor.create(char_literal621);
            	    adaptor.addChild(root_0, char_literal621_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop164;
                }
            } while (true);

            set622=(Token)input.LT(1);
            if ( (input.LA(1)>=SEMI && input.LA(1)<=COMMA)||input.LA(1)==EQ ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set622));
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fieldHeader8070);    throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 109, fieldHeader_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end fieldHeader

    public static class localVariableHeader_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start localVariableHeader
    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1282:1: localVariableHeader : variableModifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' ) ;
    public final localVariableHeader_return localVariableHeader() throws RecognitionException {
        localVariableHeader_return retval = new localVariableHeader_return();
        retval.start = input.LT(1);
        int localVariableHeader_StartIndex = input.index();
        Object root_0 = null;

        Token IDENTIFIER625=null;
        Token char_literal626=null;
        Token char_literal627=null;
        Token set628=null;
        variableModifiers_return variableModifiers623 = null;

        type_return type624 = null;


        Object IDENTIFIER625_tree=null;
        Object char_literal626_tree=null;
        Object char_literal627_tree=null;
        Object set628_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 110) ) { return retval; }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1283:5: ( variableModifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' ) )
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1283:9: variableModifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variableModifiers_in_localVariableHeader8096);
            variableModifiers623=variableModifiers();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, variableModifiers623.getTree());
            pushFollow(FOLLOW_type_in_localVariableHeader8098);
            type624=type();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, type624.getTree());
            IDENTIFIER625=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_localVariableHeader8100); if (failed) return retval;
            if ( backtracking==0 ) {
            IDENTIFIER625_tree = (Object)adaptor.create(IDENTIFIER625);
            adaptor.addChild(root_0, IDENTIFIER625_tree);
            }
            // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1283:43: ( '[' ']' )*
            loop165:
            do {
                int alt165=2;
                int LA165_0 = input.LA(1);

                if ( (LA165_0==LBRACKET) ) {
                    alt165=1;
                }


                switch (alt165) {
            	case 1 :
            	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1283:44: '[' ']'
            	    {
            	    char_literal626=(Token)input.LT(1);
            	    match(input,LBRACKET,FOLLOW_LBRACKET_in_localVariableHeader8103); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal626_tree = (Object)adaptor.create(char_literal626);
            	    adaptor.addChild(root_0, char_literal626_tree);
            	    }
            	    char_literal627=(Token)input.LT(1);
            	    match(input,RBRACKET,FOLLOW_RBRACKET_in_localVariableHeader8104); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	    char_literal627_tree = (Object)adaptor.create(char_literal627);
            	    adaptor.addChild(root_0, char_literal627_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop165;
                }
            } while (true);

            set628=(Token)input.LT(1);
            if ( (input.LA(1)>=SEMI && input.LA(1)<=COMMA)||input.LA(1)==EQ ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set628));
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_localVariableHeader8108);    throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 110, localVariableHeader_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end localVariableHeader

    // $ANTLR start synpred2
    public final void synpred2_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:304:13: ( ( annotations )? packageDeclaration )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:304:13: ( annotations )? packageDeclaration
        {
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:304:13: ( annotations )?
        int alt166=2;
        int LA166_0 = input.LA(1);

        if ( (LA166_0==MONKEYS_AT) ) {
            alt166=1;
        }
        switch (alt166) {
            case 1 :
                // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:304:14: annotations
                {
                pushFollow(FOLLOW_annotations_in_synpred2106);
                annotations();
                _fsp--;
                if (failed) return ;

                }
                break;

        }

        pushFollow(FOLLOW_packageDeclaration_in_synpred2135);
        packageDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred2

    // $ANTLR start synpred12
    public final void synpred12_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:348:10: ( classDeclaration )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:348:10: classDeclaration
        {
        pushFollow(FOLLOW_classDeclaration_in_synpred12493);
        classDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred12

    // $ANTLR start synpred27
    public final void synpred27_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:379:9: ( normalClassDeclaration )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:379:9: normalClassDeclaration
        {
        pushFollow(FOLLOW_normalClassDeclaration_in_synpred27730);
        normalClassDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred27

    // $ANTLR start synpred43
    public final void synpred43_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:467:9: ( normalInterfaceDeclaration )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:467:9: normalInterfaceDeclaration
        {
        pushFollow(FOLLOW_normalInterfaceDeclaration_in_synpred431409);
        normalInterfaceDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred43

    // $ANTLR start synpred52
    public final void synpred52_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:509:10: ( fieldDeclaration )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:509:10: fieldDeclaration
        {
        pushFollow(FOLLOW_fieldDeclaration_in_synpred521739);
        fieldDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred52

    // $ANTLR start synpred53
    public final void synpred53_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:510:10: ( methodDeclaration )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:510:10: methodDeclaration
        {
        pushFollow(FOLLOW_methodDeclaration_in_synpred531750);
        methodDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred53

    // $ANTLR start synpred54
    public final void synpred54_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:511:10: ( classDeclaration )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:511:10: classDeclaration
        {
        pushFollow(FOLLOW_classDeclaration_in_synpred541761);
        classDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred54

    // $ANTLR start synpred57
    public final void synpred57_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:527:10: ( explicitConstructorInvocation )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:527:10: explicitConstructorInvocation
        {
        pushFollow(FOLLOW_explicitConstructorInvocation_in_synpred571898);
        explicitConstructorInvocation();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred57

    // $ANTLR start synpred59
    public final void synpred59_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:519:10: ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:519:10: modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}'
        {
        pushFollow(FOLLOW_modifiers_in_synpred591810);
        modifiers();
        _fsp--;
        if (failed) return ;
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:520:9: ( typeParameters )?
        int alt169=2;
        int LA169_0 = input.LA(1);

        if ( (LA169_0==LT) ) {
            alt169=1;
        }
        switch (alt169) {
            case 1 :
                // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:520:10: typeParameters
                {
                pushFollow(FOLLOW_typeParameters_in_synpred591821);
                typeParameters();
                _fsp--;
                if (failed) return ;

                }
                break;

        }

        match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred591842); if (failed) return ;
        pushFollow(FOLLOW_formalParameters_in_synpred591852);
        formalParameters();
        _fsp--;
        if (failed) return ;
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:524:9: ( 'throws' qualifiedNameList )?
        int alt170=2;
        int LA170_0 = input.LA(1);

        if ( (LA170_0==THROWS) ) {
            alt170=1;
        }
        switch (alt170) {
            case 1 :
                // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:524:10: 'throws' qualifiedNameList
                {
                match(input,THROWS,FOLLOW_THROWS_in_synpred591863); if (failed) return ;
                pushFollow(FOLLOW_qualifiedNameList_in_synpred591865);
                qualifiedNameList();
                _fsp--;
                if (failed) return ;

                }
                break;

        }

        match(input,LBRACE,FOLLOW_LBRACE_in_synpred591886); if (failed) return ;
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:527:9: ( explicitConstructorInvocation )?
        int alt171=2;
        switch ( input.LA(1) ) {
            case LT:
                {
                alt171=1;
                }
                break;
            case THIS:
                {
                int LA171_2 = input.LA(2);

                if ( (synpred57()) ) {
                    alt171=1;
                }
                }
                break;
            case LPAREN:
                {
                int LA171_3 = input.LA(2);

                if ( (synpred57()) ) {
                    alt171=1;
                }
                }
                break;
            case SUPER:
                {
                int LA171_4 = input.LA(2);

                if ( (synpred57()) ) {
                    alt171=1;
                }
                }
                break;
            case IDENTIFIER:
                {
                int LA171_5 = input.LA(2);

                if ( (synpred57()) ) {
                    alt171=1;
                }
                }
                break;
            case INTLITERAL:
            case LONGLITERAL:
            case FLOATLITERAL:
            case DOUBLELITERAL:
            case CHARLITERAL:
            case STRINGLITERAL:
            case TRUE:
            case FALSE:
            case NULL:
                {
                int LA171_6 = input.LA(2);

                if ( (synpred57()) ) {
                    alt171=1;
                }
                }
                break;
            case NEW:
                {
                int LA171_7 = input.LA(2);

                if ( (synpred57()) ) {
                    alt171=1;
                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                {
                int LA171_8 = input.LA(2);

                if ( (synpred57()) ) {
                    alt171=1;
                }
                }
                break;
            case VOID:
                {
                int LA171_9 = input.LA(2);

                if ( (synpred57()) ) {
                    alt171=1;
                }
                }
                break;
        }

        switch (alt171) {
            case 1 :
                // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:527:10: explicitConstructorInvocation
                {
                pushFollow(FOLLOW_explicitConstructorInvocation_in_synpred591898);
                explicitConstructorInvocation();
                _fsp--;
                if (failed) return ;

                }
                break;

        }

        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:529:9: ( blockStatement )*
        loop172:
        do {
            int alt172=2;
            int LA172_0 = input.LA(1);

            if ( ((LA172_0>=IDENTIFIER && LA172_0<=NULL)||(LA172_0>=ABSTRACT && LA172_0<=BYTE)||(LA172_0>=CHAR && LA172_0<=CLASS)||LA172_0==CONTINUE||(LA172_0>=DO && LA172_0<=DOUBLE)||LA172_0==ENUM||LA172_0==FINAL||(LA172_0>=FLOAT && LA172_0<=FOR)||LA172_0==IF||(LA172_0>=INT && LA172_0<=NEW)||(LA172_0>=PRIVATE && LA172_0<=THROW)||(LA172_0>=TRANSIENT && LA172_0<=LPAREN)||LA172_0==LBRACE||LA172_0==SEMI||(LA172_0>=BANG && LA172_0<=TILDE)||(LA172_0>=PLUSPLUS && LA172_0<=SUB)||LA172_0==MONKEYS_AT) ) {
                alt172=1;
            }


            switch (alt172) {
        	case 1 :
        	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:529:10: blockStatement
        	    {
        	    pushFollow(FOLLOW_blockStatement_in_synpred591920);
        	    blockStatement();
        	    _fsp--;
        	    if (failed) return ;

        	    }
        	    break;

        	default :
        	    break loop172;
            }
        } while (true);

        match(input,RBRACE,FOLLOW_RBRACE_in_synpred591941); if (failed) return ;

        }
    }
    // $ANTLR end synpred59

    // $ANTLR start synpred68
    public final void synpred68_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:573:9: ( interfaceFieldDeclaration )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:573:9: interfaceFieldDeclaration
        {
        pushFollow(FOLLOW_interfaceFieldDeclaration_in_synpred682316);
        interfaceFieldDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred68

    // $ANTLR start synpred69
    public final void synpred69_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:574:9: ( interfaceMethodDeclaration )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:574:9: interfaceMethodDeclaration
        {
        pushFollow(FOLLOW_interfaceMethodDeclaration_in_synpred692326);
        interfaceMethodDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred69

    // $ANTLR start synpred70
    public final void synpred70_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:575:9: ( interfaceDeclaration )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:575:9: interfaceDeclaration
        {
        pushFollow(FOLLOW_interfaceDeclaration_in_synpred702336);
        interfaceDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred70

    // $ANTLR start synpred71
    public final void synpred71_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:576:9: ( classDeclaration )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:576:9: classDeclaration
        {
        pushFollow(FOLLOW_classDeclaration_in_synpred712346);
        classDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred71

    // $ANTLR start synpred96
    public final void synpred96_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:671:9: ( ellipsisParameterDecl )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:671:9: ellipsisParameterDecl
        {
        pushFollow(FOLLOW_ellipsisParameterDecl_in_synpred963110);
        ellipsisParameterDecl();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred96

    // $ANTLR start synpred98
    public final void synpred98_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:672:9: ( normalParameterDecl ( ',' normalParameterDecl )* )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:672:9: normalParameterDecl ( ',' normalParameterDecl )*
        {
        pushFollow(FOLLOW_normalParameterDecl_in_synpred983120);
        normalParameterDecl();
        _fsp--;
        if (failed) return ;
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:673:9: ( ',' normalParameterDecl )*
        loop175:
        do {
            int alt175=2;
            int LA175_0 = input.LA(1);

            if ( (LA175_0==COMMA) ) {
                alt175=1;
            }


            switch (alt175) {
        	case 1 :
        	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:673:10: ',' normalParameterDecl
        	    {
        	    match(input,COMMA,FOLLOW_COMMA_in_synpred983131); if (failed) return ;
        	    pushFollow(FOLLOW_normalParameterDecl_in_synpred983133);
        	    normalParameterDecl();
        	    _fsp--;
        	    if (failed) return ;

        	    }
        	    break;

        	default :
        	    break loop175;
            }
        } while (true);


        }
    }
    // $ANTLR end synpred98

    // $ANTLR start synpred99
    public final void synpred99_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:675:10: ( normalParameterDecl ',' )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:675:10: normalParameterDecl ','
        {
        pushFollow(FOLLOW_normalParameterDecl_in_synpred993155);
        normalParameterDecl();
        _fsp--;
        if (failed) return ;
        match(input,COMMA,FOLLOW_COMMA_in_synpred993165); if (failed) return ;

        }
    }
    // $ANTLR end synpred99

    // $ANTLR start synpred103
    public final void synpred103_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:695:9: ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:695:9: ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';'
        {
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:695:9: ( nonWildcardTypeArguments )?
        int alt176=2;
        int LA176_0 = input.LA(1);

        if ( (LA176_0==LT) ) {
            alt176=1;
        }
        switch (alt176) {
            case 1 :
                // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:695:10: nonWildcardTypeArguments
                {
                pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred1033300);
                nonWildcardTypeArguments();
                _fsp--;
                if (failed) return ;

                }
                break;

        }

        if ( input.LA(1)==SUPER||input.LA(1)==THIS ) {
            input.consume();
            errorRecovery=false;failed=false;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            MismatchedSetException mse =
                new MismatchedSetException(null,input);
            recoverFromMismatchedSet(input,mse,FOLLOW_set_in_synpred1033326);    throw mse;
        }

        pushFollow(FOLLOW_arguments_in_synpred1033358);
        arguments();
        _fsp--;
        if (failed) return ;
        match(input,SEMI,FOLLOW_SEMI_in_synpred1033360); if (failed) return ;

        }
    }
    // $ANTLR end synpred103

    // $ANTLR start synpred117
    public final void synpred117_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:782:9: ( annotationMethodDeclaration )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:782:9: annotationMethodDeclaration
        {
        pushFollow(FOLLOW_annotationMethodDeclaration_in_synpred1173959);
        annotationMethodDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred117

    // $ANTLR start synpred118
    public final void synpred118_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:783:9: ( interfaceFieldDeclaration )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:783:9: interfaceFieldDeclaration
        {
        pushFollow(FOLLOW_interfaceFieldDeclaration_in_synpred1183969);
        interfaceFieldDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred118

    // $ANTLR start synpred119
    public final void synpred119_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:784:9: ( normalClassDeclaration )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:784:9: normalClassDeclaration
        {
        pushFollow(FOLLOW_normalClassDeclaration_in_synpred1193979);
        normalClassDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred119

    // $ANTLR start synpred120
    public final void synpred120_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:785:9: ( normalInterfaceDeclaration )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:785:9: normalInterfaceDeclaration
        {
        pushFollow(FOLLOW_normalInterfaceDeclaration_in_synpred1203989);
        normalInterfaceDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred120

    // $ANTLR start synpred121
    public final void synpred121_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:786:9: ( enumDeclaration )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:786:9: enumDeclaration
        {
        pushFollow(FOLLOW_enumDeclaration_in_synpred1213999);
        enumDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred121

    // $ANTLR start synpred122
    public final void synpred122_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:787:9: ( annotationTypeDeclaration )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:787:9: annotationTypeDeclaration
        {
        pushFollow(FOLLOW_annotationTypeDeclaration_in_synpred1224009);
        annotationTypeDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred122

    // $ANTLR start synpred125
    public final void synpred125_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:830:9: ( localVariableDeclarationStatement )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:830:9: localVariableDeclarationStatement
        {
        pushFollow(FOLLOW_localVariableDeclarationStatement_in_synpred1254167);
        localVariableDeclarationStatement();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred125

    // $ANTLR start synpred126
    public final void synpred126_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:831:9: ( classOrInterfaceDeclaration )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:831:9: classOrInterfaceDeclaration
        {
        pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred1264177);
        classOrInterfaceDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred126

    // $ANTLR start synpred130
    public final void synpred130_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:851:9: ( ( 'assert' ) expression ( ':' expression )? ';' )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:851:9: ( 'assert' ) expression ( ':' expression )? ';'
        {
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:851:9: ( 'assert' )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:851:10: 'assert'
        {
        match(input,ASSERT,FOLLOW_ASSERT_in_synpred1304318); if (failed) return ;

        }

        pushFollow(FOLLOW_expression_in_synpred1304338);
        expression();
        _fsp--;
        if (failed) return ;
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:853:20: ( ':' expression )?
        int alt179=2;
        int LA179_0 = input.LA(1);

        if ( (LA179_0==COLON) ) {
            alt179=1;
        }
        switch (alt179) {
            case 1 :
                // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:853:21: ':' expression
                {
                match(input,COLON,FOLLOW_COLON_in_synpred1304341); if (failed) return ;
                pushFollow(FOLLOW_expression_in_synpred1304343);
                expression();
                _fsp--;
                if (failed) return ;

                }
                break;

        }

        match(input,SEMI,FOLLOW_SEMI_in_synpred1304347); if (failed) return ;

        }
    }
    // $ANTLR end synpred130

    // $ANTLR start synpred132
    public final void synpred132_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:854:9: ( 'assert' expression ( ':' expression )? ';' )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:854:9: 'assert' expression ( ':' expression )? ';'
        {
        match(input,ASSERT,FOLLOW_ASSERT_in_synpred1324357); if (failed) return ;
        pushFollow(FOLLOW_expression_in_synpred1324360);
        expression();
        _fsp--;
        if (failed) return ;
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:854:30: ( ':' expression )?
        int alt180=2;
        int LA180_0 = input.LA(1);

        if ( (LA180_0==COLON) ) {
            alt180=1;
        }
        switch (alt180) {
            case 1 :
                // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:854:31: ':' expression
                {
                match(input,COLON,FOLLOW_COLON_in_synpred1324363); if (failed) return ;
                pushFollow(FOLLOW_expression_in_synpred1324365);
                expression();
                _fsp--;
                if (failed) return ;

                }
                break;

        }

        match(input,SEMI,FOLLOW_SEMI_in_synpred1324369); if (failed) return ;

        }
    }
    // $ANTLR end synpred132

    // $ANTLR start synpred133
    public final void synpred133_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:855:39: ( 'else' statement )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:855:39: 'else' statement
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred1334398); if (failed) return ;
        pushFollow(FOLLOW_statement_in_synpred1334400);
        statement();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred133

    // $ANTLR start synpred148
    public final void synpred148_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:870:9: ( expression ';' )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:870:9: expression ';'
        {
        pushFollow(FOLLOW_expression_in_synpred1484622);
        expression();
        _fsp--;
        if (failed) return ;
        match(input,SEMI,FOLLOW_SEMI_in_synpred1484625); if (failed) return ;

        }
    }
    // $ANTLR end synpred148

    // $ANTLR start synpred149
    public final void synpred149_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:871:9: ( IDENTIFIER ':' statement )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:871:9: IDENTIFIER ':' statement
        {
        match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred1494640); if (failed) return ;
        match(input,COLON,FOLLOW_COLON_in_synpred1494642); if (failed) return ;
        pushFollow(FOLLOW_statement_in_synpred1494644);
        statement();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred149

    // $ANTLR start synpred153
    public final void synpred153_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:895:13: ( catches 'finally' block )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:895:13: catches 'finally' block
        {
        pushFollow(FOLLOW_catches_in_synpred1534800);
        catches();
        _fsp--;
        if (failed) return ;
        match(input,FINALLY,FOLLOW_FINALLY_in_synpred1534802); if (failed) return ;
        pushFollow(FOLLOW_block_in_synpred1534804);
        block();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred153

    // $ANTLR start synpred154
    public final void synpred154_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:896:13: ( catches )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:896:13: catches
        {
        pushFollow(FOLLOW_catches_in_synpred1544818);
        catches();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred154

    // $ANTLR start synpred157
    public final void synpred157_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:921:9: ( 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:921:9: 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement
        {
        match(input,FOR,FOLLOW_FOR_in_synpred1575010); if (failed) return ;
        match(input,LPAREN,FOLLOW_LPAREN_in_synpred1575012); if (failed) return ;
        pushFollow(FOLLOW_variableModifiers_in_synpred1575014);
        variableModifiers();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_type_in_synpred1575016);
        type();
        _fsp--;
        if (failed) return ;
        match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred1575018); if (failed) return ;
        match(input,COLON,FOLLOW_COLON_in_synpred1575020); if (failed) return ;
        pushFollow(FOLLOW_expression_in_synpred1575031);
        expression();
        _fsp--;
        if (failed) return ;
        match(input,RPAREN,FOLLOW_RPAREN_in_synpred1575033); if (failed) return ;
        pushFollow(FOLLOW_statement_in_synpred1575035);
        statement();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred157

    // $ANTLR start synpred161
    public final void synpred161_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:935:9: ( localVariableDeclaration )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:935:9: localVariableDeclaration
        {
        pushFollow(FOLLOW_localVariableDeclaration_in_synpred1615214);
        localVariableDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred161

    // $ANTLR start synpred202
    public final void synpred202_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1089:9: ( castExpression )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1089:9: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred2026459);
        castExpression();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred202

    // $ANTLR start synpred206
    public final void synpred206_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1099:9: ( '(' primitiveType ')' unaryExpression )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1099:9: '(' primitiveType ')' unaryExpression
        {
        match(input,LPAREN,FOLLOW_LPAREN_in_synpred2066550); if (failed) return ;
        pushFollow(FOLLOW_primitiveType_in_synpred2066552);
        primitiveType();
        _fsp--;
        if (failed) return ;
        match(input,RPAREN,FOLLOW_RPAREN_in_synpred2066554); if (failed) return ;
        pushFollow(FOLLOW_unaryExpression_in_synpred2066556);
        unaryExpression();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred206

    // $ANTLR start synpred208
    public final void synpred208_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1109:10: ( '.' IDENTIFIER )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1109:10: '.' IDENTIFIER
        {
        match(input,DOT,FOLLOW_DOT_in_synpred2086627); if (failed) return ;
        match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred2086629); if (failed) return ;

        }
    }
    // $ANTLR end synpred208

    // $ANTLR start synpred209
    public final void synpred209_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1111:10: ( identifierSuffix )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1111:10: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred2096651);
        identifierSuffix();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred209

    // $ANTLR start synpred211
    public final void synpred211_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1114:10: ( '.' IDENTIFIER )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1114:10: '.' IDENTIFIER
        {
        match(input,DOT,FOLLOW_DOT_in_synpred2116683); if (failed) return ;
        match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred2116685); if (failed) return ;

        }
    }
    // $ANTLR end synpred211

    // $ANTLR start synpred212
    public final void synpred212_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1116:10: ( identifierSuffix )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1116:10: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred2126707);
        identifierSuffix();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred212

    // $ANTLR start synpred224
    public final void synpred224_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1144:10: ( '[' expression ']' )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1144:10: '[' expression ']'
        {
        match(input,LBRACKET,FOLLOW_LBRACKET_in_synpred2246958); if (failed) return ;
        pushFollow(FOLLOW_expression_in_synpred2246960);
        expression();
        _fsp--;
        if (failed) return ;
        match(input,RBRACKET,FOLLOW_RBRACKET_in_synpred2246962); if (failed) return ;

        }
    }
    // $ANTLR end synpred224

    // $ANTLR start synpred237
    public final void synpred237_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1168:9: ( 'new' classOrInterfaceType classCreatorRest )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1168:9: 'new' classOrInterfaceType classCreatorRest
        {
        match(input,NEW,FOLLOW_NEW_in_synpred2377187); if (failed) return ;
        pushFollow(FOLLOW_classOrInterfaceType_in_synpred2377189);
        classOrInterfaceType();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_classCreatorRest_in_synpred2377191);
        classCreatorRest();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred237

    // $ANTLR start synpred239
    public final void synpred239_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1173:9: ( 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1173:9: 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer
        {
        match(input,NEW,FOLLOW_NEW_in_synpred2397221); if (failed) return ;
        pushFollow(FOLLOW_createdName_in_synpred2397223);
        createdName();
        _fsp--;
        if (failed) return ;
        match(input,LBRACKET,FOLLOW_LBRACKET_in_synpred2397233); if (failed) return ;
        match(input,RBRACKET,FOLLOW_RBRACKET_in_synpred2397235); if (failed) return ;
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1175:9: ( '[' ']' )*
        loop193:
        do {
            int alt193=2;
            int LA193_0 = input.LA(1);

            if ( (LA193_0==LBRACKET) ) {
                alt193=1;
            }


            switch (alt193) {
        	case 1 :
        	    // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1175:10: '[' ']'
        	    {
        	    match(input,LBRACKET,FOLLOW_LBRACKET_in_synpred2397246); if (failed) return ;
        	    match(input,RBRACKET,FOLLOW_RBRACKET_in_synpred2397248); if (failed) return ;

        	    }
        	    break;

        	default :
        	    break loop193;
            }
        } while (true);

        pushFollow(FOLLOW_arrayInitializer_in_synpred2397269);
        arrayInitializer();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred239

    // $ANTLR start synpred240
    public final void synpred240_fragment() throws RecognitionException {   
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1182:13: ( '[' expression ']' )
        // /home/jar/Pulpit/studia/JavaGeniueCode/grammar/Java.g:1182:13: '[' expression ']'
        {
        match(input,LBRACKET,FOLLOW_LBRACKET_in_synpred2407318); if (failed) return ;
        pushFollow(FOLLOW_expression_in_synpred2407320);
        expression();
        _fsp--;
        if (failed) return ;
        match(input,RBRACKET,FOLLOW_RBRACKET_in_synpred2407334); if (failed) return ;

        }
    }
    // $ANTLR end synpred240

    public final boolean synpred126() {
        backtracking++;
        int start = input.mark();
        try {
            synpred126_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred69() {
        backtracking++;
        int start = input.mark();
        try {
            synpred69_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred43() {
        backtracking++;
        int start = input.mark();
        try {
            synpred43_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred148() {
        backtracking++;
        int start = input.mark();
        try {
            synpred148_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred122() {
        backtracking++;
        int start = input.mark();
        try {
            synpred122_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred149() {
        backtracking++;
        int start = input.mark();
        try {
            synpred149_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred68() {
        backtracking++;
        int start = input.mark();
        try {
            synpred68_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred125() {
        backtracking++;
        int start = input.mark();
        try {
            synpred125_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred103() {
        backtracking++;
        int start = input.mark();
        try {
            synpred103_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred120() {
        backtracking++;
        int start = input.mark();
        try {
            synpred120_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred121() {
        backtracking++;
        int start = input.mark();
        try {
            synpred121_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred212() {
        backtracking++;
        int start = input.mark();
        try {
            synpred212_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred211() {
        backtracking++;
        int start = input.mark();
        try {
            synpred211_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred161() {
        backtracking++;
        int start = input.mark();
        try {
            synpred161_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred239() {
        backtracking++;
        int start = input.mark();
        try {
            synpred239_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred27() {
        backtracking++;
        int start = input.mark();
        try {
            synpred27_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred237() {
        backtracking++;
        int start = input.mark();
        try {
            synpred237_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred70() {
        backtracking++;
        int start = input.mark();
        try {
            synpred70_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred96() {
        backtracking++;
        int start = input.mark();
        try {
            synpred96_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred117() {
        backtracking++;
        int start = input.mark();
        try {
            synpred117_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred71() {
        backtracking++;
        int start = input.mark();
        try {
            synpred71_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred118() {
        backtracking++;
        int start = input.mark();
        try {
            synpred118_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred133() {
        backtracking++;
        int start = input.mark();
        try {
            synpred133_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred59() {
        backtracking++;
        int start = input.mark();
        try {
            synpred59_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred57() {
        backtracking++;
        int start = input.mark();
        try {
            synpred57_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred54() {
        backtracking++;
        int start = input.mark();
        try {
            synpred54_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred53() {
        backtracking++;
        int start = input.mark();
        try {
            synpred53_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred52() {
        backtracking++;
        int start = input.mark();
        try {
            synpred52_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred208() {
        backtracking++;
        int start = input.mark();
        try {
            synpred208_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred132() {
        backtracking++;
        int start = input.mark();
        try {
            synpred132_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred2() {
        backtracking++;
        int start = input.mark();
        try {
            synpred2_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred98() {
        backtracking++;
        int start = input.mark();
        try {
            synpred98_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred209() {
        backtracking++;
        int start = input.mark();
        try {
            synpred209_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred130() {
        backtracking++;
        int start = input.mark();
        try {
            synpred130_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred99() {
        backtracking++;
        int start = input.mark();
        try {
            synpred99_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred240() {
        backtracking++;
        int start = input.mark();
        try {
            synpred240_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred154() {
        backtracking++;
        int start = input.mark();
        try {
            synpred154_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred224() {
        backtracking++;
        int start = input.mark();
        try {
            synpred224_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred206() {
        backtracking++;
        int start = input.mark();
        try {
            synpred206_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred153() {
        backtracking++;
        int start = input.mark();
        try {
            synpred153_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred202() {
        backtracking++;
        int start = input.mark();
        try {
            synpred202_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred12() {
        backtracking++;
        int start = input.mark();
        try {
            synpred12_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred157() {
        backtracking++;
        int start = input.mark();
        try {
            synpred157_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred119() {
        backtracking++;
        int start = input.mark();
        try {
            synpred119_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }


    protected DFA146 dfa146 = new DFA146(this);
    protected DFA150 dfa150 = new DFA150(this);
    static final String DFA146_eotS =
        "\112\uffff";
    static final String DFA146_eofS =
        "\112\uffff";
    static final String DFA146_minS =
        "\1\70\1\4\1\114\2\uffff\2\4\1\uffff\1\0\1\120\1\52\1\114\1\121\1"+
        "\4\1\114\2\4\1\120\1\0\1\120\1\52\1\0\3\120\1\52\1\121\1\4\1\121"+
        "\1\0\1\4\1\121\1\4\1\114\1\121\1\4\1\120\1\0\2\120\1\0\3\120\1\52"+
        "\3\120\1\121\1\0\1\4\2\121\1\4\1\0\1\4\2\121\1\120\1\0\4\120\1\0"+
        "\2\120\1\0\1\4\2\121\1\0\2\120";
    static final String DFA146_maxS =
        "\1\70\2\163\2\uffff\1\131\1\4\1\uffff\1\0\2\162\1\163\1\121\1\131"+
        "\1\124\1\76\1\131\1\162\1\0\2\162\1\0\1\162\1\163\2\162\1\121\1"+
        "\76\1\121\1\0\1\4\1\121\1\131\1\124\1\121\1\76\1\162\1\0\2\162\1"+
        "\0\1\162\1\163\3\162\1\163\1\162\1\121\1\0\1\4\2\121\1\76\1\0\1"+
        "\4\2\121\1\162\1\0\2\162\1\163\1\162\1\0\2\162\1\0\1\4\2\121\1\0"+
        "\2\162";
    static final String DFA146_acceptS =
        "\3\uffff\1\3\1\1\2\uffff\1\2\102\uffff";
    static final String DFA146_specialS =
        "\10\uffff\1\13\11\uffff\1\12\2\uffff\1\10\7\uffff\1\1\7\uffff\1"+
        "\11\2\uffff\1\4\10\uffff\1\6\4\uffff\1\0\4\uffff\1\3\4\uffff\1\5"+
        "\2\uffff\1\7\3\uffff\1\2\2\uffff}>";
    static final String[] DFA146_transitionS = {
            "\1\1",
            "\1\2\27\uffff\1\3\1\uffff\1\3\2\uffff\1\3\5\uffff\1\3\5\uffff"+
            "\1\3\6\uffff\1\3\1\uffff\1\3\7\uffff\1\3\64\uffff\1\4",
            "\1\7\3\uffff\1\3\3\uffff\1\6\36\uffff\1\5",
            "",
            "",
            "\1\10\27\uffff\1\11\1\uffff\1\11\2\uffff\1\11\5\uffff\1\11\5"+
            "\uffff\1\11\6\uffff\1\11\1\uffff\1\11\7\uffff\1\11\32\uffff"+
            "\1\12",
            "\1\13",
            "",
            "\1\uffff",
            "\1\14\2\uffff\1\15\36\uffff\1\16",
            "\1\17\26\uffff\1\17\21\uffff\1\15\36\uffff\1\16",
            "\1\7\3\uffff\1\3\3\uffff\1\6\36\uffff\1\20",
            "\1\21",
            "\1\22\27\uffff\1\23\1\uffff\1\23\2\uffff\1\23\5\uffff\1\23\5"+
            "\uffff\1\23\6\uffff\1\23\1\uffff\1\23\7\uffff\1\23\32\uffff"+
            "\1\24",
            "\1\7\3\uffff\1\3\3\uffff\1\6",
            "\1\25\27\uffff\1\26\1\uffff\1\26\2\uffff\1\26\5\uffff\1\26\5"+
            "\uffff\1\26\6\uffff\1\26\1\uffff\1\26\7\uffff\1\26",
            "\1\27\27\uffff\1\30\1\uffff\1\30\2\uffff\1\30\5\uffff\1\30\5"+
            "\uffff\1\30\6\uffff\1\30\1\uffff\1\30\7\uffff\1\30\32\uffff"+
            "\1\31",
            "\1\14\2\uffff\1\15\36\uffff\1\16",
            "\1\uffff",
            "\1\32\2\uffff\1\15\36\uffff\1\16",
            "\1\33\26\uffff\1\33\21\uffff\1\15\36\uffff\1\16",
            "\1\uffff",
            "\1\34\2\uffff\1\15\36\uffff\1\16",
            "\1\37\2\uffff\1\40\1\36\35\uffff\1\41\1\35",
            "\1\42\2\uffff\1\40\36\uffff\1\41",
            "\1\43\26\uffff\1\43\21\uffff\1\40\36\uffff\1\41",
            "\1\44",
            "\1\45\27\uffff\1\46\1\uffff\1\46\2\uffff\1\46\5\uffff\1\46\5"+
            "\uffff\1\46\6\uffff\1\46\1\uffff\1\46\7\uffff\1\46",
            "\1\47",
            "\1\uffff",
            "\1\50",
            "\1\51",
            "\1\52\27\uffff\1\53\1\uffff\1\53\2\uffff\1\53\5\uffff\1\53\5"+
            "\uffff\1\53\6\uffff\1\53\1\uffff\1\53\7\uffff\1\53\32\uffff"+
            "\1\54",
            "\1\7\3\uffff\1\3\3\uffff\1\6",
            "\1\55",
            "\1\56\27\uffff\1\57\1\uffff\1\57\2\uffff\1\57\5\uffff\1\57\5"+
            "\uffff\1\57\6\uffff\1\57\1\uffff\1\57\7\uffff\1\57",
            "\1\32\2\uffff\1\15\36\uffff\1\16",
            "\1\uffff",
            "\1\60\2\uffff\1\15\36\uffff\1\16",
            "\1\34\2\uffff\1\15\36\uffff\1\16",
            "\1\uffff",
            "\1\37\2\uffff\1\40\36\uffff\1\41",
            "\1\63\2\uffff\1\40\1\62\35\uffff\1\41\1\61",
            "\1\64\2\uffff\1\40\36\uffff\1\41",
            "\1\65\26\uffff\1\65\21\uffff\1\40\36\uffff\1\41",
            "\1\42\2\uffff\1\40\36\uffff\1\41",
            "\1\70\2\uffff\1\40\1\67\35\uffff\1\41\1\66",
            "\1\71\2\uffff\1\40\36\uffff\1\41",
            "\1\72",
            "\1\uffff",
            "\1\73",
            "\1\74",
            "\1\75",
            "\1\76\27\uffff\1\77\1\uffff\1\77\2\uffff\1\77\5\uffff\1\77\5"+
            "\uffff\1\77\6\uffff\1\77\1\uffff\1\77\7\uffff\1\77",
            "\1\uffff",
            "\1\100",
            "\1\101",
            "\1\102",
            "\1\60\2\uffff\1\15\36\uffff\1\16",
            "\1\uffff",
            "\1\63\2\uffff\1\40\36\uffff\1\41",
            "\1\64\2\uffff\1\40\36\uffff\1\41",
            "\1\105\2\uffff\1\40\1\104\35\uffff\1\41\1\103",
            "\1\106\2\uffff\1\40\36\uffff\1\41",
            "\1\uffff",
            "\1\70\2\uffff\1\40\36\uffff\1\41",
            "\1\71\2\uffff\1\40\36\uffff\1\41",
            "\1\uffff",
            "\1\107",
            "\1\110",
            "\1\111",
            "\1\uffff",
            "\1\105\2\uffff\1\40\36\uffff\1\41",
            "\1\106\2\uffff\1\40\36\uffff\1\41"
    };

    static final short[] DFA146_eot = DFA.unpackEncodedString(DFA146_eotS);
    static final short[] DFA146_eof = DFA.unpackEncodedString(DFA146_eofS);
    static final char[] DFA146_min = DFA.unpackEncodedStringToUnsignedChars(DFA146_minS);
    static final char[] DFA146_max = DFA.unpackEncodedStringToUnsignedChars(DFA146_maxS);
    static final short[] DFA146_accept = DFA.unpackEncodedString(DFA146_acceptS);
    static final short[] DFA146_special = DFA.unpackEncodedString(DFA146_specialS);
    static final short[][] DFA146_transition;

    static {
        int numStates = DFA146_transitionS.length;
        DFA146_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA146_transition[i] = DFA.unpackEncodedString(DFA146_transitionS[i]);
        }
    }

    class DFA146 extends DFA {

        public DFA146(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 146;
            this.eot = DFA146_eot;
            this.eof = DFA146_eof;
            this.min = DFA146_min;
            this.max = DFA146_max;
            this.accept = DFA146_accept;
            this.special = DFA146_special;
            this.transition = DFA146_transition;
        }
        public String getDescription() {
            return "1166:1: creator : ( 'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest | 'new' classOrInterfaceType classCreatorRest | arrayCreator );";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA146_54 = input.LA(1);

                         
                        int index146_54 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred237()) ) {s = 7;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index146_54);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA146_29 = input.LA(1);

                         
                        int index146_29 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred237()) ) {s = 7;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index146_29);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA146_71 = input.LA(1);

                         
                        int index146_71 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred237()) ) {s = 7;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index146_71);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA146_59 = input.LA(1);

                         
                        int index146_59 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred237()) ) {s = 7;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index146_59);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA146_40 = input.LA(1);

                         
                        int index146_40 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred237()) ) {s = 7;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index146_40);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA146_64 = input.LA(1);

                         
                        int index146_64 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred237()) ) {s = 7;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index146_64);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA146_49 = input.LA(1);

                         
                        int index146_49 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred237()) ) {s = 7;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index146_49);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA146_67 = input.LA(1);

                         
                        int index146_67 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred237()) ) {s = 7;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index146_67);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA146_21 = input.LA(1);

                         
                        int index146_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred237()) ) {s = 7;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index146_21);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA146_37 = input.LA(1);

                         
                        int index146_37 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred237()) ) {s = 7;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index146_37);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA146_18 = input.LA(1);

                         
                        int index146_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred237()) ) {s = 7;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index146_18);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA146_8 = input.LA(1);

                         
                        int index146_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred237()) ) {s = 7;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index146_8);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 146, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA150_eotS =
        "\113\uffff";
    static final String DFA150_eofS =
        "\113\uffff";
    static final String DFA150_minS =
        "\1\70\1\4\2\120\3\4\1\0\1\120\1\52\1\120\2\uffff\1\121\1\4\1\120"+
        "\2\4\1\120\1\0\1\120\1\52\1\0\3\120\1\52\1\121\1\4\1\121\1\0\1\4"+
        "\1\121\1\4\1\120\1\121\1\4\1\120\1\0\2\120\1\0\3\120\1\52\3\120"+
        "\1\121\1\0\1\4\2\121\1\4\1\0\1\4\2\121\1\120\1\0\4\120\1\0\2\120"+
        "\1\0\1\4\2\121\1\0\2\120";
    static final String DFA150_maxS =
        "\1\70\1\76\1\163\1\120\1\131\1\4\1\141\1\0\2\162\1\163\2\uffff\1"+
        "\121\1\131\1\124\1\76\1\131\1\162\1\0\2\162\1\0\1\162\1\163\2\162"+
        "\1\121\1\76\1\121\1\0\1\4\1\121\1\131\1\124\1\121\1\76\1\162\1\0"+
        "\2\162\1\0\1\162\1\163\3\162\1\163\1\162\1\121\1\0\1\4\2\121\1\76"+
        "\1\0\1\4\2\121\1\162\1\0\2\162\1\163\1\162\1\0\2\162\1\0\1\4\2\121"+
        "\1\0\2\162";
    static final String DFA150_acceptS =
        "\13\uffff\1\1\1\2\76\uffff";
    static final String DFA150_specialS =
        "\7\uffff\1\13\13\uffff\1\4\2\uffff\1\3\7\uffff\1\1\7\uffff\1\2\2"+
        "\uffff\1\11\10\uffff\1\5\4\uffff\1\0\4\uffff\1\7\4\uffff\1\6\2\uffff"+
        "\1\12\3\uffff\1\10\2\uffff}>";
    static final String[] DFA150_transitionS = {
            "\1\1",
            "\1\2\27\uffff\1\3\1\uffff\1\3\2\uffff\1\3\5\uffff\1\3\5\uffff"+
            "\1\3\6\uffff\1\3\1\uffff\1\3\7\uffff\1\3",
            "\1\6\3\uffff\1\5\36\uffff\1\4",
            "\1\6",
            "\1\7\27\uffff\1\10\1\uffff\1\10\2\uffff\1\10\5\uffff\1\10\5"+
            "\uffff\1\10\6\uffff\1\10\1\uffff\1\10\7\uffff\1\10\32\uffff"+
            "\1\11",
            "\1\12",
            "\12\14\16\uffff\1\14\1\uffff\1\14\2\uffff\1\14\5\uffff\1\14"+
            "\5\uffff\1\14\6\uffff\1\14\1\uffff\1\14\1\uffff\1\14\5\uffff"+
            "\1\14\2\uffff\1\14\2\uffff\1\14\4\uffff\1\14\2\uffff\1\14\4"+
            "\uffff\1\13\5\uffff\2\14\5\uffff\4\14",
            "\1\uffff",
            "\1\15\2\uffff\1\16\36\uffff\1\17",
            "\1\20\26\uffff\1\20\21\uffff\1\16\36\uffff\1\17",
            "\1\6\3\uffff\1\5\36\uffff\1\21",
            "",
            "",
            "\1\22",
            "\1\23\27\uffff\1\24\1\uffff\1\24\2\uffff\1\24\5\uffff\1\24\5"+
            "\uffff\1\24\6\uffff\1\24\1\uffff\1\24\7\uffff\1\24\32\uffff"+
            "\1\25",
            "\1\6\3\uffff\1\5",
            "\1\26\27\uffff\1\27\1\uffff\1\27\2\uffff\1\27\5\uffff\1\27\5"+
            "\uffff\1\27\6\uffff\1\27\1\uffff\1\27\7\uffff\1\27",
            "\1\30\27\uffff\1\31\1\uffff\1\31\2\uffff\1\31\5\uffff\1\31\5"+
            "\uffff\1\31\6\uffff\1\31\1\uffff\1\31\7\uffff\1\31\32\uffff"+
            "\1\32",
            "\1\15\2\uffff\1\16\36\uffff\1\17",
            "\1\uffff",
            "\1\33\2\uffff\1\16\36\uffff\1\17",
            "\1\34\26\uffff\1\34\21\uffff\1\16\36\uffff\1\17",
            "\1\uffff",
            "\1\35\2\uffff\1\16\36\uffff\1\17",
            "\1\40\2\uffff\1\41\1\37\35\uffff\1\42\1\36",
            "\1\43\2\uffff\1\41\36\uffff\1\42",
            "\1\44\26\uffff\1\44\21\uffff\1\41\36\uffff\1\42",
            "\1\45",
            "\1\46\27\uffff\1\47\1\uffff\1\47\2\uffff\1\47\5\uffff\1\47\5"+
            "\uffff\1\47\6\uffff\1\47\1\uffff\1\47\7\uffff\1\47",
            "\1\50",
            "\1\uffff",
            "\1\51",
            "\1\52",
            "\1\53\27\uffff\1\54\1\uffff\1\54\2\uffff\1\54\5\uffff\1\54\5"+
            "\uffff\1\54\6\uffff\1\54\1\uffff\1\54\7\uffff\1\54\32\uffff"+
            "\1\55",
            "\1\6\3\uffff\1\5",
            "\1\56",
            "\1\57\27\uffff\1\60\1\uffff\1\60\2\uffff\1\60\5\uffff\1\60\5"+
            "\uffff\1\60\6\uffff\1\60\1\uffff\1\60\7\uffff\1\60",
            "\1\33\2\uffff\1\16\36\uffff\1\17",
            "\1\uffff",
            "\1\61\2\uffff\1\16\36\uffff\1\17",
            "\1\35\2\uffff\1\16\36\uffff\1\17",
            "\1\uffff",
            "\1\40\2\uffff\1\41\36\uffff\1\42",
            "\1\64\2\uffff\1\41\1\63\35\uffff\1\42\1\62",
            "\1\65\2\uffff\1\41\36\uffff\1\42",
            "\1\66\26\uffff\1\66\21\uffff\1\41\36\uffff\1\42",
            "\1\43\2\uffff\1\41\36\uffff\1\42",
            "\1\71\2\uffff\1\41\1\70\35\uffff\1\42\1\67",
            "\1\72\2\uffff\1\41\36\uffff\1\42",
            "\1\73",
            "\1\uffff",
            "\1\74",
            "\1\75",
            "\1\76",
            "\1\77\27\uffff\1\100\1\uffff\1\100\2\uffff\1\100\5\uffff\1\100"+
            "\5\uffff\1\100\6\uffff\1\100\1\uffff\1\100\7\uffff\1\100",
            "\1\uffff",
            "\1\101",
            "\1\102",
            "\1\103",
            "\1\61\2\uffff\1\16\36\uffff\1\17",
            "\1\uffff",
            "\1\64\2\uffff\1\41\36\uffff\1\42",
            "\1\65\2\uffff\1\41\36\uffff\1\42",
            "\1\106\2\uffff\1\41\1\105\35\uffff\1\42\1\104",
            "\1\107\2\uffff\1\41\36\uffff\1\42",
            "\1\uffff",
            "\1\71\2\uffff\1\41\36\uffff\1\42",
            "\1\72\2\uffff\1\41\36\uffff\1\42",
            "\1\uffff",
            "\1\110",
            "\1\111",
            "\1\112",
            "\1\uffff",
            "\1\106\2\uffff\1\41\36\uffff\1\42",
            "\1\107\2\uffff\1\41\36\uffff\1\42"
    };

    static final short[] DFA150_eot = DFA.unpackEncodedString(DFA150_eotS);
    static final short[] DFA150_eof = DFA.unpackEncodedString(DFA150_eofS);
    static final char[] DFA150_min = DFA.unpackEncodedStringToUnsignedChars(DFA150_minS);
    static final char[] DFA150_max = DFA.unpackEncodedStringToUnsignedChars(DFA150_maxS);
    static final short[] DFA150_accept = DFA.unpackEncodedString(DFA150_acceptS);
    static final short[] DFA150_special = DFA.unpackEncodedString(DFA150_specialS);
    static final short[][] DFA150_transition;

    static {
        int numStates = DFA150_transitionS.length;
        DFA150_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA150_transition[i] = DFA.unpackEncodedString(DFA150_transitionS[i]);
        }
    }

    class DFA150 extends DFA {

        public DFA150(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 150;
            this.eot = DFA150_eot;
            this.eof = DFA150_eof;
            this.min = DFA150_min;
            this.max = DFA150_max;
            this.accept = DFA150_accept;
            this.special = DFA150_special;
            this.transition = DFA150_transition;
        }
        public String getDescription() {
            return "1172:1: arrayCreator : ( 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer | 'new' createdName '[' expression ']' ( '[' expression ']' )* ( '[' ']' )* );";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA150_55 = input.LA(1);

                         
                        int index150_55 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred239()) ) {s = 11;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index150_55);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA150_30 = input.LA(1);

                         
                        int index150_30 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred239()) ) {s = 11;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index150_30);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA150_38 = input.LA(1);

                         
                        int index150_38 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred239()) ) {s = 11;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index150_38);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA150_22 = input.LA(1);

                         
                        int index150_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred239()) ) {s = 11;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index150_22);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA150_19 = input.LA(1);

                         
                        int index150_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred239()) ) {s = 11;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index150_19);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA150_50 = input.LA(1);

                         
                        int index150_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred239()) ) {s = 11;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index150_50);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA150_65 = input.LA(1);

                         
                        int index150_65 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred239()) ) {s = 11;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index150_65);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA150_60 = input.LA(1);

                         
                        int index150_60 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred239()) ) {s = 11;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index150_60);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA150_72 = input.LA(1);

                         
                        int index150_72 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred239()) ) {s = 11;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index150_72);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA150_41 = input.LA(1);

                         
                        int index150_41 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred239()) ) {s = 11;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index150_41);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA150_68 = input.LA(1);

                         
                        int index150_68 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred239()) ) {s = 11;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index150_68);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA150_7 = input.LA(1);

                         
                        int index150_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred239()) ) {s = 11;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index150_7);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 150, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_annotations_in_compilationUnit106 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_packageDeclaration_in_compilationUnit135 = new BitSet(new long[]{0x9C84080404000002L,0x0001000000040489L});
    public static final BitSet FOLLOW_importDeclaration_in_compilationUnit157 = new BitSet(new long[]{0x9C84080404000002L,0x0001000000040489L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit179 = new BitSet(new long[]{0x9C80080404000002L,0x0001000000040489L});
    public static final BitSet FOLLOW_PACKAGE_in_packageDeclaration210 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_packageDeclaration212 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_packageDeclaration222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_importDeclaration243 = new BitSet(new long[]{0x8000000000000010L});
    public static final BitSet FOLLOW_STATIC_in_importDeclaration255 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_importDeclaration276 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_DOT_in_importDeclaration278 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_STAR_in_importDeclaration280 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_importDeclaration290 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_importDeclaration307 = new BitSet(new long[]{0x8000000000000010L});
    public static final BitSet FOLLOW_STATIC_in_importDeclaration319 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_importDeclaration340 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_DOT_in_importDeclaration351 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_importDeclaration353 = new BitSet(new long[]{0x0000000000000000L,0x0000000000140000L});
    public static final BitSet FOLLOW_DOT_in_importDeclaration375 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_STAR_in_importDeclaration377 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_importDeclaration398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedImportName418 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_DOT_in_qualifiedImportName429 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedImportName431 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_typeDeclaration472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_classOrInterfaceDeclaration493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration503 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_modifiers538 = new BitSet(new long[]{0x9C80080004000002L,0x0001000000000489L});
    public static final BitSet FOLLOW_PUBLIC_in_modifiers548 = new BitSet(new long[]{0x9C80080004000002L,0x0001000000000489L});
    public static final BitSet FOLLOW_PROTECTED_in_modifiers558 = new BitSet(new long[]{0x9C80080004000002L,0x0001000000000489L});
    public static final BitSet FOLLOW_PRIVATE_in_modifiers568 = new BitSet(new long[]{0x9C80080004000002L,0x0001000000000489L});
    public static final BitSet FOLLOW_STATIC_in_modifiers578 = new BitSet(new long[]{0x9C80080004000002L,0x0001000000000489L});
    public static final BitSet FOLLOW_ABSTRACT_in_modifiers588 = new BitSet(new long[]{0x9C80080004000002L,0x0001000000000489L});
    public static final BitSet FOLLOW_FINAL_in_modifiers598 = new BitSet(new long[]{0x9C80080004000002L,0x0001000000000489L});
    public static final BitSet FOLLOW_NATIVE_in_modifiers608 = new BitSet(new long[]{0x9C80080004000002L,0x0001000000000489L});
    public static final BitSet FOLLOW_SYNCHRONIZED_in_modifiers618 = new BitSet(new long[]{0x9C80080004000002L,0x0001000000000489L});
    public static final BitSet FOLLOW_TRANSIENT_in_modifiers628 = new BitSet(new long[]{0x9C80080004000002L,0x0001000000000489L});
    public static final BitSet FOLLOW_VOLATILE_in_modifiers638 = new BitSet(new long[]{0x9C80080004000002L,0x0001000000000489L});
    public static final BitSet FOLLOW_STRICTFP_in_modifiers648 = new BitSet(new long[]{0x9C80080004000002L,0x0001000000000489L});
    public static final BitSet FOLLOW_FINAL_in_variableModifiers680 = new BitSet(new long[]{0x0000080000000002L,0x0001000000000000L});
    public static final BitSet FOLLOW_annotation_in_variableModifiers694 = new BitSet(new long[]{0x0000080000000002L,0x0001000000000000L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_classDeclaration730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_classDeclaration740 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_normalClassDeclaration760 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_CLASS_in_normalClassDeclaration763 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_normalClassDeclaration765 = new BitSet(new long[]{0x0002040000000000L,0x0008000000004000L});
    public static final BitSet FOLLOW_typeParameters_in_normalClassDeclaration776 = new BitSet(new long[]{0x0002040000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_EXTENDS_in_normalClassDeclaration798 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_normalClassDeclaration800 = new BitSet(new long[]{0x0002000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_IMPLEMENTS_in_normalClassDeclaration822 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_typeList_in_normalClassDeclaration824 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_classBody_in_normalClassDeclaration857 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_typeParameters878 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters892 = new BitSet(new long[]{0x0000000000000000L,0x0004000000080000L});
    public static final BitSet FOLLOW_COMMA_in_typeParameters907 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters909 = new BitSet(new long[]{0x0000000000000000L,0x0004000000080000L});
    public static final BitSet FOLLOW_GT_in_typeParameters934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_typeParameter954 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_EXTENDS_in_typeParameter965 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_typeBound_in_typeParameter967 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeBound999 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_AMP_in_typeBound1010 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_typeBound1012 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_modifiers_in_enumDeclaration1044 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_ENUM_in_enumDeclaration1056 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_enumDeclaration1077 = new BitSet(new long[]{0x0002000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_IMPLEMENTS_in_enumDeclaration1088 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_typeList_in_enumDeclaration1090 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_enumBody_in_enumDeclaration1111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_enumBody1136 = new BitSet(new long[]{0x0000000000000010L,0x00010000000C8000L});
    public static final BitSet FOLLOW_enumConstants_in_enumBody1147 = new BitSet(new long[]{0x0000000000000000L,0x00000000000C8000L});
    public static final BitSet FOLLOW_COMMA_in_enumBody1169 = new BitSet(new long[]{0x0000000000000000L,0x0000000000048000L});
    public static final BitSet FOLLOW_enumBodyDeclarations_in_enumBody1182 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_RBRACE_in_enumBody1204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants1224 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_COMMA_in_enumConstants1235 = new BitSet(new long[]{0x0000000000000010L,0x0001000000000000L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants1237 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_annotations_in_enumConstant1271 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_enumConstant1292 = new BitSet(new long[]{0x0000000000000002L,0x0000000000005000L});
    public static final BitSet FOLLOW_arguments_in_enumConstant1303 = new BitSet(new long[]{0x0000000000000002L,0x0000000000004000L});
    public static final BitSet FOLLOW_classBody_in_enumConstant1325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_enumBodyDeclarations1366 = new BitSet(new long[]{0xDCD0288254000012L,0x0001000000044489L});
    public static final BitSet FOLLOW_classBodyDeclaration_in_enumBodyDeclarations1378 = new BitSet(new long[]{0xDCD0288254000012L,0x0001000000044489L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration1409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration1419 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_normalInterfaceDeclaration1443 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_INTERFACE_in_normalInterfaceDeclaration1445 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_normalInterfaceDeclaration1447 = new BitSet(new long[]{0x0000040000000000L,0x0008000000004000L});
    public static final BitSet FOLLOW_typeParameters_in_normalInterfaceDeclaration1458 = new BitSet(new long[]{0x0000040000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_EXTENDS_in_normalInterfaceDeclaration1480 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_typeList_in_normalInterfaceDeclaration1482 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_interfaceBody_in_normalInterfaceDeclaration1503 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList1523 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_COMMA_in_typeList1534 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_typeList1536 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_LBRACE_in_classBody1567 = new BitSet(new long[]{0xDCD0288254000010L,0x000100000004C489L});
    public static final BitSet FOLLOW_classBodyDeclaration_in_classBody1579 = new BitSet(new long[]{0xDCD0288254000010L,0x000100000004C489L});
    public static final BitSet FOLLOW_RBRACE_in_classBody1601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_interfaceBody1621 = new BitSet(new long[]{0xDCD0288254000010L,0x0001000000048489L});
    public static final BitSet FOLLOW_interfaceBodyDeclaration_in_interfaceBody1633 = new BitSet(new long[]{0xDCD0288254000010L,0x0001000000048489L});
    public static final BitSet FOLLOW_RBRACE_in_interfaceBody1655 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_classBodyDeclaration1675 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STATIC_in_classBodyDeclaration1686 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_block_in_classBodyDeclaration1708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_memberDecl_in_classBodyDeclaration1718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldDeclaration_in_memberDecl1739 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodDeclaration_in_memberDecl1750 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_memberDecl1761 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_memberDecl1772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_methodDeclaration1810 = new BitSet(new long[]{0x0000000000000010L,0x0008000000000000L});
    public static final BitSet FOLLOW_typeParameters_in_methodDeclaration1821 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodDeclaration1842 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_formalParameters_in_methodDeclaration1852 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004040L});
    public static final BitSet FOLLOW_THROWS_in_methodDeclaration1863 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_methodDeclaration1865 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_LBRACE_in_methodDeclaration1886 = new BitSet(new long[]{0xFDD168D67C003FF0L,0x00090003C184DFBFL});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_methodDeclaration1898 = new BitSet(new long[]{0xFDD168D67C003FF0L,0x00010003C184DFBFL});
    public static final BitSet FOLLOW_blockStatement_in_methodDeclaration1920 = new BitSet(new long[]{0xFDD168D67C003FF0L,0x00010003C184DFBFL});
    public static final BitSet FOLLOW_RBRACE_in_methodDeclaration1941 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_methodDeclaration1951 = new BitSet(new long[]{0x4050208250000010L,0x0008000000000200L});
    public static final BitSet FOLLOW_typeParameters_in_methodDeclaration1962 = new BitSet(new long[]{0x4050208250000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_type_in_methodDeclaration1984 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_VOID_in_methodDeclaration1998 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodDeclaration2018 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_formalParameters_in_methodDeclaration2028 = new BitSet(new long[]{0x0000000000000000L,0x0000000000054040L});
    public static final BitSet FOLLOW_LBRACKET_in_methodDeclaration2039 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_methodDeclaration2041 = new BitSet(new long[]{0x0000000000000000L,0x0000000000054040L});
    public static final BitSet FOLLOW_THROWS_in_methodDeclaration2063 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_methodDeclaration2065 = new BitSet(new long[]{0x0000000000000000L,0x0000000000044000L});
    public static final BitSet FOLLOW_block_in_methodDeclaration2120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_methodDeclaration2134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_fieldDeclaration2166 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_fieldDeclaration2176 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarator_in_fieldDeclaration2186 = new BitSet(new long[]{0x0000000000000000L,0x00000000000C0000L});
    public static final BitSet FOLLOW_COMMA_in_fieldDeclaration2197 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarator_in_fieldDeclaration2199 = new BitSet(new long[]{0x0000000000000000L,0x00000000000C0000L});
    public static final BitSet FOLLOW_SEMI_in_fieldDeclaration2220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_variableDeclarator2240 = new BitSet(new long[]{0x0000000000000002L,0x0000000000410000L});
    public static final BitSet FOLLOW_LBRACKET_in_variableDeclarator2251 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_variableDeclarator2253 = new BitSet(new long[]{0x0000000000000002L,0x0000000000410000L});
    public static final BitSet FOLLOW_EQ_in_variableDeclarator2275 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1805212L});
    public static final BitSet FOLLOW_variableInitializer_in_variableDeclarator2277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceFieldDeclaration_in_interfaceBodyDeclaration2316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodDeclaration_in_interfaceBodyDeclaration2326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_interfaceBodyDeclaration2336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_interfaceBodyDeclaration2346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_interfaceBodyDeclaration2356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_interfaceMethodDeclaration2376 = new BitSet(new long[]{0x4050208250000010L,0x0008000000000200L});
    public static final BitSet FOLLOW_typeParameters_in_interfaceMethodDeclaration2387 = new BitSet(new long[]{0x4050208250000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_type_in_interfaceMethodDeclaration2409 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_VOID_in_interfaceMethodDeclaration2420 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_interfaceMethodDeclaration2440 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_formalParameters_in_interfaceMethodDeclaration2450 = new BitSet(new long[]{0x0000000000000000L,0x0000000000050040L});
    public static final BitSet FOLLOW_LBRACKET_in_interfaceMethodDeclaration2461 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_interfaceMethodDeclaration2463 = new BitSet(new long[]{0x0000000000000000L,0x0000000000050040L});
    public static final BitSet FOLLOW_THROWS_in_interfaceMethodDeclaration2485 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_interfaceMethodDeclaration2487 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_interfaceMethodDeclaration2500 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_interfaceFieldDeclaration2522 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_interfaceFieldDeclaration2524 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarator_in_interfaceFieldDeclaration2526 = new BitSet(new long[]{0x0000000000000000L,0x00000000000C0000L});
    public static final BitSet FOLLOW_COMMA_in_interfaceFieldDeclaration2537 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarator_in_interfaceFieldDeclaration2539 = new BitSet(new long[]{0x0000000000000000L,0x00000000000C0000L});
    public static final BitSet FOLLOW_SEMI_in_interfaceFieldDeclaration2560 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_type2581 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_LBRACKET_in_type2592 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_type2594 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_primitiveType_in_type2615 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_LBRACKET_in_type2626 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_type2628 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_classOrInterfaceType2660 = new BitSet(new long[]{0x0000000000000002L,0x0008000000100000L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType2671 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_DOT_in_classOrInterfaceType2693 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_classOrInterfaceType2695 = new BitSet(new long[]{0x0000000000000002L,0x0008000000100000L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType2710 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_set_in_primitiveType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_typeArguments2847 = new BitSet(new long[]{0x4050208250000010L,0x0000000002000000L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments2849 = new BitSet(new long[]{0x0000000000000000L,0x0004000000080000L});
    public static final BitSet FOLLOW_COMMA_in_typeArguments2860 = new BitSet(new long[]{0x4050208250000010L,0x0000000002000000L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments2862 = new BitSet(new long[]{0x0000000000000000L,0x0004000000080000L});
    public static final BitSet FOLLOW_GT_in_typeArguments2884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument2904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUES_in_typeArgument2914 = new BitSet(new long[]{0x0000040000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_typeArgument2938 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_typeArgument2982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList3013 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_COMMA_in_qualifiedNameList3024 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList3026 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_LPAREN_in_formalParameters3057 = new BitSet(new long[]{0x4050288250000010L,0x0001000000002000L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameters3068 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_RPAREN_in_formalParameters3090 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ellipsisParameterDecl_in_formalParameterDecls3110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalParameterDecl_in_formalParameterDecls3120 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_COMMA_in_formalParameterDecls3131 = new BitSet(new long[]{0x4050288250000010L,0x0001000000000000L});
    public static final BitSet FOLLOW_normalParameterDecl_in_formalParameterDecls3133 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_normalParameterDecl_in_formalParameterDecls3155 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_COMMA_in_formalParameterDecls3165 = new BitSet(new long[]{0x4050288250000010L,0x0001000000000000L});
    public static final BitSet FOLLOW_ellipsisParameterDecl_in_formalParameterDecls3187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_normalParameterDecl3207 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_normalParameterDecl3209 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_normalParameterDecl3211 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_LBRACKET_in_normalParameterDecl3222 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_normalParameterDecl3224 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_variableModifiers_in_ellipsisParameterDecl3255 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_ellipsisParameterDecl3265 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_ELLIPSIS_in_ellipsisParameterDecl3268 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_ellipsisParameterDecl3278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3300 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000012L});
    public static final BitSet FOLLOW_set_in_explicitConstructorInvocation3326 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation3358 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_explicitConstructorInvocation3360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_explicitConstructorInvocation3371 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_DOT_in_explicitConstructorInvocation3381 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3392 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_in_explicitConstructorInvocation3413 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation3423 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_explicitConstructorInvocation3425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedName3445 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_DOT_in_qualifiedName3456 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedName3458 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_annotation_in_annotations3490 = new BitSet(new long[]{0x0000000000000002L,0x0001000000000000L});
    public static final BitSet FOLLOW_MONKEYS_AT_in_annotation3523 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_annotation3525 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_LPAREN_in_annotation3539 = new BitSet(new long[]{0x4150208250003FF0L,0x00010003C1807212L});
    public static final BitSet FOLLOW_elementValuePairs_in_annotation3566 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_elementValue_in_annotation3590 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_RPAREN_in_annotation3626 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs3658 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_COMMA_in_elementValuePairs3669 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs3671 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_elementValuePair3702 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_EQ_in_elementValuePair3704 = new BitSet(new long[]{0x4150208250003FF0L,0x00010003C1805212L});
    public static final BitSet FOLLOW_elementValue_in_elementValuePair3706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_elementValue3726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_elementValue3736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elementValueArrayInitializer_in_elementValue3746 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_elementValueArrayInitializer3766 = new BitSet(new long[]{0x4150208250003FF0L,0x00010003C188D212L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer3777 = new BitSet(new long[]{0x0000000000000000L,0x0000000000088000L});
    public static final BitSet FOLLOW_COMMA_in_elementValueArrayInitializer3792 = new BitSet(new long[]{0x4150208250003FF0L,0x00010003C1805212L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer3794 = new BitSet(new long[]{0x0000000000000000L,0x0000000000088000L});
    public static final BitSet FOLLOW_COMMA_in_elementValueArrayInitializer3823 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_RBRACE_in_elementValueArrayInitializer3827 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_annotationTypeDeclaration3850 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_MONKEYS_AT_in_annotationTypeDeclaration3852 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_INTERFACE_in_annotationTypeDeclaration3862 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_annotationTypeDeclaration3872 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_annotationTypeBody_in_annotationTypeDeclaration3882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_annotationTypeBody3903 = new BitSet(new long[]{0xDCD0288254000010L,0x0001000000048489L});
    public static final BitSet FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody3915 = new BitSet(new long[]{0xDCD0288254000010L,0x0001000000048489L});
    public static final BitSet FOLLOW_RBRACE_in_annotationTypeBody3937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationMethodDeclaration_in_annotationTypeElementDeclaration3959 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceFieldDeclaration_in_annotationTypeElementDeclaration3969 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_annotationTypeElementDeclaration3979 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementDeclaration3989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_annotationTypeElementDeclaration3999 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_annotationTypeElementDeclaration4009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_annotationTypeElementDeclaration4019 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_annotationMethodDeclaration4039 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_annotationMethodDeclaration4041 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_annotationMethodDeclaration4043 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_LPAREN_in_annotationMethodDeclaration4053 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_RPAREN_in_annotationMethodDeclaration4055 = new BitSet(new long[]{0x0000002000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_DEFAULT_in_annotationMethodDeclaration4058 = new BitSet(new long[]{0x4150208250003FF0L,0x00010003C1805212L});
    public static final BitSet FOLLOW_elementValue_in_annotationMethodDeclaration4060 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_annotationMethodDeclaration4089 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_block4113 = new BitSet(new long[]{0xFDD168D67C003FF0L,0x00010003C184DFBFL});
    public static final BitSet FOLLOW_blockStatement_in_block4124 = new BitSet(new long[]{0xFDD168D67C003FF0L,0x00010003C184DFBFL});
    public static final BitSet FOLLOW_RBRACE_in_block4145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclarationStatement_in_blockStatement4167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_blockStatement4177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statement_in_blockStatement4187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement4208 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_localVariableDeclarationStatement4218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_localVariableDeclaration4238 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_localVariableDeclaration4240 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarator_in_localVariableDeclaration4250 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_COMMA_in_localVariableDeclaration4261 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarator_in_localVariableDeclaration4263 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_block_in_statement4294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSERT_in_statement4318 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_statement4338 = new BitSet(new long[]{0x0000000000000000L,0x0000000004040000L});
    public static final BitSet FOLLOW_COLON_in_statement4341 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_statement4343 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_statement4347 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSERT_in_statement4357 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_statement4360 = new BitSet(new long[]{0x0000000000000000L,0x0000000004040000L});
    public static final BitSet FOLLOW_COLON_in_statement4363 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_statement4365 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_statement4369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_statement4391 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_parExpression_in_statement4393 = new BitSet(new long[]{0x615160D278003FF0L,0x00000003C1845B3EL});
    public static final BitSet FOLLOW_statement_in_statement4395 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_ELSE_in_statement4398 = new BitSet(new long[]{0x615160D278003FF0L,0x00000003C1845B3EL});
    public static final BitSet FOLLOW_statement_in_statement4400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forstatement_in_statement4422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_statement4432 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_parExpression_in_statement4434 = new BitSet(new long[]{0x615160D278003FF0L,0x00000003C1845B3EL});
    public static final BitSet FOLLOW_statement_in_statement4436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_statement4446 = new BitSet(new long[]{0x615160D278003FF0L,0x00000003C1845B3EL});
    public static final BitSet FOLLOW_statement_in_statement4448 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_WHILE_in_statement4450 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_parExpression_in_statement4452 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_statement4454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_trystatement_in_statement4464 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SWITCH_in_statement4474 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_parExpression_in_statement4476 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_LBRACE_in_statement4478 = new BitSet(new long[]{0x0000002080000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_switchBlockStatementGroups_in_statement4480 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_RBRACE_in_statement4482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYNCHRONIZED_in_statement4492 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_parExpression_in_statement4494 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_block_in_statement4496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURN_in_statement4506 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1841212L});
    public static final BitSet FOLLOW_expression_in_statement4509 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_statement4514 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THROW_in_statement4524 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_statement4526 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_statement4528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BREAK_in_statement4538 = new BitSet(new long[]{0x0000000000000010L,0x0000000000040000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_statement4553 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_statement4570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONTINUE_in_statement4580 = new BitSet(new long[]{0x0000000000000010L,0x0000000000040000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_statement4595 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_statement4612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_statement4622 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_statement4625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_statement4640 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_COLON_in_statement4642 = new BitSet(new long[]{0x615160D278003FF0L,0x00000003C1845B3EL});
    public static final BitSet FOLLOW_statement_in_statement4644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_statement4654 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups4676 = new BitSet(new long[]{0x0000002080000002L});
    public static final BitSet FOLLOW_switchLabel_in_switchBlockStatementGroup4705 = new BitSet(new long[]{0xFDD168D67C003FF2L,0x00010003C1845FBFL});
    public static final BitSet FOLLOW_blockStatement_in_switchBlockStatementGroup4716 = new BitSet(new long[]{0xFDD168D67C003FF2L,0x00010003C1845FBFL});
    public static final BitSet FOLLOW_CASE_in_switchLabel4747 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_switchLabel4749 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_COLON_in_switchLabel4751 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DEFAULT_in_switchLabel4761 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_COLON_in_switchLabel4763 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_trystatement4784 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_block_in_trystatement4786 = new BitSet(new long[]{0x0000100100000000L});
    public static final BitSet FOLLOW_catches_in_trystatement4800 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_FINALLY_in_trystatement4802 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_block_in_trystatement4804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_trystatement4818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FINALLY_in_trystatement4832 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_block_in_trystatement4834 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catchClause_in_catches4865 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_catchClause_in_catches4876 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_CATCH_in_catchClause4907 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_LPAREN_in_catchClause4909 = new BitSet(new long[]{0x4050288250000010L,0x0001000000000000L});
    public static final BitSet FOLLOW_formalParameter_in_catchClause4911 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_RPAREN_in_catchClause4921 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_block_in_catchClause4923 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_formalParameter4944 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_formalParameter4946 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_formalParameter4948 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_LBRACKET_in_formalParameter4959 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_formalParameter4961 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_FOR_in_forstatement5010 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_LPAREN_in_forstatement5012 = new BitSet(new long[]{0x4050288250000010L,0x0001000000000000L});
    public static final BitSet FOLLOW_variableModifiers_in_forstatement5014 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_forstatement5016 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_forstatement5018 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_COLON_in_forstatement5020 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_forstatement5031 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_RPAREN_in_forstatement5033 = new BitSet(new long[]{0x615160D278003FF0L,0x00000003C1845B3EL});
    public static final BitSet FOLLOW_statement_in_forstatement5035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_forstatement5067 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_LPAREN_in_forstatement5069 = new BitSet(new long[]{0x4150288250003FF0L,0x00010003C1841212L});
    public static final BitSet FOLLOW_forInit_in_forstatement5089 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_forstatement5110 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1841212L});
    public static final BitSet FOLLOW_expression_in_forstatement5130 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_forstatement5151 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1803212L});
    public static final BitSet FOLLOW_expressionList_in_forstatement5171 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_RPAREN_in_forstatement5192 = new BitSet(new long[]{0x615160D278003FF0L,0x00000003C1845B3EL});
    public static final BitSet FOLLOW_statement_in_forstatement5194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_forInit5214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forInit5224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_parExpression5244 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_parExpression5246 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_RPAREN_in_parExpression5248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList5268 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_COMMA_in_expressionList5279 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_expressionList5281 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression5313 = new BitSet(new long[]{0x0000000000000002L,0x000CFF0000400000L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression5324 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_expression5326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQ_in_assignmentOperator5358 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSEQ_in_assignmentOperator5368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUBEQ_in_assignmentOperator5378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAREQ_in_assignmentOperator5388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SLASHEQ_in_assignmentOperator5398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AMPEQ_in_assignmentOperator5408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BAREQ_in_assignmentOperator5418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARETEQ_in_assignmentOperator5428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PERCENTEQ_in_assignmentOperator5438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_assignmentOperator5449 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_LT_in_assignmentOperator5451 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_EQ_in_assignmentOperator5453 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_assignmentOperator5464 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_GT_in_assignmentOperator5466 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_GT_in_assignmentOperator5468 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_EQ_in_assignmentOperator5470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_assignmentOperator5481 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_GT_in_assignmentOperator5483 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_EQ_in_assignmentOperator5485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression5506 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_QUES_in_conditionalExpression5517 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression5519 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_COLON_in_conditionalExpression5521 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalExpression5523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression5554 = new BitSet(new long[]{0x0000000000000002L,0x0000000020000000L});
    public static final BitSet FOLLOW_BARBAR_in_conditionalOrExpression5565 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression5567 = new BitSet(new long[]{0x0000000000000002L,0x0000000020000000L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5598 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
    public static final BitSet FOLLOW_AMPAMP_in_conditionalAndExpression5609 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5611 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5642 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_BAR_in_inclusiveOrExpression5653 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5655 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression5686 = new BitSet(new long[]{0x0000000000000002L,0x0000004000000000L});
    public static final BitSet FOLLOW_CARET_in_exclusiveOrExpression5697 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression5699 = new BitSet(new long[]{0x0000000000000002L,0x0000004000000000L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression5730 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_AMP_in_andExpression5741 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression5743 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression5774 = new BitSet(new long[]{0x0000000000000002L,0x0002000008000000L});
    public static final BitSet FOLLOW_set_in_equalityExpression5801 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression5851 = new BitSet(new long[]{0x0000000000000002L,0x0002000008000000L});
    public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression5882 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_INSTANCEOF_in_instanceOfExpression5893 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression5895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression5926 = new BitSet(new long[]{0x0000000000000002L,0x000C000000000000L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression5937 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression5939 = new BitSet(new long[]{0x0000000000000002L,0x000C000000000000L});
    public static final BitSet FOLLOW_LT_in_relationalOp5971 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_EQ_in_relationalOp5973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_relationalOp5984 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_EQ_in_relationalOp5986 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_relationalOp5996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_relationalOp6006 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression6026 = new BitSet(new long[]{0x0000000000000002L,0x000C000000000000L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression6037 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression6039 = new BitSet(new long[]{0x0000000000000002L,0x000C000000000000L});
    public static final BitSet FOLLOW_LT_in_shiftOp6072 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_LT_in_shiftOp6074 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_shiftOp6085 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_GT_in_shiftOp6087 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_GT_in_shiftOp6089 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_shiftOp6100 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_GT_in_shiftOp6102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression6123 = new BitSet(new long[]{0x0000000000000002L,0x0000000300000000L});
    public static final BitSet FOLLOW_set_in_additiveExpression6150 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression6200 = new BitSet(new long[]{0x0000000000000002L,0x0000000300000000L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression6238 = new BitSet(new long[]{0x0000000000000002L,0x0000008C00000000L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression6265 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression6333 = new BitSet(new long[]{0x0000000000000002L,0x0000008C00000000L});
    public static final BitSet FOLLOW_PLUS_in_unaryExpression6366 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUB_in_unaryExpression6379 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSPLUS_in_unaryExpression6391 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6393 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUBSUB_in_unaryExpression6403 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression6415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unaryExpressionNotPlusMinus6435 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6437 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_unaryExpressionNotPlusMinus6447 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus6459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus6469 = new BitSet(new long[]{0x0000000000000002L,0x00000000C0110000L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus6480 = new BitSet(new long[]{0x0000000000000002L,0x00000000C0110000L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus6501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_castExpression6550 = new BitSet(new long[]{0x4050208250000000L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression6552 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_RPAREN_in_castExpression6554 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression6556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_castExpression6566 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_castExpression6568 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_RPAREN_in_castExpression6570 = new BitSet(new long[]{0x4150208250003FF0L,0x0000000001801212L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression6572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary6594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THIS_in_primary6616 = new BitSet(new long[]{0x0000000000000002L,0x0000000000111000L});
    public static final BitSet FOLLOW_DOT_in_primary6627 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_primary6629 = new BitSet(new long[]{0x0000000000000002L,0x0000000000111000L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary6651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_primary6672 = new BitSet(new long[]{0x0000000000000002L,0x0000000000111000L});
    public static final BitSet FOLLOW_DOT_in_primary6683 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_primary6685 = new BitSet(new long[]{0x0000000000000002L,0x0000000000111000L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary6707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_in_primary6728 = new BitSet(new long[]{0x0000000000000000L,0x0000000000101000L});
    public static final BitSet FOLLOW_superSuffix_in_primary6738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary6748 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_creator_in_primary6758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary6768 = new BitSet(new long[]{0x0000000000000000L,0x0000000000110000L});
    public static final BitSet FOLLOW_LBRACKET_in_primary6779 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_primary6781 = new BitSet(new long[]{0x0000000000000000L,0x0000000000110000L});
    public static final BitSet FOLLOW_DOT_in_primary6802 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_CLASS_in_primary6804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VOID_in_primary6814 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_DOT_in_primary6816 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_CLASS_in_primary6818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix6844 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_superSuffix6854 = new BitSet(new long[]{0x0000000000000010L,0x0008000000000000L});
    public static final BitSet FOLLOW_typeArguments_in_superSuffix6857 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_superSuffix6878 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_arguments_in_superSuffix6889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_identifierSuffix6922 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_identifierSuffix6924 = new BitSet(new long[]{0x0000000000000000L,0x0000000000110000L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix6945 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_CLASS_in_identifierSuffix6947 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_identifierSuffix6958 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix6960 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_identifierSuffix6962 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix6983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix6993 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_CLASS_in_identifierSuffix6995 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix7005 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_identifierSuffix7007 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_identifierSuffix7009 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix7011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix7021 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_THIS_in_identifierSuffix7023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix7033 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_in_identifierSuffix7035 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix7037 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_innerCreator_in_identifierSuffix7047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector7069 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_selector7071 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_arguments_in_selector7082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector7103 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_THIS_in_selector7105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector7115 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_in_selector7117 = new BitSet(new long[]{0x0000000000000000L,0x0000000000101000L});
    public static final BitSet FOLLOW_superSuffix_in_selector7127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_innerCreator_in_selector7137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_selector7147 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_selector7149 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_selector7151 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_creator7171 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator7173 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_creator7175 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator7177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_creator7187 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_creator7189 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator7191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arrayCreator_in_creator7201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_arrayCreator7221 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_createdName_in_arrayCreator7223 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7233 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7235 = new BitSet(new long[]{0x0000000000000000L,0x0000000000014000L});
    public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7246 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7248 = new BitSet(new long[]{0x0000000000000000L,0x0000000000014000L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreator7269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_arrayCreator7280 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_createdName_in_arrayCreator7282 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7292 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_arrayCreator7294 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7304 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7318 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_arrayCreator7320 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7334 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7356 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7358 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer7389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer7399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_arrayInitializer7419 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C188D212L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer7435 = new BitSet(new long[]{0x0000000000000000L,0x0000000000088000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer7454 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1805212L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer7456 = new BitSet(new long[]{0x0000000000000000L,0x0000000000088000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer7506 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_RBRACE_in_arrayInitializer7519 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_createdName7553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName7563 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_innerCreator7584 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_NEW_in_innerCreator7586 = new BitSet(new long[]{0x0000000000000010L,0x0008000000000000L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_innerCreator7597 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_innerCreator7618 = new BitSet(new long[]{0x0000000000000000L,0x0008000000001000L});
    public static final BitSet FOLLOW_typeArguments_in_innerCreator7629 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator7650 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest7671 = new BitSet(new long[]{0x0000000000000002L,0x0000000000004000L});
    public static final BitSet FOLLOW_classBody_in_classCreatorRest7682 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_nonWildcardTypeArguments7714 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments7716 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_GT_in_nonWildcardTypeArguments7726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_arguments7746 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1803212L});
    public static final BitSet FOLLOW_expressionList_in_arguments7749 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_RPAREN_in_arguments7762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_literal0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_classHeader7886 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_CLASS_in_classHeader7888 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_classHeader7890 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_enumHeader7910 = new BitSet(new long[]{0x0000020000000010L});
    public static final BitSet FOLLOW_set_in_enumHeader7912 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_enumHeader7918 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_interfaceHeader7938 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_INTERFACE_in_interfaceHeader7940 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_interfaceHeader7942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_annotationHeader7962 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_MONKEYS_AT_in_annotationHeader7964 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_INTERFACE_in_annotationHeader7966 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_annotationHeader7968 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_typeHeader7988 = new BitSet(new long[]{0x0020020400000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_CLASS_in_typeHeader7991 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ENUM_in_typeHeader7993 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_MONKEYS_AT_in_typeHeader7996 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_INTERFACE_in_typeHeader8000 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_typeHeader8004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_methodHeader8024 = new BitSet(new long[]{0x4050208250000010L,0x0008000000000200L});
    public static final BitSet FOLLOW_typeParameters_in_methodHeader8026 = new BitSet(new long[]{0x4050208250000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_type_in_methodHeader8030 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_VOID_in_methodHeader8032 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodHeader8036 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_LPAREN_in_methodHeader8038 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_fieldHeader8058 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_fieldHeader8060 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_fieldHeader8062 = new BitSet(new long[]{0x0000000000000000L,0x00000000004D0000L});
    public static final BitSet FOLLOW_LBRACKET_in_fieldHeader8065 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_fieldHeader8066 = new BitSet(new long[]{0x0000000000000000L,0x00000000004D0000L});
    public static final BitSet FOLLOW_set_in_fieldHeader8070 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_localVariableHeader8096 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_localVariableHeader8098 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_localVariableHeader8100 = new BitSet(new long[]{0x0000000000000000L,0x00000000004D0000L});
    public static final BitSet FOLLOW_LBRACKET_in_localVariableHeader8103 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_localVariableHeader8104 = new BitSet(new long[]{0x0000000000000000L,0x00000000004D0000L});
    public static final BitSet FOLLOW_set_in_localVariableHeader8108 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotations_in_synpred2106 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_packageDeclaration_in_synpred2135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_synpred12493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_synpred27730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_synpred431409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldDeclaration_in_synpred521739 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodDeclaration_in_synpred531750 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_synpred541761 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_synpred571898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_synpred591810 = new BitSet(new long[]{0x0000000000000010L,0x0008000000000000L});
    public static final BitSet FOLLOW_typeParameters_in_synpred591821 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_synpred591842 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_formalParameters_in_synpred591852 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004040L});
    public static final BitSet FOLLOW_THROWS_in_synpred591863 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_synpred591865 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_LBRACE_in_synpred591886 = new BitSet(new long[]{0xFDD168D67C003FF0L,0x00090003C184DFBFL});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_synpred591898 = new BitSet(new long[]{0xFDD168D67C003FF0L,0x00010003C184DFBFL});
    public static final BitSet FOLLOW_blockStatement_in_synpred591920 = new BitSet(new long[]{0xFDD168D67C003FF0L,0x00010003C184DFBFL});
    public static final BitSet FOLLOW_RBRACE_in_synpred591941 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceFieldDeclaration_in_synpred682316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodDeclaration_in_synpred692326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_synpred702336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_synpred712346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ellipsisParameterDecl_in_synpred963110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalParameterDecl_in_synpred983120 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_COMMA_in_synpred983131 = new BitSet(new long[]{0x4050288250000010L,0x0001000000000000L});
    public static final BitSet FOLLOW_normalParameterDecl_in_synpred983133 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_normalParameterDecl_in_synpred993155 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_COMMA_in_synpred993165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred1033300 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000012L});
    public static final BitSet FOLLOW_set_in_synpred1033326 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_arguments_in_synpred1033358 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_synpred1033360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationMethodDeclaration_in_synpred1173959 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceFieldDeclaration_in_synpred1183969 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_synpred1193979 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_synpred1203989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_synpred1213999 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_synpred1224009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclarationStatement_in_synpred1254167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred1264177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSERT_in_synpred1304318 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_synpred1304338 = new BitSet(new long[]{0x0000000000000000L,0x0000000004040000L});
    public static final BitSet FOLLOW_COLON_in_synpred1304341 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_synpred1304343 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_synpred1304347 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSERT_in_synpred1324357 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_synpred1324360 = new BitSet(new long[]{0x0000000000000000L,0x0000000004040000L});
    public static final BitSet FOLLOW_COLON_in_synpred1324363 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_synpred1324365 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_synpred1324369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred1334398 = new BitSet(new long[]{0x615160D278003FF0L,0x00000003C1845B3EL});
    public static final BitSet FOLLOW_statement_in_synpred1334400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_synpred1484622 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_SEMI_in_synpred1484625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_synpred1494640 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_COLON_in_synpred1494642 = new BitSet(new long[]{0x615160D278003FF0L,0x00000003C1845B3EL});
    public static final BitSet FOLLOW_statement_in_synpred1494644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred1534800 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_FINALLY_in_synpred1534802 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_block_in_synpred1534804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred1544818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_synpred1575010 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_LPAREN_in_synpred1575012 = new BitSet(new long[]{0x4050288250000010L,0x0001000000000000L});
    public static final BitSet FOLLOW_variableModifiers_in_synpred1575014 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_synpred1575016 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_synpred1575018 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_COLON_in_synpred1575020 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_synpred1575031 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_RPAREN_in_synpred1575033 = new BitSet(new long[]{0x615160D278003FF0L,0x00000003C1845B3EL});
    public static final BitSet FOLLOW_statement_in_synpred1575035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_synpred1615214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred2026459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_synpred2066550 = new BitSet(new long[]{0x4050208250000000L});
    public static final BitSet FOLLOW_primitiveType_in_synpred2066552 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_RPAREN_in_synpred2066554 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_unaryExpression_in_synpred2066556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred2086627 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_synpred2086629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred2096651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred2116683 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_synpred2116685 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred2126707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_synpred2246958 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_synpred2246960 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_synpred2246962 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_synpred2377187 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_synpred2377189 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_classCreatorRest_in_synpred2377191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_synpred2397221 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_createdName_in_synpred2397223 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_LBRACKET_in_synpred2397233 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_synpred2397235 = new BitSet(new long[]{0x0000000000000000L,0x0000000000014000L});
    public static final BitSet FOLLOW_LBRACKET_in_synpred2397246 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_synpred2397248 = new BitSet(new long[]{0x0000000000000000L,0x0000000000014000L});
    public static final BitSet FOLLOW_arrayInitializer_in_synpred2397269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_synpred2407318 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C1801212L});
    public static final BitSet FOLLOW_expression_in_synpred2407320 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_synpred2407334 = new BitSet(new long[]{0x0000000000000002L});

}