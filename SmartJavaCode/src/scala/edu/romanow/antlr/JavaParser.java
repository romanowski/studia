// $ANTLR 3.4 grammar/Java.g 2012-09-24 18:23:43

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
@SuppressWarnings({"all", "warnings", "unchecked"})
public class JavaParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ABSTRACT", "AMP", "AMPAMP", "AMPEQ", "ARG_T", "ASSERT", "BANG", "BANGEQ", "BAR", "BARBAR", "BAREQ", "BLOCK_T", "BOOLEAN", "BREAK", "BYTE", "CARET", "CARETEQ", "CASE", "CATCH", "CHAR", "CHARLITERAL", "CLASS", "CLASS_T", "COLON", "COMMA", "COMMENT", "CONST", "CONTINUE", "DEFAULT", "DO", "DOT", "DOUBLE", "DOUBLELITERAL", "DoubleSuffix", "ELLIPSIS", "ELSE", "ENUM", "EQ", "EQEQ", "EXP_T", "EXTENDS", "EscapeSequence", "Exponent", "FALSE", "FINAL", "FINALLY", "FLOAT", "FLOATLITERAL", "FOR", "FUN_ARG_T", "FloatSuffix", "GOTO", "GT", "HexDigit", "HexPrefix", "IDENTIFIER", "IF", "IMPLEMENTS", "IMPORT", "IMPORT_T", "INSTANCEOF", "INT", "INTERFACE", "INTLITERAL", "IdentifierPart", "IdentifierStart", "IntegerNumber", "LBRACE", "LBRACKET", "LINE_COMMENT", "LINE_T", "LONG", "LONGLITERAL", "LPAREN", "LT", "LongSuffix", "METHOD_RET_VAL_T", "METHOD_T", "MONKEYS_AT", "NATIVE", "NEW", "NULL", "NonIntegerNumber", "PACKAGE", "PCK_T", "PERCENT", "PERCENTEQ", "PLUS", "PLUSEQ", "PLUSPLUS", "PRIVATE", "PROTECTED", "PUBLIC", "QUES", "RBRACE", "RBRACKET", "RETURN", "RPAREN", "SEMI", "SHORT", "SLASH", "SLASHEQ", "STAR", "STAREQ", "STATIC", "STRICTFP", "STRINGLITERAL", "SUB", "SUBEQ", "SUBSUB", "SUPER", "SWITCH", "SYNCHRONIZED", "SurrogateIdentifer", "THIS", "THROW", "THROWS", "TILDE", "TRANSIENT", "TRUE", "TRY", "TYPE_NAME_T", "VARIABLE_T", "VAR_NAME_T", "VOID", "VOLATILE", "WHILE", "WS"
    };

    public static final int EOF=-1;
    public static final int ABSTRACT=4;
    public static final int AMP=5;
    public static final int AMPAMP=6;
    public static final int AMPEQ=7;
    public static final int ARG_T=8;
    public static final int ASSERT=9;
    public static final int BANG=10;
    public static final int BANGEQ=11;
    public static final int BAR=12;
    public static final int BARBAR=13;
    public static final int BAREQ=14;
    public static final int BLOCK_T=15;
    public static final int BOOLEAN=16;
    public static final int BREAK=17;
    public static final int BYTE=18;
    public static final int CARET=19;
    public static final int CARETEQ=20;
    public static final int CASE=21;
    public static final int CATCH=22;
    public static final int CHAR=23;
    public static final int CHARLITERAL=24;
    public static final int CLASS=25;
    public static final int CLASS_T=26;
    public static final int COLON=27;
    public static final int COMMA=28;
    public static final int COMMENT=29;
    public static final int CONST=30;
    public static final int CONTINUE=31;
    public static final int DEFAULT=32;
    public static final int DO=33;
    public static final int DOT=34;
    public static final int DOUBLE=35;
    public static final int DOUBLELITERAL=36;
    public static final int DoubleSuffix=37;
    public static final int ELLIPSIS=38;
    public static final int ELSE=39;
    public static final int ENUM=40;
    public static final int EQ=41;
    public static final int EQEQ=42;
    public static final int EXP_T=43;
    public static final int EXTENDS=44;
    public static final int EscapeSequence=45;
    public static final int Exponent=46;
    public static final int FALSE=47;
    public static final int FINAL=48;
    public static final int FINALLY=49;
    public static final int FLOAT=50;
    public static final int FLOATLITERAL=51;
    public static final int FOR=52;
    public static final int FUN_ARG_T=53;
    public static final int FloatSuffix=54;
    public static final int GOTO=55;
    public static final int GT=56;
    public static final int HexDigit=57;
    public static final int HexPrefix=58;
    public static final int IDENTIFIER=59;
    public static final int IF=60;
    public static final int IMPLEMENTS=61;
    public static final int IMPORT=62;
    public static final int IMPORT_T=63;
    public static final int INSTANCEOF=64;
    public static final int INT=65;
    public static final int INTERFACE=66;
    public static final int INTLITERAL=67;
    public static final int IdentifierPart=68;
    public static final int IdentifierStart=69;
    public static final int IntegerNumber=70;
    public static final int LBRACE=71;
    public static final int LBRACKET=72;
    public static final int LINE_COMMENT=73;
    public static final int LINE_T=74;
    public static final int LONG=75;
    public static final int LONGLITERAL=76;
    public static final int LPAREN=77;
    public static final int LT=78;
    public static final int LongSuffix=79;
    public static final int METHOD_RET_VAL_T=80;
    public static final int METHOD_T=81;
    public static final int MONKEYS_AT=82;
    public static final int NATIVE=83;
    public static final int NEW=84;
    public static final int NULL=85;
    public static final int NonIntegerNumber=86;
    public static final int PACKAGE=87;
    public static final int PCK_T=88;
    public static final int PERCENT=89;
    public static final int PERCENTEQ=90;
    public static final int PLUS=91;
    public static final int PLUSEQ=92;
    public static final int PLUSPLUS=93;
    public static final int PRIVATE=94;
    public static final int PROTECTED=95;
    public static final int PUBLIC=96;
    public static final int QUES=97;
    public static final int RBRACE=98;
    public static final int RBRACKET=99;
    public static final int RETURN=100;
    public static final int RPAREN=101;
    public static final int SEMI=102;
    public static final int SHORT=103;
    public static final int SLASH=104;
    public static final int SLASHEQ=105;
    public static final int STAR=106;
    public static final int STAREQ=107;
    public static final int STATIC=108;
    public static final int STRICTFP=109;
    public static final int STRINGLITERAL=110;
    public static final int SUB=111;
    public static final int SUBEQ=112;
    public static final int SUBSUB=113;
    public static final int SUPER=114;
    public static final int SWITCH=115;
    public static final int SYNCHRONIZED=116;
    public static final int SurrogateIdentifer=117;
    public static final int THIS=118;
    public static final int THROW=119;
    public static final int THROWS=120;
    public static final int TILDE=121;
    public static final int TRANSIENT=122;
    public static final int TRUE=123;
    public static final int TRY=124;
    public static final int TYPE_NAME_T=125;
    public static final int VARIABLE_T=126;
    public static final int VAR_NAME_T=127;
    public static final int VOID=128;
    public static final int VOLATILE=129;
    public static final int WHILE=130;
    public static final int WS=131;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public JavaParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public JavaParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
        this.state.ruleMemo = new HashMap[398+1];
         

    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return JavaParser.tokenNames; }
    public String getGrammarFileName() { return "grammar/Java.g"; }


    public static class compilationUnit_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "compilationUnit"
    // grammar/Java.g:306:1: compilationUnit : ( ( annotations )? packageDeclaration )? ( importDeclaration )* ( typeDeclaration )* ;
    public final JavaParser.compilationUnit_return compilationUnit() throws RecognitionException {
        JavaParser.compilationUnit_return retval = new JavaParser.compilationUnit_return();
        retval.start = input.LT(1);

        int compilationUnit_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.annotations_return annotations1 =null;

        JavaParser.packageDeclaration_return packageDeclaration2 =null;

        JavaParser.importDeclaration_return importDeclaration3 =null;

        JavaParser.typeDeclaration_return typeDeclaration4 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 1) ) { return retval; }

            // grammar/Java.g:307:5: ( ( ( annotations )? packageDeclaration )? ( importDeclaration )* ( typeDeclaration )* )
            // grammar/Java.g:307:9: ( ( annotations )? packageDeclaration )? ( importDeclaration )* ( typeDeclaration )*
            {
            root_0 = (CommonTree)adaptor.nil();


            // grammar/Java.g:307:9: ( ( annotations )? packageDeclaration )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==MONKEYS_AT) ) {
                int LA2_1 = input.LA(2);

                if ( (synpred2_Java()) ) {
                    alt2=1;
                }
            }
            else if ( (LA2_0==PACKAGE) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // grammar/Java.g:307:13: ( annotations )? packageDeclaration
                    {
                    // grammar/Java.g:307:13: ( annotations )?
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==MONKEYS_AT) ) {
                        alt1=1;
                    }
                    switch (alt1) {
                        case 1 :
                            // grammar/Java.g:307:14: annotations
                            {
                            pushFollow(FOLLOW_annotations_in_compilationUnit98);
                            annotations1=annotations();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, annotations1.getTree());

                            }
                            break;

                    }


                    pushFollow(FOLLOW_packageDeclaration_in_compilationUnit127);
                    packageDeclaration2=packageDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, packageDeclaration2.getTree());

                    }
                    break;

            }


            // grammar/Java.g:311:9: ( importDeclaration )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==IMPORT) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // grammar/Java.g:311:10: importDeclaration
            	    {
            	    pushFollow(FOLLOW_importDeclaration_in_compilationUnit149);
            	    importDeclaration3=importDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, importDeclaration3.getTree());

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            // grammar/Java.g:313:9: ( typeDeclaration )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==ABSTRACT||LA4_0==BOOLEAN||LA4_0==BYTE||LA4_0==CHAR||LA4_0==CLASS||LA4_0==DOUBLE||LA4_0==ENUM||LA4_0==FINAL||LA4_0==FLOAT||LA4_0==IDENTIFIER||(LA4_0 >= INT && LA4_0 <= INTERFACE)||LA4_0==LONG||LA4_0==LT||(LA4_0 >= MONKEYS_AT && LA4_0 <= NATIVE)||(LA4_0 >= PRIVATE && LA4_0 <= PUBLIC)||(LA4_0 >= SEMI && LA4_0 <= SHORT)||(LA4_0 >= STATIC && LA4_0 <= STRICTFP)||LA4_0==SYNCHRONIZED||LA4_0==TRANSIENT||(LA4_0 >= VOID && LA4_0 <= VOLATILE)) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // grammar/Java.g:313:10: typeDeclaration
            	    {
            	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit171);
            	    typeDeclaration4=typeDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeDeclaration4.getTree());

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 1, compilationUnit_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "compilationUnit"


    public static class packageDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "packageDeclaration"
    // grammar/Java.g:318:1: packageDeclaration : 'package' qualifiedName ';' -> ^( PCK_T 'package' qualifiedName ';' ) ;
    public final JavaParser.packageDeclaration_return packageDeclaration() throws RecognitionException {
        JavaParser.packageDeclaration_return retval = new JavaParser.packageDeclaration_return();
        retval.start = input.LT(1);

        int packageDeclaration_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal5=null;
        Token char_literal7=null;
        JavaParser.qualifiedName_return qualifiedName6 =null;


        CommonTree string_literal5_tree=null;
        CommonTree char_literal7_tree=null;
        RewriteRuleTokenStream stream_PACKAGE=new RewriteRuleTokenStream(adaptor,"token PACKAGE");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleSubtreeStream stream_qualifiedName=new RewriteRuleSubtreeStream(adaptor,"rule qualifiedName");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 2) ) { return retval; }

            // grammar/Java.g:319:5: ( 'package' qualifiedName ';' -> ^( PCK_T 'package' qualifiedName ';' ) )
            // grammar/Java.g:319:9: 'package' qualifiedName ';'
            {
            string_literal5=(Token)match(input,PACKAGE,FOLLOW_PACKAGE_in_packageDeclaration202); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_PACKAGE.add(string_literal5);


            pushFollow(FOLLOW_qualifiedName_in_packageDeclaration204);
            qualifiedName6=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qualifiedName.add(qualifiedName6.getTree());

            char_literal7=(Token)match(input,SEMI,FOLLOW_SEMI_in_packageDeclaration206); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMI.add(char_literal7);


            // AST REWRITE
            // elements: SEMI, PACKAGE, qualifiedName
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 319:37: -> ^( PCK_T 'package' qualifiedName ';' )
            {
                // grammar/Java.g:319:40: ^( PCK_T 'package' qualifiedName ';' )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(PCK_T, "PCK_T")
                , root_1);

                adaptor.addChild(root_1, 
                stream_PACKAGE.nextNode()
                );

                adaptor.addChild(root_1, stream_qualifiedName.nextTree());

                adaptor.addChild(root_1, 
                stream_SEMI.nextNode()
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 2, packageDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "packageDeclaration"


    public static class importDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "importDeclaration"
    // grammar/Java.g:323:1: importDeclaration : importDeclarationInner -> ^( IMPORT_T importDeclarationInner ) ;
    public final JavaParser.importDeclaration_return importDeclaration() throws RecognitionException {
        JavaParser.importDeclaration_return retval = new JavaParser.importDeclaration_return();
        retval.start = input.LT(1);

        int importDeclaration_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.importDeclarationInner_return importDeclarationInner8 =null;


        RewriteRuleSubtreeStream stream_importDeclarationInner=new RewriteRuleSubtreeStream(adaptor,"rule importDeclarationInner");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return retval; }

            // grammar/Java.g:324:2: ( importDeclarationInner -> ^( IMPORT_T importDeclarationInner ) )
            // grammar/Java.g:324:4: importDeclarationInner
            {
            pushFollow(FOLLOW_importDeclarationInner_in_importDeclaration233);
            importDeclarationInner8=importDeclarationInner();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_importDeclarationInner.add(importDeclarationInner8.getTree());

            // AST REWRITE
            // elements: importDeclarationInner
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 324:27: -> ^( IMPORT_T importDeclarationInner )
            {
                // grammar/Java.g:324:30: ^( IMPORT_T importDeclarationInner )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(IMPORT_T, "IMPORT_T")
                , root_1);

                adaptor.addChild(root_1, stream_importDeclarationInner.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 3, importDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "importDeclaration"


    public static class importDeclarationInner_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "importDeclarationInner"
    // grammar/Java.g:329:1: importDeclarationInner : ( 'import' ( 'static' )? IDENTIFIER '.' '*' ';' | 'import' ( 'static' )? IDENTIFIER ( '.' IDENTIFIER )+ ( '.' '*' )? ';' );
    public final JavaParser.importDeclarationInner_return importDeclarationInner() throws RecognitionException {
        JavaParser.importDeclarationInner_return retval = new JavaParser.importDeclarationInner_return();
        retval.start = input.LT(1);

        int importDeclarationInner_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal9=null;
        Token string_literal10=null;
        Token IDENTIFIER11=null;
        Token char_literal12=null;
        Token char_literal13=null;
        Token char_literal14=null;
        Token string_literal15=null;
        Token string_literal16=null;
        Token IDENTIFIER17=null;
        Token char_literal18=null;
        Token IDENTIFIER19=null;
        Token char_literal20=null;
        Token char_literal21=null;
        Token char_literal22=null;

        CommonTree string_literal9_tree=null;
        CommonTree string_literal10_tree=null;
        CommonTree IDENTIFIER11_tree=null;
        CommonTree char_literal12_tree=null;
        CommonTree char_literal13_tree=null;
        CommonTree char_literal14_tree=null;
        CommonTree string_literal15_tree=null;
        CommonTree string_literal16_tree=null;
        CommonTree IDENTIFIER17_tree=null;
        CommonTree char_literal18_tree=null;
        CommonTree IDENTIFIER19_tree=null;
        CommonTree char_literal20_tree=null;
        CommonTree char_literal21_tree=null;
        CommonTree char_literal22_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 4) ) { return retval; }

            // grammar/Java.g:330:5: ( 'import' ( 'static' )? IDENTIFIER '.' '*' ';' | 'import' ( 'static' )? IDENTIFIER ( '.' IDENTIFIER )+ ( '.' '*' )? ';' )
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

                            if ( (LA9_4==STAR) ) {
                                alt9=1;
                            }
                            else if ( (LA9_4==IDENTIFIER) ) {
                                alt9=2;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                NoViableAltException nvae =
                                    new NoViableAltException("", 9, 4, input);

                                throw nvae;

                            }
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 9, 3, input);

                            throw nvae;

                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 2, input);

                        throw nvae;

                    }
                }
                else if ( (LA9_1==IDENTIFIER) ) {
                    int LA9_3 = input.LA(3);

                    if ( (LA9_3==DOT) ) {
                        int LA9_4 = input.LA(4);

                        if ( (LA9_4==STAR) ) {
                            alt9=1;
                        }
                        else if ( (LA9_4==IDENTIFIER) ) {
                            alt9=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 9, 4, input);

                            throw nvae;

                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 3, input);

                        throw nvae;

                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 1, input);

                    throw nvae;

                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;

            }
            switch (alt9) {
                case 1 :
                    // grammar/Java.g:330:9: 'import' ( 'static' )? IDENTIFIER '.' '*' ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal9=(Token)match(input,IMPORT,FOLLOW_IMPORT_in_importDeclarationInner260); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal9_tree = 
                    (CommonTree)adaptor.create(string_literal9)
                    ;
                    adaptor.addChild(root_0, string_literal9_tree);
                    }

                    // grammar/Java.g:331:9: ( 'static' )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==STATIC) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // grammar/Java.g:331:10: 'static'
                            {
                            string_literal10=(Token)match(input,STATIC,FOLLOW_STATIC_in_importDeclarationInner271); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal10_tree = 
                            (CommonTree)adaptor.create(string_literal10)
                            ;
                            adaptor.addChild(root_0, string_literal10_tree);
                            }

                            }
                            break;

                    }


                    IDENTIFIER11=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_importDeclarationInner292); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER11_tree = 
                    (CommonTree)adaptor.create(IDENTIFIER11)
                    ;
                    adaptor.addChild(root_0, IDENTIFIER11_tree);
                    }

                    char_literal12=(Token)match(input,DOT,FOLLOW_DOT_in_importDeclarationInner294); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal12_tree = 
                    (CommonTree)adaptor.create(char_literal12)
                    ;
                    adaptor.addChild(root_0, char_literal12_tree);
                    }

                    char_literal13=(Token)match(input,STAR,FOLLOW_STAR_in_importDeclarationInner296); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal13_tree = 
                    (CommonTree)adaptor.create(char_literal13)
                    ;
                    adaptor.addChild(root_0, char_literal13_tree);
                    }

                    char_literal14=(Token)match(input,SEMI,FOLLOW_SEMI_in_importDeclarationInner306); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal14_tree = 
                    (CommonTree)adaptor.create(char_literal14)
                    ;
                    adaptor.addChild(root_0, char_literal14_tree);
                    }

                    }
                    break;
                case 2 :
                    // grammar/Java.g:335:9: 'import' ( 'static' )? IDENTIFIER ( '.' IDENTIFIER )+ ( '.' '*' )? ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal15=(Token)match(input,IMPORT,FOLLOW_IMPORT_in_importDeclarationInner316); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal15_tree = 
                    (CommonTree)adaptor.create(string_literal15)
                    ;
                    adaptor.addChild(root_0, string_literal15_tree);
                    }

                    // grammar/Java.g:336:9: ( 'static' )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==STATIC) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // grammar/Java.g:336:10: 'static'
                            {
                            string_literal16=(Token)match(input,STATIC,FOLLOW_STATIC_in_importDeclarationInner327); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal16_tree = 
                            (CommonTree)adaptor.create(string_literal16)
                            ;
                            adaptor.addChild(root_0, string_literal16_tree);
                            }

                            }
                            break;

                    }


                    IDENTIFIER17=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_importDeclarationInner348); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER17_tree = 
                    (CommonTree)adaptor.create(IDENTIFIER17)
                    ;
                    adaptor.addChild(root_0, IDENTIFIER17_tree);
                    }

                    // grammar/Java.g:339:9: ( '.' IDENTIFIER )+
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
                    	    // grammar/Java.g:339:10: '.' IDENTIFIER
                    	    {
                    	    char_literal18=(Token)match(input,DOT,FOLLOW_DOT_in_importDeclarationInner359); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal18_tree = 
                    	    (CommonTree)adaptor.create(char_literal18)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal18_tree);
                    	    }

                    	    IDENTIFIER19=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_importDeclarationInner361); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    IDENTIFIER19_tree = 
                    	    (CommonTree)adaptor.create(IDENTIFIER19)
                    	    ;
                    	    adaptor.addChild(root_0, IDENTIFIER19_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt7 >= 1 ) break loop7;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(7, input);
                                throw eee;
                        }
                        cnt7++;
                    } while (true);


                    // grammar/Java.g:341:9: ( '.' '*' )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==DOT) ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // grammar/Java.g:341:10: '.' '*'
                            {
                            char_literal20=(Token)match(input,DOT,FOLLOW_DOT_in_importDeclarationInner383); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal20_tree = 
                            (CommonTree)adaptor.create(char_literal20)
                            ;
                            adaptor.addChild(root_0, char_literal20_tree);
                            }

                            char_literal21=(Token)match(input,STAR,FOLLOW_STAR_in_importDeclarationInner385); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal21_tree = 
                            (CommonTree)adaptor.create(char_literal21)
                            ;
                            adaptor.addChild(root_0, char_literal21_tree);
                            }

                            }
                            break;

                    }


                    char_literal22=(Token)match(input,SEMI,FOLLOW_SEMI_in_importDeclarationInner406); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal22_tree = 
                    (CommonTree)adaptor.create(char_literal22)
                    ;
                    adaptor.addChild(root_0, char_literal22_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 4, importDeclarationInner_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "importDeclarationInner"


    public static class qualifiedImportName_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "qualifiedImportName"
    // grammar/Java.g:347:1: qualifiedImportName : IDENTIFIER ( '.' IDENTIFIER )* ;
    public final JavaParser.qualifiedImportName_return qualifiedImportName() throws RecognitionException {
        JavaParser.qualifiedImportName_return retval = new JavaParser.qualifiedImportName_return();
        retval.start = input.LT(1);

        int qualifiedImportName_StartIndex = input.index();

        CommonTree root_0 = null;

        Token IDENTIFIER23=null;
        Token char_literal24=null;
        Token IDENTIFIER25=null;

        CommonTree IDENTIFIER23_tree=null;
        CommonTree char_literal24_tree=null;
        CommonTree IDENTIFIER25_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return retval; }

            // grammar/Java.g:348:5: ( IDENTIFIER ( '.' IDENTIFIER )* )
            // grammar/Java.g:348:9: IDENTIFIER ( '.' IDENTIFIER )*
            {
            root_0 = (CommonTree)adaptor.nil();


            IDENTIFIER23=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedImportName426); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER23_tree = 
            (CommonTree)adaptor.create(IDENTIFIER23)
            ;
            adaptor.addChild(root_0, IDENTIFIER23_tree);
            }

            // grammar/Java.g:349:9: ( '.' IDENTIFIER )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==DOT) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // grammar/Java.g:349:10: '.' IDENTIFIER
            	    {
            	    char_literal24=(Token)match(input,DOT,FOLLOW_DOT_in_qualifiedImportName437); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal24_tree = 
            	    (CommonTree)adaptor.create(char_literal24)
            	    ;
            	    adaptor.addChild(root_0, char_literal24_tree);
            	    }

            	    IDENTIFIER25=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedImportName439); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    IDENTIFIER25_tree = 
            	    (CommonTree)adaptor.create(IDENTIFIER25)
            	    ;
            	    adaptor.addChild(root_0, IDENTIFIER25_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 5, qualifiedImportName_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "qualifiedImportName"


    public static class typeDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "typeDeclaration"
    // grammar/Java.g:353:1: typeDeclaration : ( classOrInterfaceDeclaration | ';' );
    public final JavaParser.typeDeclaration_return typeDeclaration() throws RecognitionException {
        JavaParser.typeDeclaration_return retval = new JavaParser.typeDeclaration_return();
        retval.start = input.LT(1);

        int typeDeclaration_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal27=null;
        JavaParser.classOrInterfaceDeclaration_return classOrInterfaceDeclaration26 =null;


        CommonTree char_literal27_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return retval; }

            // grammar/Java.g:354:5: ( classOrInterfaceDeclaration | ';' )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==ABSTRACT||LA11_0==BOOLEAN||LA11_0==BYTE||LA11_0==CHAR||LA11_0==CLASS||LA11_0==DOUBLE||LA11_0==ENUM||LA11_0==FINAL||LA11_0==FLOAT||LA11_0==IDENTIFIER||(LA11_0 >= INT && LA11_0 <= INTERFACE)||LA11_0==LONG||LA11_0==LT||(LA11_0 >= MONKEYS_AT && LA11_0 <= NATIVE)||(LA11_0 >= PRIVATE && LA11_0 <= PUBLIC)||LA11_0==SHORT||(LA11_0 >= STATIC && LA11_0 <= STRICTFP)||LA11_0==SYNCHRONIZED||LA11_0==TRANSIENT||(LA11_0 >= VOID && LA11_0 <= VOLATILE)) ) {
                alt11=1;
            }
            else if ( (LA11_0==SEMI) ) {
                alt11=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;

            }
            switch (alt11) {
                case 1 :
                    // grammar/Java.g:354:9: classOrInterfaceDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration469);
                    classOrInterfaceDeclaration26=classOrInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceDeclaration26.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:355:9: ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal27=(Token)match(input,SEMI,FOLLOW_SEMI_in_typeDeclaration479); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal27_tree = 
                    (CommonTree)adaptor.create(char_literal27)
                    ;
                    adaptor.addChild(root_0, char_literal27_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 6, typeDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "typeDeclaration"


    public static class classOrInterfaceDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "classOrInterfaceDeclaration"
    // grammar/Java.g:358:1: classOrInterfaceDeclaration : ( classDeclaration | interfaceDeclaration );
    public final JavaParser.classOrInterfaceDeclaration_return classOrInterfaceDeclaration() throws RecognitionException {
        JavaParser.classOrInterfaceDeclaration_return retval = new JavaParser.classOrInterfaceDeclaration_return();
        retval.start = input.LT(1);

        int classOrInterfaceDeclaration_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.classDeclaration_return classDeclaration28 =null;

        JavaParser.interfaceDeclaration_return interfaceDeclaration29 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return retval; }

            // grammar/Java.g:359:5: ( classDeclaration | interfaceDeclaration )
            int alt12=2;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA12_1 = input.LA(2);

                if ( (synpred12_Java()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 1, input);

                    throw nvae;

                }
                }
                break;
            case PUBLIC:
                {
                int LA12_2 = input.LA(2);

                if ( (synpred12_Java()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 2, input);

                    throw nvae;

                }
                }
                break;
            case PROTECTED:
                {
                int LA12_3 = input.LA(2);

                if ( (synpred12_Java()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 3, input);

                    throw nvae;

                }
                }
                break;
            case PRIVATE:
                {
                int LA12_4 = input.LA(2);

                if ( (synpred12_Java()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 4, input);

                    throw nvae;

                }
                }
                break;
            case STATIC:
                {
                int LA12_5 = input.LA(2);

                if ( (synpred12_Java()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 5, input);

                    throw nvae;

                }
                }
                break;
            case ABSTRACT:
                {
                int LA12_6 = input.LA(2);

                if ( (synpred12_Java()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 6, input);

                    throw nvae;

                }
                }
                break;
            case FINAL:
                {
                int LA12_7 = input.LA(2);

                if ( (synpred12_Java()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 7, input);

                    throw nvae;

                }
                }
                break;
            case NATIVE:
                {
                int LA12_8 = input.LA(2);

                if ( (synpred12_Java()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 8, input);

                    throw nvae;

                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA12_9 = input.LA(2);

                if ( (synpred12_Java()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 9, input);

                    throw nvae;

                }
                }
                break;
            case TRANSIENT:
                {
                int LA12_10 = input.LA(2);

                if ( (synpred12_Java()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 10, input);

                    throw nvae;

                }
                }
                break;
            case VOLATILE:
                {
                int LA12_11 = input.LA(2);

                if ( (synpred12_Java()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 11, input);

                    throw nvae;

                }
                }
                break;
            case STRICTFP:
                {
                int LA12_12 = input.LA(2);

                if ( (synpred12_Java()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 12, input);

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
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;

            }

            switch (alt12) {
                case 1 :
                    // grammar/Java.g:359:10: classDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_classDeclaration_in_classOrInterfaceDeclaration499);
                    classDeclaration28=classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classDeclaration28.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:360:9: interfaceDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration509);
                    interfaceDeclaration29=interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceDeclaration29.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 7, classOrInterfaceDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "classOrInterfaceDeclaration"


    public static class modifiers_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "modifiers"
    // grammar/Java.g:364:1: modifiers : ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )* ;
    public final JavaParser.modifiers_return modifiers() throws RecognitionException {
        JavaParser.modifiers_return retval = new JavaParser.modifiers_return();
        retval.start = input.LT(1);

        int modifiers_StartIndex = input.index();

        CommonTree root_0 = null;

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
        Token string_literal41=null;
        JavaParser.annotation_return annotation30 =null;


        CommonTree string_literal31_tree=null;
        CommonTree string_literal32_tree=null;
        CommonTree string_literal33_tree=null;
        CommonTree string_literal34_tree=null;
        CommonTree string_literal35_tree=null;
        CommonTree string_literal36_tree=null;
        CommonTree string_literal37_tree=null;
        CommonTree string_literal38_tree=null;
        CommonTree string_literal39_tree=null;
        CommonTree string_literal40_tree=null;
        CommonTree string_literal41_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return retval; }

            // grammar/Java.g:365:5: ( ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )* )
            // grammar/Java.g:366:5: ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )*
            {
            root_0 = (CommonTree)adaptor.nil();


            // grammar/Java.g:366:5: ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )*
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
            	    // grammar/Java.g:366:10: annotation
            	    {
            	    pushFollow(FOLLOW_annotation_in_modifiers536);
            	    annotation30=annotation();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation30.getTree());

            	    }
            	    break;
            	case 2 :
            	    // grammar/Java.g:367:9: 'public'
            	    {
            	    string_literal31=(Token)match(input,PUBLIC,FOLLOW_PUBLIC_in_modifiers546); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal31_tree = 
            	    (CommonTree)adaptor.create(string_literal31)
            	    ;
            	    adaptor.addChild(root_0, string_literal31_tree);
            	    }

            	    }
            	    break;
            	case 3 :
            	    // grammar/Java.g:368:9: 'protected'
            	    {
            	    string_literal32=(Token)match(input,PROTECTED,FOLLOW_PROTECTED_in_modifiers556); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal32_tree = 
            	    (CommonTree)adaptor.create(string_literal32)
            	    ;
            	    adaptor.addChild(root_0, string_literal32_tree);
            	    }

            	    }
            	    break;
            	case 4 :
            	    // grammar/Java.g:369:9: 'private'
            	    {
            	    string_literal33=(Token)match(input,PRIVATE,FOLLOW_PRIVATE_in_modifiers566); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal33_tree = 
            	    (CommonTree)adaptor.create(string_literal33)
            	    ;
            	    adaptor.addChild(root_0, string_literal33_tree);
            	    }

            	    }
            	    break;
            	case 5 :
            	    // grammar/Java.g:370:9: 'static'
            	    {
            	    string_literal34=(Token)match(input,STATIC,FOLLOW_STATIC_in_modifiers576); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal34_tree = 
            	    (CommonTree)adaptor.create(string_literal34)
            	    ;
            	    adaptor.addChild(root_0, string_literal34_tree);
            	    }

            	    }
            	    break;
            	case 6 :
            	    // grammar/Java.g:371:9: 'abstract'
            	    {
            	    string_literal35=(Token)match(input,ABSTRACT,FOLLOW_ABSTRACT_in_modifiers586); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal35_tree = 
            	    (CommonTree)adaptor.create(string_literal35)
            	    ;
            	    adaptor.addChild(root_0, string_literal35_tree);
            	    }

            	    }
            	    break;
            	case 7 :
            	    // grammar/Java.g:372:9: 'final'
            	    {
            	    string_literal36=(Token)match(input,FINAL,FOLLOW_FINAL_in_modifiers596); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal36_tree = 
            	    (CommonTree)adaptor.create(string_literal36)
            	    ;
            	    adaptor.addChild(root_0, string_literal36_tree);
            	    }

            	    }
            	    break;
            	case 8 :
            	    // grammar/Java.g:373:9: 'native'
            	    {
            	    string_literal37=(Token)match(input,NATIVE,FOLLOW_NATIVE_in_modifiers606); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal37_tree = 
            	    (CommonTree)adaptor.create(string_literal37)
            	    ;
            	    adaptor.addChild(root_0, string_literal37_tree);
            	    }

            	    }
            	    break;
            	case 9 :
            	    // grammar/Java.g:374:9: 'synchronized'
            	    {
            	    string_literal38=(Token)match(input,SYNCHRONIZED,FOLLOW_SYNCHRONIZED_in_modifiers616); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal38_tree = 
            	    (CommonTree)adaptor.create(string_literal38)
            	    ;
            	    adaptor.addChild(root_0, string_literal38_tree);
            	    }

            	    }
            	    break;
            	case 10 :
            	    // grammar/Java.g:375:9: 'transient'
            	    {
            	    string_literal39=(Token)match(input,TRANSIENT,FOLLOW_TRANSIENT_in_modifiers626); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal39_tree = 
            	    (CommonTree)adaptor.create(string_literal39)
            	    ;
            	    adaptor.addChild(root_0, string_literal39_tree);
            	    }

            	    }
            	    break;
            	case 11 :
            	    // grammar/Java.g:376:9: 'volatile'
            	    {
            	    string_literal40=(Token)match(input,VOLATILE,FOLLOW_VOLATILE_in_modifiers636); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal40_tree = 
            	    (CommonTree)adaptor.create(string_literal40)
            	    ;
            	    adaptor.addChild(root_0, string_literal40_tree);
            	    }

            	    }
            	    break;
            	case 12 :
            	    // grammar/Java.g:377:9: 'strictfp'
            	    {
            	    string_literal41=(Token)match(input,STRICTFP,FOLLOW_STRICTFP_in_modifiers646); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal41_tree = 
            	    (CommonTree)adaptor.create(string_literal41)
            	    ;
            	    adaptor.addChild(root_0, string_literal41_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 8, modifiers_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "modifiers"


    public static class variableModifiers_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "variableModifiers"
    // grammar/Java.g:382:1: variableModifiers : ( 'final' | annotation )* ;
    public final JavaParser.variableModifiers_return variableModifiers() throws RecognitionException {
        JavaParser.variableModifiers_return retval = new JavaParser.variableModifiers_return();
        retval.start = input.LT(1);

        int variableModifiers_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal42=null;
        JavaParser.annotation_return annotation43 =null;


        CommonTree string_literal42_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return retval; }

            // grammar/Java.g:383:5: ( ( 'final' | annotation )* )
            // grammar/Java.g:383:9: ( 'final' | annotation )*
            {
            root_0 = (CommonTree)adaptor.nil();


            // grammar/Java.g:383:9: ( 'final' | annotation )*
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
            	    // grammar/Java.g:383:13: 'final'
            	    {
            	    string_literal42=(Token)match(input,FINAL,FOLLOW_FINAL_in_variableModifiers677); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal42_tree = 
            	    (CommonTree)adaptor.create(string_literal42)
            	    ;
            	    adaptor.addChild(root_0, string_literal42_tree);
            	    }

            	    }
            	    break;
            	case 2 :
            	    // grammar/Java.g:384:13: annotation
            	    {
            	    pushFollow(FOLLOW_annotation_in_variableModifiers691);
            	    annotation43=annotation();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation43.getTree());

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 9, variableModifiers_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "variableModifiers"


    public static class classDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "classDeclaration"
    // grammar/Java.g:389:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );
    public final JavaParser.classDeclaration_return classDeclaration() throws RecognitionException {
        JavaParser.classDeclaration_return retval = new JavaParser.classDeclaration_return();
        retval.start = input.LT(1);

        int classDeclaration_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.normalClassDeclaration_return normalClassDeclaration44 =null;

        JavaParser.enumDeclaration_return enumDeclaration45 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return retval; }

            // grammar/Java.g:390:5: ( normalClassDeclaration | enumDeclaration )
            int alt15=2;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA15_1 = input.LA(2);

                if ( (synpred27_Java()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 1, input);

                    throw nvae;

                }
                }
                break;
            case PUBLIC:
                {
                int LA15_2 = input.LA(2);

                if ( (synpred27_Java()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 2, input);

                    throw nvae;

                }
                }
                break;
            case PROTECTED:
                {
                int LA15_3 = input.LA(2);

                if ( (synpred27_Java()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 3, input);

                    throw nvae;

                }
                }
                break;
            case PRIVATE:
                {
                int LA15_4 = input.LA(2);

                if ( (synpred27_Java()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 4, input);

                    throw nvae;

                }
                }
                break;
            case STATIC:
                {
                int LA15_5 = input.LA(2);

                if ( (synpred27_Java()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 5, input);

                    throw nvae;

                }
                }
                break;
            case ABSTRACT:
                {
                int LA15_6 = input.LA(2);

                if ( (synpred27_Java()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 6, input);

                    throw nvae;

                }
                }
                break;
            case FINAL:
                {
                int LA15_7 = input.LA(2);

                if ( (synpred27_Java()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 7, input);

                    throw nvae;

                }
                }
                break;
            case NATIVE:
                {
                int LA15_8 = input.LA(2);

                if ( (synpred27_Java()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 8, input);

                    throw nvae;

                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA15_9 = input.LA(2);

                if ( (synpred27_Java()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 9, input);

                    throw nvae;

                }
                }
                break;
            case TRANSIENT:
                {
                int LA15_10 = input.LA(2);

                if ( (synpred27_Java()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 10, input);

                    throw nvae;

                }
                }
                break;
            case VOLATILE:
                {
                int LA15_11 = input.LA(2);

                if ( (synpred27_Java()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 11, input);

                    throw nvae;

                }
                }
                break;
            case STRICTFP:
                {
                int LA15_12 = input.LA(2);

                if ( (synpred27_Java()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 12, input);

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
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;

            }

            switch (alt15) {
                case 1 :
                    // grammar/Java.g:390:9: normalClassDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_normalClassDeclaration_in_classDeclaration722);
                    normalClassDeclaration44=normalClassDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalClassDeclaration44.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:391:9: enumDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_enumDeclaration_in_classDeclaration732);
                    enumDeclaration45=enumDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumDeclaration45.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 10, classDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "classDeclaration"


    public static class normalClassDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "normalClassDeclaration"
    // grammar/Java.g:394:1: normalClassDeclaration : normalClassDeclarationInner -> ^( CLASS_T normalClassDeclarationInner ) ;
    public final JavaParser.normalClassDeclaration_return normalClassDeclaration() throws RecognitionException {
        JavaParser.normalClassDeclaration_return retval = new JavaParser.normalClassDeclaration_return();
        retval.start = input.LT(1);

        int normalClassDeclaration_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.normalClassDeclarationInner_return normalClassDeclarationInner46 =null;


        RewriteRuleSubtreeStream stream_normalClassDeclarationInner=new RewriteRuleSubtreeStream(adaptor,"rule normalClassDeclarationInner");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return retval; }

            // grammar/Java.g:395:6: ( normalClassDeclarationInner -> ^( CLASS_T normalClassDeclarationInner ) )
            // grammar/Java.g:395:9: normalClassDeclarationInner
            {
            pushFollow(FOLLOW_normalClassDeclarationInner_in_normalClassDeclaration751);
            normalClassDeclarationInner46=normalClassDeclarationInner();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_normalClassDeclarationInner.add(normalClassDeclarationInner46.getTree());

            // AST REWRITE
            // elements: normalClassDeclarationInner
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 395:37: -> ^( CLASS_T normalClassDeclarationInner )
            {
                // grammar/Java.g:395:40: ^( CLASS_T normalClassDeclarationInner )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(CLASS_T, "CLASS_T")
                , root_1);

                adaptor.addChild(root_1, stream_normalClassDeclarationInner.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 11, normalClassDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "normalClassDeclaration"


    public static class normalClassDeclarationInner_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "normalClassDeclarationInner"
    // grammar/Java.g:398:1: normalClassDeclarationInner : modifiers 'class' IDENTIFIER ( typeParameters )? ( extendPart )? ( implementsPart )? classBody ;
    public final JavaParser.normalClassDeclarationInner_return normalClassDeclarationInner() throws RecognitionException {
        JavaParser.normalClassDeclarationInner_return retval = new JavaParser.normalClassDeclarationInner_return();
        retval.start = input.LT(1);

        int normalClassDeclarationInner_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal48=null;
        Token IDENTIFIER49=null;
        JavaParser.modifiers_return modifiers47 =null;

        JavaParser.typeParameters_return typeParameters50 =null;

        JavaParser.extendPart_return extendPart51 =null;

        JavaParser.implementsPart_return implementsPart52 =null;

        JavaParser.classBody_return classBody53 =null;


        CommonTree string_literal48_tree=null;
        CommonTree IDENTIFIER49_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return retval; }

            // grammar/Java.g:399:5: ( modifiers 'class' IDENTIFIER ( typeParameters )? ( extendPart )? ( implementsPart )? classBody )
            // grammar/Java.g:399:9: modifiers 'class' IDENTIFIER ( typeParameters )? ( extendPart )? ( implementsPart )? classBody
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_normalClassDeclarationInner781);
            modifiers47=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers47.getTree());

            string_literal48=(Token)match(input,CLASS,FOLLOW_CLASS_in_normalClassDeclarationInner784); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal48_tree = 
            (CommonTree)adaptor.create(string_literal48)
            ;
            adaptor.addChild(root_0, string_literal48_tree);
            }

            IDENTIFIER49=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_normalClassDeclarationInner792); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER49_tree = 
            (CommonTree)adaptor.create(IDENTIFIER49)
            ;
            adaptor.addChild(root_0, IDENTIFIER49_tree);
            }

            // grammar/Java.g:401:9: ( typeParameters )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==LT) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // grammar/Java.g:401:10: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalClassDeclarationInner803);
                    typeParameters50=typeParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters50.getTree());

                    }
                    break;

            }


            // grammar/Java.g:403:9: ( extendPart )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==EXTENDS) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // grammar/Java.g:403:10: extendPart
                    {
                    pushFollow(FOLLOW_extendPart_in_normalClassDeclarationInner825);
                    extendPart51=extendPart();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, extendPart51.getTree());

                    }
                    break;

            }


            // grammar/Java.g:405:9: ( implementsPart )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==IMPLEMENTS) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // grammar/Java.g:405:10: implementsPart
                    {
                    pushFollow(FOLLOW_implementsPart_in_normalClassDeclarationInner847);
                    implementsPart52=implementsPart();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, implementsPart52.getTree());

                    }
                    break;

            }


            pushFollow(FOLLOW_classBody_in_normalClassDeclarationInner868);
            classBody53=classBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, classBody53.getTree());

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 12, normalClassDeclarationInner_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "normalClassDeclarationInner"


    public static class extendPart_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "extendPart"
    // grammar/Java.g:410:1: extendPart : 'extends' type -> ^( 'extends' type ) ;
    public final JavaParser.extendPart_return extendPart() throws RecognitionException {
        JavaParser.extendPart_return retval = new JavaParser.extendPart_return();
        retval.start = input.LT(1);

        int extendPart_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal54=null;
        JavaParser.type_return type55 =null;


        CommonTree string_literal54_tree=null;
        RewriteRuleTokenStream stream_EXTENDS=new RewriteRuleTokenStream(adaptor,"token EXTENDS");
        RewriteRuleSubtreeStream stream_type=new RewriteRuleSubtreeStream(adaptor,"rule type");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return retval; }

            // grammar/Java.g:411:5: ( 'extends' type -> ^( 'extends' type ) )
            // grammar/Java.g:411:7: 'extends' type
            {
            string_literal54=(Token)match(input,EXTENDS,FOLLOW_EXTENDS_in_extendPart885); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EXTENDS.add(string_literal54);


            pushFollow(FOLLOW_type_in_extendPart887);
            type55=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_type.add(type55.getTree());

            // AST REWRITE
            // elements: type, EXTENDS
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 411:22: -> ^( 'extends' type )
            {
                // grammar/Java.g:411:25: ^( 'extends' type )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                stream_EXTENDS.nextNode()
                , root_1);

                adaptor.addChild(root_1, stream_type.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 13, extendPart_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "extendPart"


    public static class implementsPart_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "implementsPart"
    // grammar/Java.g:414:1: implementsPart : 'implements' typeList -> ^( 'implements' typeList ) ;
    public final JavaParser.implementsPart_return implementsPart() throws RecognitionException {
        JavaParser.implementsPart_return retval = new JavaParser.implementsPart_return();
        retval.start = input.LT(1);

        int implementsPart_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal56=null;
        JavaParser.typeList_return typeList57 =null;


        CommonTree string_literal56_tree=null;
        RewriteRuleTokenStream stream_IMPLEMENTS=new RewriteRuleTokenStream(adaptor,"token IMPLEMENTS");
        RewriteRuleSubtreeStream stream_typeList=new RewriteRuleSubtreeStream(adaptor,"rule typeList");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return retval; }

            // grammar/Java.g:415:5: ( 'implements' typeList -> ^( 'implements' typeList ) )
            // grammar/Java.g:415:7: 'implements' typeList
            {
            string_literal56=(Token)match(input,IMPLEMENTS,FOLLOW_IMPLEMENTS_in_implementsPart914); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IMPLEMENTS.add(string_literal56);


            pushFollow(FOLLOW_typeList_in_implementsPart916);
            typeList57=typeList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_typeList.add(typeList57.getTree());

            // AST REWRITE
            // elements: IMPLEMENTS, typeList
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 415:30: -> ^( 'implements' typeList )
            {
                // grammar/Java.g:415:33: ^( 'implements' typeList )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                stream_IMPLEMENTS.nextNode()
                , root_1);

                adaptor.addChild(root_1, stream_typeList.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 14, implementsPart_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "implementsPart"


    public static class typeParameters_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "typeParameters"
    // grammar/Java.g:419:1: typeParameters : '<' ^ typeParameter ( ',' typeParameter )* '>' ;
    public final JavaParser.typeParameters_return typeParameters() throws RecognitionException {
        JavaParser.typeParameters_return retval = new JavaParser.typeParameters_return();
        retval.start = input.LT(1);

        int typeParameters_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal58=null;
        Token char_literal60=null;
        Token char_literal62=null;
        JavaParser.typeParameter_return typeParameter59 =null;

        JavaParser.typeParameter_return typeParameter61 =null;


        CommonTree char_literal58_tree=null;
        CommonTree char_literal60_tree=null;
        CommonTree char_literal62_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return retval; }

            // grammar/Java.g:420:5: ( '<' ^ typeParameter ( ',' typeParameter )* '>' )
            // grammar/Java.g:420:9: '<' ^ typeParameter ( ',' typeParameter )* '>'
            {
            root_0 = (CommonTree)adaptor.nil();


            char_literal58=(Token)match(input,LT,FOLLOW_LT_in_typeParameters947); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal58_tree = 
            (CommonTree)adaptor.create(char_literal58)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(char_literal58_tree, root_0);
            }

            pushFollow(FOLLOW_typeParameter_in_typeParameters962);
            typeParameter59=typeParameter();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameter59.getTree());

            // grammar/Java.g:422:13: ( ',' typeParameter )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==COMMA) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // grammar/Java.g:422:14: ',' typeParameter
            	    {
            	    char_literal60=(Token)match(input,COMMA,FOLLOW_COMMA_in_typeParameters977); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal60_tree = 
            	    (CommonTree)adaptor.create(char_literal60)
            	    ;
            	    adaptor.addChild(root_0, char_literal60_tree);
            	    }

            	    pushFollow(FOLLOW_typeParameter_in_typeParameters979);
            	    typeParameter61=typeParameter();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameter61.getTree());

            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);


            char_literal62=(Token)match(input,GT,FOLLOW_GT_in_typeParameters1004); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal62_tree = 
            (CommonTree)adaptor.create(char_literal62)
            ;
            adaptor.addChild(root_0, char_literal62_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 15, typeParameters_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "typeParameters"


    public static class typeParameter_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "typeParameter"
    // grammar/Java.g:427:1: typeParameter : IDENTIFIER ( 'extends' typeBound )? ;
    public final JavaParser.typeParameter_return typeParameter() throws RecognitionException {
        JavaParser.typeParameter_return retval = new JavaParser.typeParameter_return();
        retval.start = input.LT(1);

        int typeParameter_StartIndex = input.index();

        CommonTree root_0 = null;

        Token IDENTIFIER63=null;
        Token string_literal64=null;
        JavaParser.typeBound_return typeBound65 =null;


        CommonTree IDENTIFIER63_tree=null;
        CommonTree string_literal64_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return retval; }

            // grammar/Java.g:428:5: ( IDENTIFIER ( 'extends' typeBound )? )
            // grammar/Java.g:428:9: IDENTIFIER ( 'extends' typeBound )?
            {
            root_0 = (CommonTree)adaptor.nil();


            IDENTIFIER63=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_typeParameter1023); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER63_tree = 
            (CommonTree)adaptor.create(IDENTIFIER63)
            ;
            adaptor.addChild(root_0, IDENTIFIER63_tree);
            }

            // grammar/Java.g:429:9: ( 'extends' typeBound )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==EXTENDS) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // grammar/Java.g:429:10: 'extends' typeBound
                    {
                    string_literal64=(Token)match(input,EXTENDS,FOLLOW_EXTENDS_in_typeParameter1034); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal64_tree = 
                    (CommonTree)adaptor.create(string_literal64)
                    ;
                    adaptor.addChild(root_0, string_literal64_tree);
                    }

                    pushFollow(FOLLOW_typeBound_in_typeParameter1036);
                    typeBound65=typeBound();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeBound65.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 16, typeParameter_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "typeParameter"


    public static class typeBound_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "typeBound"
    // grammar/Java.g:434:1: typeBound : type ( '&' type )* ;
    public final JavaParser.typeBound_return typeBound() throws RecognitionException {
        JavaParser.typeBound_return retval = new JavaParser.typeBound_return();
        retval.start = input.LT(1);

        int typeBound_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal67=null;
        JavaParser.type_return type66 =null;

        JavaParser.type_return type68 =null;


        CommonTree char_literal67_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return retval; }

            // grammar/Java.g:435:5: ( type ( '&' type )* )
            // grammar/Java.g:435:9: type ( '&' type )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_type_in_typeBound1067);
            type66=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type66.getTree());

            // grammar/Java.g:436:9: ( '&' type )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==AMP) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // grammar/Java.g:436:10: '&' type
            	    {
            	    char_literal67=(Token)match(input,AMP,FOLLOW_AMP_in_typeBound1078); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal67_tree = 
            	    (CommonTree)adaptor.create(char_literal67)
            	    ;
            	    adaptor.addChild(root_0, char_literal67_tree);
            	    }

            	    pushFollow(FOLLOW_type_in_typeBound1080);
            	    type68=type();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, type68.getTree());

            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 17, typeBound_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "typeBound"


    public static class enumDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "enumDeclaration"
    // grammar/Java.g:441:1: enumDeclaration : modifiers ( 'enum' ^) IDENTIFIER ( 'implements' typeList )? enumBody ;
    public final JavaParser.enumDeclaration_return enumDeclaration() throws RecognitionException {
        JavaParser.enumDeclaration_return retval = new JavaParser.enumDeclaration_return();
        retval.start = input.LT(1);

        int enumDeclaration_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal70=null;
        Token IDENTIFIER71=null;
        Token string_literal72=null;
        JavaParser.modifiers_return modifiers69 =null;

        JavaParser.typeList_return typeList73 =null;

        JavaParser.enumBody_return enumBody74 =null;


        CommonTree string_literal70_tree=null;
        CommonTree IDENTIFIER71_tree=null;
        CommonTree string_literal72_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return retval; }

            // grammar/Java.g:442:5: ( modifiers ( 'enum' ^) IDENTIFIER ( 'implements' typeList )? enumBody )
            // grammar/Java.g:442:9: modifiers ( 'enum' ^) IDENTIFIER ( 'implements' typeList )? enumBody
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_enumDeclaration1111);
            modifiers69=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers69.getTree());

            // grammar/Java.g:443:9: ( 'enum' ^)
            // grammar/Java.g:443:10: 'enum' ^
            {
            string_literal70=(Token)match(input,ENUM,FOLLOW_ENUM_in_enumDeclaration1122); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal70_tree = 
            (CommonTree)adaptor.create(string_literal70)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(string_literal70_tree, root_0);
            }

            }


            IDENTIFIER71=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_enumDeclaration1143); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER71_tree = 
            (CommonTree)adaptor.create(IDENTIFIER71)
            ;
            adaptor.addChild(root_0, IDENTIFIER71_tree);
            }

            // grammar/Java.g:446:9: ( 'implements' typeList )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==IMPLEMENTS) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // grammar/Java.g:446:10: 'implements' typeList
                    {
                    string_literal72=(Token)match(input,IMPLEMENTS,FOLLOW_IMPLEMENTS_in_enumDeclaration1154); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal72_tree = 
                    (CommonTree)adaptor.create(string_literal72)
                    ;
                    adaptor.addChild(root_0, string_literal72_tree);
                    }

                    pushFollow(FOLLOW_typeList_in_enumDeclaration1156);
                    typeList73=typeList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeList73.getTree());

                    }
                    break;

            }


            pushFollow(FOLLOW_enumBody_in_enumDeclaration1177);
            enumBody74=enumBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, enumBody74.getTree());

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 18, enumDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "enumDeclaration"


    public static class enumBody_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "enumBody"
    // grammar/Java.g:452:1: enumBody : '{' ( enumConstants )? ( enumBodyDeclarations )? '}' ;
    public final JavaParser.enumBody_return enumBody() throws RecognitionException {
        JavaParser.enumBody_return retval = new JavaParser.enumBody_return();
        retval.start = input.LT(1);

        int enumBody_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal75=null;
        Token char_literal78=null;
        JavaParser.enumConstants_return enumConstants76 =null;

        JavaParser.enumBodyDeclarations_return enumBodyDeclarations77 =null;


        CommonTree char_literal75_tree=null;
        CommonTree char_literal78_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return retval; }

            // grammar/Java.g:453:5: ( '{' ( enumConstants )? ( enumBodyDeclarations )? '}' )
            // grammar/Java.g:453:9: '{' ( enumConstants )? ( enumBodyDeclarations )? '}'
            {
            root_0 = (CommonTree)adaptor.nil();


            char_literal75=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_enumBody1198); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal75_tree = 
            (CommonTree)adaptor.create(char_literal75)
            ;
            adaptor.addChild(root_0, char_literal75_tree);
            }

            // grammar/Java.g:454:9: ( enumConstants )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==MONKEYS_AT) ) {
                int LA23_1 = input.LA(2);

                if ( (synpred35_Java()) ) {
                    alt23=1;
                }
            }
            else if ( (LA23_0==IDENTIFIER) ) {
                int LA23_2 = input.LA(2);

                if ( (synpred35_Java()) ) {
                    alt23=1;
                }
            }
            switch (alt23) {
                case 1 :
                    // grammar/Java.g:454:10: enumConstants
                    {
                    pushFollow(FOLLOW_enumConstants_in_enumBody1209);
                    enumConstants76=enumConstants();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumConstants76.getTree());

                    }
                    break;

            }


            // grammar/Java.g:456:9: ( enumBodyDeclarations )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==ABSTRACT||LA24_0==BOOLEAN||LA24_0==BYTE||LA24_0==CHAR||LA24_0==CLASS||LA24_0==DOUBLE||LA24_0==ENUM||LA24_0==FINAL||LA24_0==FLOAT||LA24_0==IDENTIFIER||(LA24_0 >= INT && LA24_0 <= INTERFACE)||LA24_0==LBRACE||LA24_0==LONG||LA24_0==LT||(LA24_0 >= MONKEYS_AT && LA24_0 <= NATIVE)||(LA24_0 >= PRIVATE && LA24_0 <= PUBLIC)||(LA24_0 >= SEMI && LA24_0 <= SHORT)||(LA24_0 >= STATIC && LA24_0 <= STRICTFP)||LA24_0==SYNCHRONIZED||LA24_0==TRANSIENT||(LA24_0 >= VOID && LA24_0 <= VOLATILE)) ) {
                alt24=1;
            }
            else if ( (LA24_0==RBRACE) ) {
                int LA24_2 = input.LA(2);

                if ( (synpred36_Java()) ) {
                    alt24=1;
                }
            }
            switch (alt24) {
                case 1 :
                    // grammar/Java.g:456:10: enumBodyDeclarations
                    {
                    pushFollow(FOLLOW_enumBodyDeclarations_in_enumBody1231);
                    enumBodyDeclarations77=enumBodyDeclarations();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumBodyDeclarations77.getTree());

                    }
                    break;

            }


            char_literal78=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_enumBody1252); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal78_tree = 
            (CommonTree)adaptor.create(char_literal78)
            ;
            adaptor.addChild(root_0, char_literal78_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 19, enumBody_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "enumBody"


    public static class enumConstants_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "enumConstants"
    // grammar/Java.g:462:1: enumConstants : enumConstant ( ',' enumConstant )* ';' -> ^( BLOCK_T enumConstant ( ',' enumConstant )* ';' ) ;
    public final JavaParser.enumConstants_return enumConstants() throws RecognitionException {
        JavaParser.enumConstants_return retval = new JavaParser.enumConstants_return();
        retval.start = input.LT(1);

        int enumConstants_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal80=null;
        Token char_literal82=null;
        JavaParser.enumConstant_return enumConstant79 =null;

        JavaParser.enumConstant_return enumConstant81 =null;


        CommonTree char_literal80_tree=null;
        CommonTree char_literal82_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleSubtreeStream stream_enumConstant=new RewriteRuleSubtreeStream(adaptor,"rule enumConstant");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return retval; }

            // grammar/Java.g:463:5: ( enumConstant ( ',' enumConstant )* ';' -> ^( BLOCK_T enumConstant ( ',' enumConstant )* ';' ) )
            // grammar/Java.g:463:9: enumConstant ( ',' enumConstant )* ';'
            {
            pushFollow(FOLLOW_enumConstant_in_enumConstants1272);
            enumConstant79=enumConstant();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_enumConstant.add(enumConstant79.getTree());

            // grammar/Java.g:464:9: ( ',' enumConstant )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==COMMA) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // grammar/Java.g:464:10: ',' enumConstant
            	    {
            	    char_literal80=(Token)match(input,COMMA,FOLLOW_COMMA_in_enumConstants1283); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(char_literal80);


            	    pushFollow(FOLLOW_enumConstant_in_enumConstants1285);
            	    enumConstant81=enumConstant();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_enumConstant.add(enumConstant81.getTree());

            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);


            char_literal82=(Token)match(input,SEMI,FOLLOW_SEMI_in_enumConstants1298); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMI.add(char_literal82);


            // AST REWRITE
            // elements: enumConstant, SEMI, enumConstant, COMMA
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 465:15: -> ^( BLOCK_T enumConstant ( ',' enumConstant )* ';' )
            {
                // grammar/Java.g:465:18: ^( BLOCK_T enumConstant ( ',' enumConstant )* ';' )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(BLOCK_T, "BLOCK_T")
                , root_1);

                adaptor.addChild(root_1, stream_enumConstant.nextTree());

                // grammar/Java.g:465:42: ( ',' enumConstant )*
                while ( stream_enumConstant.hasNext()||stream_COMMA.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_COMMA.nextNode()
                    );

                    adaptor.addChild(root_1, stream_enumConstant.nextTree());

                }
                stream_enumConstant.reset();
                stream_COMMA.reset();

                adaptor.addChild(root_1, 
                stream_SEMI.nextNode()
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 20, enumConstants_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "enumConstants"


    public static class enumConstant_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "enumConstant"
    // grammar/Java.g:472:1: enumConstant : ( annotations )? IDENTIFIER ( arguments )? ( classBody )? ;
    public final JavaParser.enumConstant_return enumConstant() throws RecognitionException {
        JavaParser.enumConstant_return retval = new JavaParser.enumConstant_return();
        retval.start = input.LT(1);

        int enumConstant_StartIndex = input.index();

        CommonTree root_0 = null;

        Token IDENTIFIER84=null;
        JavaParser.annotations_return annotations83 =null;

        JavaParser.arguments_return arguments85 =null;

        JavaParser.classBody_return classBody86 =null;


        CommonTree IDENTIFIER84_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return retval; }

            // grammar/Java.g:473:5: ( ( annotations )? IDENTIFIER ( arguments )? ( classBody )? )
            // grammar/Java.g:473:9: ( annotations )? IDENTIFIER ( arguments )? ( classBody )?
            {
            root_0 = (CommonTree)adaptor.nil();


            // grammar/Java.g:473:9: ( annotations )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==MONKEYS_AT) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // grammar/Java.g:473:10: annotations
                    {
                    pushFollow(FOLLOW_annotations_in_enumConstant1337);
                    annotations83=annotations();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotations83.getTree());

                    }
                    break;

            }


            IDENTIFIER84=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_enumConstant1358); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER84_tree = 
            (CommonTree)adaptor.create(IDENTIFIER84)
            ;
            adaptor.addChild(root_0, IDENTIFIER84_tree);
            }

            // grammar/Java.g:476:9: ( arguments )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==LPAREN) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // grammar/Java.g:476:10: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_enumConstant1369);
                    arguments85=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments85.getTree());

                    }
                    break;

            }


            // grammar/Java.g:478:9: ( classBody )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==LBRACE) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // grammar/Java.g:478:10: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_enumConstant1391);
                    classBody86=classBody();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classBody86.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 21, enumConstant_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "enumConstant"


    public static class enumBodyDeclarations_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "enumBodyDeclarations"
    // grammar/Java.g:484:1: enumBodyDeclarations : ( classBodyDeclaration )* ;
    public final JavaParser.enumBodyDeclarations_return enumBodyDeclarations() throws RecognitionException {
        JavaParser.enumBodyDeclarations_return retval = new JavaParser.enumBodyDeclarations_return();
        retval.start = input.LT(1);

        int enumBodyDeclarations_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.classBodyDeclaration_return classBodyDeclaration87 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return retval; }

            // grammar/Java.g:485:5: ( ( classBodyDeclaration )* )
            // grammar/Java.g:485:9: ( classBodyDeclaration )*
            {
            root_0 = (CommonTree)adaptor.nil();


            // grammar/Java.g:485:9: ( classBodyDeclaration )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( (LA29_0==ABSTRACT||LA29_0==BOOLEAN||LA29_0==BYTE||LA29_0==CHAR||LA29_0==CLASS||LA29_0==DOUBLE||LA29_0==ENUM||LA29_0==FINAL||LA29_0==FLOAT||LA29_0==IDENTIFIER||(LA29_0 >= INT && LA29_0 <= INTERFACE)||LA29_0==LBRACE||LA29_0==LONG||LA29_0==LT||(LA29_0 >= MONKEYS_AT && LA29_0 <= NATIVE)||(LA29_0 >= PRIVATE && LA29_0 <= PUBLIC)||(LA29_0 >= SEMI && LA29_0 <= SHORT)||(LA29_0 >= STATIC && LA29_0 <= STRICTFP)||LA29_0==SYNCHRONIZED||LA29_0==TRANSIENT||(LA29_0 >= VOID && LA29_0 <= VOLATILE)) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // grammar/Java.g:485:10: classBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_classBodyDeclaration_in_enumBodyDeclarations1432);
            	    classBodyDeclaration87=classBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, classBodyDeclaration87.getTree());

            	    }
            	    break;

            	default :
            	    break loop29;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 22, enumBodyDeclarations_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "enumBodyDeclarations"


    public static class interfaceDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "interfaceDeclaration"
    // grammar/Java.g:489:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );
    public final JavaParser.interfaceDeclaration_return interfaceDeclaration() throws RecognitionException {
        JavaParser.interfaceDeclaration_return retval = new JavaParser.interfaceDeclaration_return();
        retval.start = input.LT(1);

        int interfaceDeclaration_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.normalInterfaceDeclaration_return normalInterfaceDeclaration88 =null;

        JavaParser.annotationTypeDeclaration_return annotationTypeDeclaration89 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return retval; }

            // grammar/Java.g:490:5: ( normalInterfaceDeclaration | annotationTypeDeclaration )
            int alt30=2;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA30_1 = input.LA(2);

                if ( (synpred42_Java()) ) {
                    alt30=1;
                }
                else if ( (true) ) {
                    alt30=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 1, input);

                    throw nvae;

                }
                }
                break;
            case PUBLIC:
                {
                int LA30_2 = input.LA(2);

                if ( (synpred42_Java()) ) {
                    alt30=1;
                }
                else if ( (true) ) {
                    alt30=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 2, input);

                    throw nvae;

                }
                }
                break;
            case PROTECTED:
                {
                int LA30_3 = input.LA(2);

                if ( (synpred42_Java()) ) {
                    alt30=1;
                }
                else if ( (true) ) {
                    alt30=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 3, input);

                    throw nvae;

                }
                }
                break;
            case PRIVATE:
                {
                int LA30_4 = input.LA(2);

                if ( (synpred42_Java()) ) {
                    alt30=1;
                }
                else if ( (true) ) {
                    alt30=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 4, input);

                    throw nvae;

                }
                }
                break;
            case STATIC:
                {
                int LA30_5 = input.LA(2);

                if ( (synpred42_Java()) ) {
                    alt30=1;
                }
                else if ( (true) ) {
                    alt30=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 5, input);

                    throw nvae;

                }
                }
                break;
            case ABSTRACT:
                {
                int LA30_6 = input.LA(2);

                if ( (synpred42_Java()) ) {
                    alt30=1;
                }
                else if ( (true) ) {
                    alt30=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 6, input);

                    throw nvae;

                }
                }
                break;
            case FINAL:
                {
                int LA30_7 = input.LA(2);

                if ( (synpred42_Java()) ) {
                    alt30=1;
                }
                else if ( (true) ) {
                    alt30=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 7, input);

                    throw nvae;

                }
                }
                break;
            case NATIVE:
                {
                int LA30_8 = input.LA(2);

                if ( (synpred42_Java()) ) {
                    alt30=1;
                }
                else if ( (true) ) {
                    alt30=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 8, input);

                    throw nvae;

                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA30_9 = input.LA(2);

                if ( (synpred42_Java()) ) {
                    alt30=1;
                }
                else if ( (true) ) {
                    alt30=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 9, input);

                    throw nvae;

                }
                }
                break;
            case TRANSIENT:
                {
                int LA30_10 = input.LA(2);

                if ( (synpred42_Java()) ) {
                    alt30=1;
                }
                else if ( (true) ) {
                    alt30=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 10, input);

                    throw nvae;

                }
                }
                break;
            case VOLATILE:
                {
                int LA30_11 = input.LA(2);

                if ( (synpred42_Java()) ) {
                    alt30=1;
                }
                else if ( (true) ) {
                    alt30=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 11, input);

                    throw nvae;

                }
                }
                break;
            case STRICTFP:
                {
                int LA30_12 = input.LA(2);

                if ( (synpred42_Java()) ) {
                    alt30=1;
                }
                else if ( (true) ) {
                    alt30=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 12, input);

                    throw nvae;

                }
                }
                break;
            case INTERFACE:
                {
                alt30=1;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;

            }

            switch (alt30) {
                case 1 :
                    // grammar/Java.g:490:9: normalInterfaceDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration1462);
                    normalInterfaceDeclaration88=normalInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalInterfaceDeclaration88.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:491:9: annotationTypeDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration1472);
                    annotationTypeDeclaration89=annotationTypeDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeDeclaration89.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 23, interfaceDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "interfaceDeclaration"


    public static class normalInterfaceDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "normalInterfaceDeclaration"
    // grammar/Java.g:494:1: normalInterfaceDeclaration : modifiers 'interface' IDENTIFIER ( typeParameters )? ( 'extends' typeList )? interfaceBody ;
    public final JavaParser.normalInterfaceDeclaration_return normalInterfaceDeclaration() throws RecognitionException {
        JavaParser.normalInterfaceDeclaration_return retval = new JavaParser.normalInterfaceDeclaration_return();
        retval.start = input.LT(1);

        int normalInterfaceDeclaration_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal91=null;
        Token IDENTIFIER92=null;
        Token string_literal94=null;
        JavaParser.modifiers_return modifiers90 =null;

        JavaParser.typeParameters_return typeParameters93 =null;

        JavaParser.typeList_return typeList95 =null;

        JavaParser.interfaceBody_return interfaceBody96 =null;


        CommonTree string_literal91_tree=null;
        CommonTree IDENTIFIER92_tree=null;
        CommonTree string_literal94_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return retval; }

            // grammar/Java.g:495:5: ( modifiers 'interface' IDENTIFIER ( typeParameters )? ( 'extends' typeList )? interfaceBody )
            // grammar/Java.g:495:9: modifiers 'interface' IDENTIFIER ( typeParameters )? ( 'extends' typeList )? interfaceBody
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_normalInterfaceDeclaration1491);
            modifiers90=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers90.getTree());

            string_literal91=(Token)match(input,INTERFACE,FOLLOW_INTERFACE_in_normalInterfaceDeclaration1493); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal91_tree = 
            (CommonTree)adaptor.create(string_literal91)
            ;
            adaptor.addChild(root_0, string_literal91_tree);
            }

            IDENTIFIER92=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_normalInterfaceDeclaration1495); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER92_tree = 
            (CommonTree)adaptor.create(IDENTIFIER92)
            ;
            adaptor.addChild(root_0, IDENTIFIER92_tree);
            }

            // grammar/Java.g:496:9: ( typeParameters )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==LT) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // grammar/Java.g:496:10: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalInterfaceDeclaration1506);
                    typeParameters93=typeParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters93.getTree());

                    }
                    break;

            }


            // grammar/Java.g:498:9: ( 'extends' typeList )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==EXTENDS) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // grammar/Java.g:498:10: 'extends' typeList
                    {
                    string_literal94=(Token)match(input,EXTENDS,FOLLOW_EXTENDS_in_normalInterfaceDeclaration1528); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal94_tree = 
                    (CommonTree)adaptor.create(string_literal94)
                    ;
                    adaptor.addChild(root_0, string_literal94_tree);
                    }

                    pushFollow(FOLLOW_typeList_in_normalInterfaceDeclaration1530);
                    typeList95=typeList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeList95.getTree());

                    }
                    break;

            }


            pushFollow(FOLLOW_interfaceBody_in_normalInterfaceDeclaration1551);
            interfaceBody96=interfaceBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceBody96.getTree());

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 24, normalInterfaceDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "normalInterfaceDeclaration"


    public static class typeList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "typeList"
    // grammar/Java.g:503:1: typeList : type ( ',' type )* ;
    public final JavaParser.typeList_return typeList() throws RecognitionException {
        JavaParser.typeList_return retval = new JavaParser.typeList_return();
        retval.start = input.LT(1);

        int typeList_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal98=null;
        JavaParser.type_return type97 =null;

        JavaParser.type_return type99 =null;


        CommonTree char_literal98_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return retval; }

            // grammar/Java.g:504:5: ( type ( ',' type )* )
            // grammar/Java.g:504:9: type ( ',' type )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_type_in_typeList1570);
            type97=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type97.getTree());

            // grammar/Java.g:505:9: ( ',' type )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);

                if ( (LA33_0==COMMA) ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // grammar/Java.g:505:10: ',' type
            	    {
            	    char_literal98=(Token)match(input,COMMA,FOLLOW_COMMA_in_typeList1581); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal98_tree = 
            	    (CommonTree)adaptor.create(char_literal98)
            	    ;
            	    adaptor.addChild(root_0, char_literal98_tree);
            	    }

            	    pushFollow(FOLLOW_type_in_typeList1583);
            	    type99=type();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, type99.getTree());

            	    }
            	    break;

            	default :
            	    break loop33;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 25, typeList_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "typeList"


    public static class classBody_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "classBody"
    // grammar/Java.g:509:1: classBody : '{' ( classBodyDeclaration )* '}' ;
    public final JavaParser.classBody_return classBody() throws RecognitionException {
        JavaParser.classBody_return retval = new JavaParser.classBody_return();
        retval.start = input.LT(1);

        int classBody_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal100=null;
        Token char_literal102=null;
        JavaParser.classBodyDeclaration_return classBodyDeclaration101 =null;


        CommonTree char_literal100_tree=null;
        CommonTree char_literal102_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return retval; }

            // grammar/Java.g:510:5: ( '{' ( classBodyDeclaration )* '}' )
            // grammar/Java.g:510:9: '{' ( classBodyDeclaration )* '}'
            {
            root_0 = (CommonTree)adaptor.nil();


            char_literal100=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_classBody1613); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal100_tree = 
            (CommonTree)adaptor.create(char_literal100)
            ;
            adaptor.addChild(root_0, char_literal100_tree);
            }

            // grammar/Java.g:511:9: ( classBodyDeclaration )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==ABSTRACT||LA34_0==BOOLEAN||LA34_0==BYTE||LA34_0==CHAR||LA34_0==CLASS||LA34_0==DOUBLE||LA34_0==ENUM||LA34_0==FINAL||LA34_0==FLOAT||LA34_0==IDENTIFIER||(LA34_0 >= INT && LA34_0 <= INTERFACE)||LA34_0==LBRACE||LA34_0==LONG||LA34_0==LT||(LA34_0 >= MONKEYS_AT && LA34_0 <= NATIVE)||(LA34_0 >= PRIVATE && LA34_0 <= PUBLIC)||(LA34_0 >= SEMI && LA34_0 <= SHORT)||(LA34_0 >= STATIC && LA34_0 <= STRICTFP)||LA34_0==SYNCHRONIZED||LA34_0==TRANSIENT||(LA34_0 >= VOID && LA34_0 <= VOLATILE)) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // grammar/Java.g:511:10: classBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_classBodyDeclaration_in_classBody1624);
            	    classBodyDeclaration101=classBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, classBodyDeclaration101.getTree());

            	    }
            	    break;

            	default :
            	    break loop34;
                }
            } while (true);


            char_literal102=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_classBody1645); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal102_tree = 
            (CommonTree)adaptor.create(char_literal102)
            ;
            adaptor.addChild(root_0, char_literal102_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 26, classBody_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "classBody"


    public static class interfaceBody_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "interfaceBody"
    // grammar/Java.g:516:1: interfaceBody : '{' ( interfaceBodyDeclaration )* '}' ;
    public final JavaParser.interfaceBody_return interfaceBody() throws RecognitionException {
        JavaParser.interfaceBody_return retval = new JavaParser.interfaceBody_return();
        retval.start = input.LT(1);

        int interfaceBody_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal103=null;
        Token char_literal105=null;
        JavaParser.interfaceBodyDeclaration_return interfaceBodyDeclaration104 =null;


        CommonTree char_literal103_tree=null;
        CommonTree char_literal105_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 27) ) { return retval; }

            // grammar/Java.g:517:5: ( '{' ( interfaceBodyDeclaration )* '}' )
            // grammar/Java.g:517:9: '{' ( interfaceBodyDeclaration )* '}'
            {
            root_0 = (CommonTree)adaptor.nil();


            char_literal103=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_interfaceBody1664); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal103_tree = 
            (CommonTree)adaptor.create(char_literal103)
            ;
            adaptor.addChild(root_0, char_literal103_tree);
            }

            // grammar/Java.g:518:9: ( interfaceBodyDeclaration )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( (LA35_0==ABSTRACT||LA35_0==BOOLEAN||LA35_0==BYTE||LA35_0==CHAR||LA35_0==CLASS||LA35_0==DOUBLE||LA35_0==ENUM||LA35_0==FINAL||LA35_0==FLOAT||LA35_0==IDENTIFIER||(LA35_0 >= INT && LA35_0 <= INTERFACE)||LA35_0==LONG||LA35_0==LT||(LA35_0 >= MONKEYS_AT && LA35_0 <= NATIVE)||(LA35_0 >= PRIVATE && LA35_0 <= PUBLIC)||(LA35_0 >= SEMI && LA35_0 <= SHORT)||(LA35_0 >= STATIC && LA35_0 <= STRICTFP)||LA35_0==SYNCHRONIZED||LA35_0==TRANSIENT||(LA35_0 >= VOID && LA35_0 <= VOLATILE)) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // grammar/Java.g:518:10: interfaceBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_interfaceBodyDeclaration_in_interfaceBody1675);
            	    interfaceBodyDeclaration104=interfaceBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceBodyDeclaration104.getTree());

            	    }
            	    break;

            	default :
            	    break loop35;
                }
            } while (true);


            char_literal105=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_interfaceBody1696); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal105_tree = 
            (CommonTree)adaptor.create(char_literal105)
            ;
            adaptor.addChild(root_0, char_literal105_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 27, interfaceBody_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "interfaceBody"


    public static class classBodyDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "classBodyDeclaration"
    // grammar/Java.g:523:1: classBodyDeclaration : ( ';' | ( 'static' )? block | memberDecl );
    public final JavaParser.classBodyDeclaration_return classBodyDeclaration() throws RecognitionException {
        JavaParser.classBodyDeclaration_return retval = new JavaParser.classBodyDeclaration_return();
        retval.start = input.LT(1);

        int classBodyDeclaration_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal106=null;
        Token string_literal107=null;
        JavaParser.block_return block108 =null;

        JavaParser.memberDecl_return memberDecl109 =null;


        CommonTree char_literal106_tree=null;
        CommonTree string_literal107_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return retval; }

            // grammar/Java.g:524:5: ( ';' | ( 'static' )? block | memberDecl )
            int alt37=3;
            switch ( input.LA(1) ) {
            case SEMI:
                {
                alt37=1;
                }
                break;
            case STATIC:
                {
                int LA37_2 = input.LA(2);

                if ( (LA37_2==LBRACE) ) {
                    alt37=2;
                }
                else if ( (LA37_2==ABSTRACT||LA37_2==BOOLEAN||LA37_2==BYTE||LA37_2==CHAR||LA37_2==CLASS||LA37_2==DOUBLE||LA37_2==ENUM||LA37_2==FINAL||LA37_2==FLOAT||LA37_2==IDENTIFIER||(LA37_2 >= INT && LA37_2 <= INTERFACE)||LA37_2==LONG||LA37_2==LT||(LA37_2 >= MONKEYS_AT && LA37_2 <= NATIVE)||(LA37_2 >= PRIVATE && LA37_2 <= PUBLIC)||LA37_2==SHORT||(LA37_2 >= STATIC && LA37_2 <= STRICTFP)||LA37_2==SYNCHRONIZED||LA37_2==TRANSIENT||(LA37_2 >= VOID && LA37_2 <= VOLATILE)) ) {
                    alt37=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 37, 2, input);

                    throw nvae;

                }
                }
                break;
            case LBRACE:
                {
                alt37=2;
                }
                break;
            case ABSTRACT:
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case CLASS:
            case DOUBLE:
            case ENUM:
            case FINAL:
            case FLOAT:
            case IDENTIFIER:
            case INT:
            case INTERFACE:
            case LONG:
            case LT:
            case MONKEYS_AT:
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
                {
                alt37=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, input);

                throw nvae;

            }

            switch (alt37) {
                case 1 :
                    // grammar/Java.g:524:9: ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal106=(Token)match(input,SEMI,FOLLOW_SEMI_in_classBodyDeclaration1715); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal106_tree = 
                    (CommonTree)adaptor.create(char_literal106)
                    ;
                    adaptor.addChild(root_0, char_literal106_tree);
                    }

                    }
                    break;
                case 2 :
                    // grammar/Java.g:525:9: ( 'static' )? block
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    // grammar/Java.g:525:9: ( 'static' )?
                    int alt36=2;
                    int LA36_0 = input.LA(1);

                    if ( (LA36_0==STATIC) ) {
                        alt36=1;
                    }
                    switch (alt36) {
                        case 1 :
                            // grammar/Java.g:525:10: 'static'
                            {
                            string_literal107=(Token)match(input,STATIC,FOLLOW_STATIC_in_classBodyDeclaration1726); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal107_tree = 
                            (CommonTree)adaptor.create(string_literal107)
                            ;
                            adaptor.addChild(root_0, string_literal107_tree);
                            }

                            }
                            break;

                    }


                    pushFollow(FOLLOW_block_in_classBodyDeclaration1747);
                    block108=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block108.getTree());

                    }
                    break;
                case 3 :
                    // grammar/Java.g:528:9: memberDecl
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_memberDecl_in_classBodyDeclaration1757);
                    memberDecl109=memberDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, memberDecl109.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 28, classBodyDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "classBodyDeclaration"


    public static class memberDecl_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "memberDecl"
    // grammar/Java.g:531:1: memberDecl : ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration );
    public final JavaParser.memberDecl_return memberDecl() throws RecognitionException {
        JavaParser.memberDecl_return retval = new JavaParser.memberDecl_return();
        retval.start = input.LT(1);

        int memberDecl_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.fieldDeclaration_return fieldDeclaration110 =null;

        JavaParser.methodDeclaration_return methodDeclaration111 =null;

        JavaParser.classDeclaration_return classDeclaration112 =null;

        JavaParser.interfaceDeclaration_return interfaceDeclaration113 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 29) ) { return retval; }

            // grammar/Java.g:532:5: ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration )
            int alt38=4;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA38_1 = input.LA(2);

                if ( (synpred51_Java()) ) {
                    alt38=1;
                }
                else if ( (synpred52_Java()) ) {
                    alt38=2;
                }
                else if ( (synpred53_Java()) ) {
                    alt38=3;
                }
                else if ( (true) ) {
                    alt38=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 1, input);

                    throw nvae;

                }
                }
                break;
            case PUBLIC:
                {
                int LA38_2 = input.LA(2);

                if ( (synpred51_Java()) ) {
                    alt38=1;
                }
                else if ( (synpred52_Java()) ) {
                    alt38=2;
                }
                else if ( (synpred53_Java()) ) {
                    alt38=3;
                }
                else if ( (true) ) {
                    alt38=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 2, input);

                    throw nvae;

                }
                }
                break;
            case PROTECTED:
                {
                int LA38_3 = input.LA(2);

                if ( (synpred51_Java()) ) {
                    alt38=1;
                }
                else if ( (synpred52_Java()) ) {
                    alt38=2;
                }
                else if ( (synpred53_Java()) ) {
                    alt38=3;
                }
                else if ( (true) ) {
                    alt38=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 3, input);

                    throw nvae;

                }
                }
                break;
            case PRIVATE:
                {
                int LA38_4 = input.LA(2);

                if ( (synpred51_Java()) ) {
                    alt38=1;
                }
                else if ( (synpred52_Java()) ) {
                    alt38=2;
                }
                else if ( (synpred53_Java()) ) {
                    alt38=3;
                }
                else if ( (true) ) {
                    alt38=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 4, input);

                    throw nvae;

                }
                }
                break;
            case STATIC:
                {
                int LA38_5 = input.LA(2);

                if ( (synpred51_Java()) ) {
                    alt38=1;
                }
                else if ( (synpred52_Java()) ) {
                    alt38=2;
                }
                else if ( (synpred53_Java()) ) {
                    alt38=3;
                }
                else if ( (true) ) {
                    alt38=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 5, input);

                    throw nvae;

                }
                }
                break;
            case ABSTRACT:
                {
                int LA38_6 = input.LA(2);

                if ( (synpred51_Java()) ) {
                    alt38=1;
                }
                else if ( (synpred52_Java()) ) {
                    alt38=2;
                }
                else if ( (synpred53_Java()) ) {
                    alt38=3;
                }
                else if ( (true) ) {
                    alt38=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 6, input);

                    throw nvae;

                }
                }
                break;
            case FINAL:
                {
                int LA38_7 = input.LA(2);

                if ( (synpred51_Java()) ) {
                    alt38=1;
                }
                else if ( (synpred52_Java()) ) {
                    alt38=2;
                }
                else if ( (synpred53_Java()) ) {
                    alt38=3;
                }
                else if ( (true) ) {
                    alt38=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 7, input);

                    throw nvae;

                }
                }
                break;
            case NATIVE:
                {
                int LA38_8 = input.LA(2);

                if ( (synpred51_Java()) ) {
                    alt38=1;
                }
                else if ( (synpred52_Java()) ) {
                    alt38=2;
                }
                else if ( (synpred53_Java()) ) {
                    alt38=3;
                }
                else if ( (true) ) {
                    alt38=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 8, input);

                    throw nvae;

                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA38_9 = input.LA(2);

                if ( (synpred51_Java()) ) {
                    alt38=1;
                }
                else if ( (synpred52_Java()) ) {
                    alt38=2;
                }
                else if ( (synpred53_Java()) ) {
                    alt38=3;
                }
                else if ( (true) ) {
                    alt38=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 9, input);

                    throw nvae;

                }
                }
                break;
            case TRANSIENT:
                {
                int LA38_10 = input.LA(2);

                if ( (synpred51_Java()) ) {
                    alt38=1;
                }
                else if ( (synpred52_Java()) ) {
                    alt38=2;
                }
                else if ( (synpred53_Java()) ) {
                    alt38=3;
                }
                else if ( (true) ) {
                    alt38=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 10, input);

                    throw nvae;

                }
                }
                break;
            case VOLATILE:
                {
                int LA38_11 = input.LA(2);

                if ( (synpred51_Java()) ) {
                    alt38=1;
                }
                else if ( (synpred52_Java()) ) {
                    alt38=2;
                }
                else if ( (synpred53_Java()) ) {
                    alt38=3;
                }
                else if ( (true) ) {
                    alt38=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 11, input);

                    throw nvae;

                }
                }
                break;
            case STRICTFP:
                {
                int LA38_12 = input.LA(2);

                if ( (synpred51_Java()) ) {
                    alt38=1;
                }
                else if ( (synpred52_Java()) ) {
                    alt38=2;
                }
                else if ( (synpred53_Java()) ) {
                    alt38=3;
                }
                else if ( (true) ) {
                    alt38=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 12, input);

                    throw nvae;

                }
                }
                break;
            case IDENTIFIER:
                {
                int LA38_13 = input.LA(2);

                if ( (synpred51_Java()) ) {
                    alt38=1;
                }
                else if ( (synpred52_Java()) ) {
                    alt38=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 13, input);

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
                int LA38_14 = input.LA(2);

                if ( (synpred51_Java()) ) {
                    alt38=1;
                }
                else if ( (synpred52_Java()) ) {
                    alt38=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 14, input);

                    throw nvae;

                }
                }
                break;
            case LT:
            case VOID:
                {
                alt38=2;
                }
                break;
            case CLASS:
            case ENUM:
                {
                alt38=3;
                }
                break;
            case INTERFACE:
                {
                alt38=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                throw nvae;

            }

            switch (alt38) {
                case 1 :
                    // grammar/Java.g:532:10: fieldDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_fieldDeclaration_in_memberDecl1777);
                    fieldDeclaration110=fieldDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, fieldDeclaration110.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:533:10: methodDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_methodDeclaration_in_memberDecl1788);
                    methodDeclaration111=methodDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, methodDeclaration111.getTree());

                    }
                    break;
                case 3 :
                    // grammar/Java.g:534:10: classDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_classDeclaration_in_memberDecl1799);
                    classDeclaration112=classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classDeclaration112.getTree());

                    }
                    break;
                case 4 :
                    // grammar/Java.g:535:10: interfaceDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_interfaceDeclaration_in_memberDecl1810);
                    interfaceDeclaration113=interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceDeclaration113.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 29, memberDecl_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "memberDecl"


    public static class methodDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "methodDeclaration"
    // grammar/Java.g:539:1: methodDeclaration : methodDeclarationInner -> ^( METHOD_T methodDeclarationInner ) ;
    public final JavaParser.methodDeclaration_return methodDeclaration() throws RecognitionException {
        JavaParser.methodDeclaration_return retval = new JavaParser.methodDeclaration_return();
        retval.start = input.LT(1);

        int methodDeclaration_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.methodDeclarationInner_return methodDeclarationInner114 =null;


        RewriteRuleSubtreeStream stream_methodDeclarationInner=new RewriteRuleSubtreeStream(adaptor,"rule methodDeclarationInner");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 30) ) { return retval; }

            // grammar/Java.g:540:2: ( methodDeclarationInner -> ^( METHOD_T methodDeclarationInner ) )
            // grammar/Java.g:540:3: methodDeclarationInner
            {
            pushFollow(FOLLOW_methodDeclarationInner_in_methodDeclaration1824);
            methodDeclarationInner114=methodDeclarationInner();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_methodDeclarationInner.add(methodDeclarationInner114.getTree());

            // AST REWRITE
            // elements: methodDeclarationInner
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 540:26: -> ^( METHOD_T methodDeclarationInner )
            {
                // grammar/Java.g:540:29: ^( METHOD_T methodDeclarationInner )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(METHOD_T, "METHOD_T")
                , root_1);

                adaptor.addChild(root_1, stream_methodDeclarationInner.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 30, methodDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "methodDeclaration"


    public static class methodDeclarationInner_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "methodDeclarationInner"
    // grammar/Java.g:543:1: methodDeclarationInner : ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? methodRetValue IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) );
    public final JavaParser.methodDeclarationInner_return methodDeclarationInner() throws RecognitionException {
        JavaParser.methodDeclarationInner_return retval = new JavaParser.methodDeclarationInner_return();
        retval.start = input.LT(1);

        int methodDeclarationInner_StartIndex = input.index();

        CommonTree root_0 = null;

        Token IDENTIFIER117=null;
        Token string_literal119=null;
        Token char_literal121=null;
        Token char_literal124=null;
        Token IDENTIFIER128=null;
        Token char_literal130=null;
        Token char_literal131=null;
        Token string_literal132=null;
        Token char_literal135=null;
        JavaParser.modifiers_return modifiers115 =null;

        JavaParser.typeParameters_return typeParameters116 =null;

        JavaParser.formalParameters_return formalParameters118 =null;

        JavaParser.qualifiedNameList_return qualifiedNameList120 =null;

        JavaParser.explicitConstructorInvocation_return explicitConstructorInvocation122 =null;

        JavaParser.blockStatement_return blockStatement123 =null;

        JavaParser.modifiers_return modifiers125 =null;

        JavaParser.typeParameters_return typeParameters126 =null;

        JavaParser.methodRetValue_return methodRetValue127 =null;

        JavaParser.formalParameters_return formalParameters129 =null;

        JavaParser.qualifiedNameList_return qualifiedNameList133 =null;

        JavaParser.block_return block134 =null;


        CommonTree IDENTIFIER117_tree=null;
        CommonTree string_literal119_tree=null;
        CommonTree char_literal121_tree=null;
        CommonTree char_literal124_tree=null;
        CommonTree IDENTIFIER128_tree=null;
        CommonTree char_literal130_tree=null;
        CommonTree char_literal131_tree=null;
        CommonTree string_literal132_tree=null;
        CommonTree char_literal135_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 31) ) { return retval; }

            // grammar/Java.g:544:5: ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? methodRetValue IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) )
            int alt47=2;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA47_1 = input.LA(2);

                if ( (synpred58_Java()) ) {
                    alt47=1;
                }
                else if ( (true) ) {
                    alt47=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 1, input);

                    throw nvae;

                }
                }
                break;
            case PUBLIC:
                {
                int LA47_2 = input.LA(2);

                if ( (synpred58_Java()) ) {
                    alt47=1;
                }
                else if ( (true) ) {
                    alt47=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 2, input);

                    throw nvae;

                }
                }
                break;
            case PROTECTED:
                {
                int LA47_3 = input.LA(2);

                if ( (synpred58_Java()) ) {
                    alt47=1;
                }
                else if ( (true) ) {
                    alt47=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 3, input);

                    throw nvae;

                }
                }
                break;
            case PRIVATE:
                {
                int LA47_4 = input.LA(2);

                if ( (synpred58_Java()) ) {
                    alt47=1;
                }
                else if ( (true) ) {
                    alt47=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 4, input);

                    throw nvae;

                }
                }
                break;
            case STATIC:
                {
                int LA47_5 = input.LA(2);

                if ( (synpred58_Java()) ) {
                    alt47=1;
                }
                else if ( (true) ) {
                    alt47=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 5, input);

                    throw nvae;

                }
                }
                break;
            case ABSTRACT:
                {
                int LA47_6 = input.LA(2);

                if ( (synpred58_Java()) ) {
                    alt47=1;
                }
                else if ( (true) ) {
                    alt47=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 6, input);

                    throw nvae;

                }
                }
                break;
            case FINAL:
                {
                int LA47_7 = input.LA(2);

                if ( (synpred58_Java()) ) {
                    alt47=1;
                }
                else if ( (true) ) {
                    alt47=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 7, input);

                    throw nvae;

                }
                }
                break;
            case NATIVE:
                {
                int LA47_8 = input.LA(2);

                if ( (synpred58_Java()) ) {
                    alt47=1;
                }
                else if ( (true) ) {
                    alt47=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 8, input);

                    throw nvae;

                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA47_9 = input.LA(2);

                if ( (synpred58_Java()) ) {
                    alt47=1;
                }
                else if ( (true) ) {
                    alt47=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 9, input);

                    throw nvae;

                }
                }
                break;
            case TRANSIENT:
                {
                int LA47_10 = input.LA(2);

                if ( (synpred58_Java()) ) {
                    alt47=1;
                }
                else if ( (true) ) {
                    alt47=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 10, input);

                    throw nvae;

                }
                }
                break;
            case VOLATILE:
                {
                int LA47_11 = input.LA(2);

                if ( (synpred58_Java()) ) {
                    alt47=1;
                }
                else if ( (true) ) {
                    alt47=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 11, input);

                    throw nvae;

                }
                }
                break;
            case STRICTFP:
                {
                int LA47_12 = input.LA(2);

                if ( (synpred58_Java()) ) {
                    alt47=1;
                }
                else if ( (true) ) {
                    alt47=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 12, input);

                    throw nvae;

                }
                }
                break;
            case LT:
                {
                int LA47_13 = input.LA(2);

                if ( (synpred58_Java()) ) {
                    alt47=1;
                }
                else if ( (true) ) {
                    alt47=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 13, input);

                    throw nvae;

                }
                }
                break;
            case IDENTIFIER:
                {
                int LA47_14 = input.LA(2);

                if ( (synpred58_Java()) ) {
                    alt47=1;
                }
                else if ( (true) ) {
                    alt47=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 14, input);

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
                alt47=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 47, 0, input);

                throw nvae;

            }

            switch (alt47) {
                case 1 :
                    // grammar/Java.g:546:10: modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_modifiers_in_methodDeclarationInner1865);
                    modifiers115=modifiers();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers115.getTree());

                    // grammar/Java.g:547:9: ( typeParameters )?
                    int alt39=2;
                    int LA39_0 = input.LA(1);

                    if ( (LA39_0==LT) ) {
                        alt39=1;
                    }
                    switch (alt39) {
                        case 1 :
                            // grammar/Java.g:547:10: typeParameters
                            {
                            pushFollow(FOLLOW_typeParameters_in_methodDeclarationInner1876);
                            typeParameters116=typeParameters();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters116.getTree());

                            }
                            break;

                    }


                    IDENTIFIER117=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodDeclarationInner1897); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER117_tree = 
                    (CommonTree)adaptor.create(IDENTIFIER117)
                    ;
                    adaptor.addChild(root_0, IDENTIFIER117_tree);
                    }

                    pushFollow(FOLLOW_formalParameters_in_methodDeclarationInner1907);
                    formalParameters118=formalParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters118.getTree());

                    // grammar/Java.g:551:9: ( 'throws' qualifiedNameList )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);

                    if ( (LA40_0==THROWS) ) {
                        alt40=1;
                    }
                    switch (alt40) {
                        case 1 :
                            // grammar/Java.g:551:10: 'throws' qualifiedNameList
                            {
                            string_literal119=(Token)match(input,THROWS,FOLLOW_THROWS_in_methodDeclarationInner1918); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal119_tree = 
                            (CommonTree)adaptor.create(string_literal119)
                            ;
                            adaptor.addChild(root_0, string_literal119_tree);
                            }

                            pushFollow(FOLLOW_qualifiedNameList_in_methodDeclarationInner1920);
                            qualifiedNameList120=qualifiedNameList();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList120.getTree());

                            }
                            break;

                    }


                    char_literal121=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_methodDeclarationInner1941); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal121_tree = 
                    (CommonTree)adaptor.create(char_literal121)
                    ;
                    adaptor.addChild(root_0, char_literal121_tree);
                    }

                    // grammar/Java.g:554:9: ( explicitConstructorInvocation )?
                    int alt41=2;
                    switch ( input.LA(1) ) {
                        case LT:
                            {
                            alt41=1;
                            }
                            break;
                        case THIS:
                            {
                            int LA41_2 = input.LA(2);

                            if ( (synpred56_Java()) ) {
                                alt41=1;
                            }
                            }
                            break;
                        case LPAREN:
                            {
                            int LA41_3 = input.LA(2);

                            if ( (synpred56_Java()) ) {
                                alt41=1;
                            }
                            }
                            break;
                        case SUPER:
                            {
                            int LA41_4 = input.LA(2);

                            if ( (synpred56_Java()) ) {
                                alt41=1;
                            }
                            }
                            break;
                        case IDENTIFIER:
                            {
                            int LA41_5 = input.LA(2);

                            if ( (synpred56_Java()) ) {
                                alt41=1;
                            }
                            }
                            break;
                        case CHARLITERAL:
                        case DOUBLELITERAL:
                        case FALSE:
                        case FLOATLITERAL:
                        case INTLITERAL:
                        case LONGLITERAL:
                        case NULL:
                        case STRINGLITERAL:
                        case TRUE:
                            {
                            int LA41_6 = input.LA(2);

                            if ( (synpred56_Java()) ) {
                                alt41=1;
                            }
                            }
                            break;
                        case NEW:
                            {
                            int LA41_7 = input.LA(2);

                            if ( (synpred56_Java()) ) {
                                alt41=1;
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
                            int LA41_8 = input.LA(2);

                            if ( (synpred56_Java()) ) {
                                alt41=1;
                            }
                            }
                            break;
                        case VOID:
                            {
                            int LA41_9 = input.LA(2);

                            if ( (synpred56_Java()) ) {
                                alt41=1;
                            }
                            }
                            break;
                    }

                    switch (alt41) {
                        case 1 :
                            // grammar/Java.g:554:10: explicitConstructorInvocation
                            {
                            pushFollow(FOLLOW_explicitConstructorInvocation_in_methodDeclarationInner1952);
                            explicitConstructorInvocation122=explicitConstructorInvocation();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, explicitConstructorInvocation122.getTree());

                            }
                            break;

                    }


                    // grammar/Java.g:556:9: ( blockStatement )*
                    loop42:
                    do {
                        int alt42=2;
                        int LA42_0 = input.LA(1);

                        if ( (LA42_0==EOF||LA42_0==ABSTRACT||(LA42_0 >= ASSERT && LA42_0 <= BANG)||(LA42_0 >= BOOLEAN && LA42_0 <= BYTE)||(LA42_0 >= CHAR && LA42_0 <= CLASS)||LA42_0==CONTINUE||LA42_0==DO||(LA42_0 >= DOUBLE && LA42_0 <= DOUBLELITERAL)||LA42_0==ENUM||(LA42_0 >= FALSE && LA42_0 <= FINAL)||(LA42_0 >= FLOAT && LA42_0 <= FOR)||(LA42_0 >= IDENTIFIER && LA42_0 <= IF)||(LA42_0 >= INT && LA42_0 <= INTLITERAL)||LA42_0==LBRACE||(LA42_0 >= LONG && LA42_0 <= LT)||(LA42_0 >= MONKEYS_AT && LA42_0 <= NULL)||LA42_0==PLUS||(LA42_0 >= PLUSPLUS && LA42_0 <= PUBLIC)||LA42_0==RETURN||(LA42_0 >= SEMI && LA42_0 <= SHORT)||(LA42_0 >= STATIC && LA42_0 <= SUB)||(LA42_0 >= SUBSUB && LA42_0 <= SYNCHRONIZED)||(LA42_0 >= THIS && LA42_0 <= THROW)||(LA42_0 >= TILDE && LA42_0 <= TRY)||(LA42_0 >= VOID && LA42_0 <= WHILE)) ) {
                            alt42=1;
                        }


                        switch (alt42) {
                    	case 1 :
                    	    // grammar/Java.g:556:10: blockStatement
                    	    {
                    	    pushFollow(FOLLOW_blockStatement_in_methodDeclarationInner1974);
                    	    blockStatement123=blockStatement();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, blockStatement123.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop42;
                        }
                    } while (true);


                    char_literal124=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_methodDeclarationInner1995); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal124_tree = 
                    (CommonTree)adaptor.create(char_literal124)
                    ;
                    adaptor.addChild(root_0, char_literal124_tree);
                    }

                    }
                    break;
                case 2 :
                    // grammar/Java.g:559:9: modifiers ( typeParameters )? methodRetValue IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' )
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_modifiers_in_methodDeclarationInner2005);
                    modifiers125=modifiers();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers125.getTree());

                    // grammar/Java.g:560:9: ( typeParameters )?
                    int alt43=2;
                    int LA43_0 = input.LA(1);

                    if ( (LA43_0==LT) ) {
                        alt43=1;
                    }
                    switch (alt43) {
                        case 1 :
                            // grammar/Java.g:560:10: typeParameters
                            {
                            pushFollow(FOLLOW_typeParameters_in_methodDeclarationInner2016);
                            typeParameters126=typeParameters();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters126.getTree());

                            }
                            break;

                    }


                    pushFollow(FOLLOW_methodRetValue_in_methodDeclarationInner2037);
                    methodRetValue127=methodRetValue();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, methodRetValue127.getTree());

                    IDENTIFIER128=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodDeclarationInner2047); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER128_tree = 
                    (CommonTree)adaptor.create(IDENTIFIER128)
                    ;
                    adaptor.addChild(root_0, IDENTIFIER128_tree);
                    }

                    pushFollow(FOLLOW_formalParameters_in_methodDeclarationInner2057);
                    formalParameters129=formalParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters129.getTree());

                    // grammar/Java.g:565:9: ( '[' ']' )*
                    loop44:
                    do {
                        int alt44=2;
                        int LA44_0 = input.LA(1);

                        if ( (LA44_0==LBRACKET) ) {
                            alt44=1;
                        }


                        switch (alt44) {
                    	case 1 :
                    	    // grammar/Java.g:565:10: '[' ']'
                    	    {
                    	    char_literal130=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_methodDeclarationInner2068); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal130_tree = 
                    	    (CommonTree)adaptor.create(char_literal130)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal130_tree);
                    	    }

                    	    char_literal131=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_methodDeclarationInner2070); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal131_tree = 
                    	    (CommonTree)adaptor.create(char_literal131)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal131_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop44;
                        }
                    } while (true);


                    // grammar/Java.g:567:9: ( 'throws' qualifiedNameList )?
                    int alt45=2;
                    int LA45_0 = input.LA(1);

                    if ( (LA45_0==THROWS) ) {
                        alt45=1;
                    }
                    switch (alt45) {
                        case 1 :
                            // grammar/Java.g:567:10: 'throws' qualifiedNameList
                            {
                            string_literal132=(Token)match(input,THROWS,FOLLOW_THROWS_in_methodDeclarationInner2092); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal132_tree = 
                            (CommonTree)adaptor.create(string_literal132)
                            ;
                            adaptor.addChild(root_0, string_literal132_tree);
                            }

                            pushFollow(FOLLOW_qualifiedNameList_in_methodDeclarationInner2094);
                            qualifiedNameList133=qualifiedNameList();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList133.getTree());

                            }
                            break;

                    }


                    // grammar/Java.g:569:9: ( block | ';' )
                    int alt46=2;
                    int LA46_0 = input.LA(1);

                    if ( (LA46_0==LBRACE) ) {
                        alt46=1;
                    }
                    else if ( (LA46_0==SEMI) ) {
                        alt46=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 46, 0, input);

                        throw nvae;

                    }
                    switch (alt46) {
                        case 1 :
                            // grammar/Java.g:570:13: block
                            {
                            pushFollow(FOLLOW_block_in_methodDeclarationInner2129);
                            block134=block();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, block134.getTree());

                            }
                            break;
                        case 2 :
                            // grammar/Java.g:571:13: ';'
                            {
                            char_literal135=(Token)match(input,SEMI,FOLLOW_SEMI_in_methodDeclarationInner2143); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal135_tree = 
                            (CommonTree)adaptor.create(char_literal135)
                            ;
                            adaptor.addChild(root_0, char_literal135_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 31, methodDeclarationInner_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "methodDeclarationInner"


    public static class methodRetValue_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "methodRetValue"
    // grammar/Java.g:575:1: methodRetValue : (| methodRetValueInner -> ^( METHOD_RET_VAL_T methodRetValueInner ) );
    public final JavaParser.methodRetValue_return methodRetValue() throws RecognitionException {
        JavaParser.methodRetValue_return retval = new JavaParser.methodRetValue_return();
        retval.start = input.LT(1);

        int methodRetValue_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.methodRetValueInner_return methodRetValueInner136 =null;


        RewriteRuleSubtreeStream stream_methodRetValueInner=new RewriteRuleSubtreeStream(adaptor,"rule methodRetValueInner");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 32) ) { return retval; }

            // grammar/Java.g:576:2: (| methodRetValueInner -> ^( METHOD_RET_VAL_T methodRetValueInner ) )
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==IDENTIFIER) ) {
                int LA48_1 = input.LA(2);

                if ( (LA48_1==LPAREN) ) {
                    alt48=1;
                }
                else if ( (LA48_1==DOT||LA48_1==IDENTIFIER||LA48_1==LBRACKET||LA48_1==LT) ) {
                    alt48=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 1, input);

                    throw nvae;

                }
            }
            else if ( (LA48_0==BOOLEAN||LA48_0==BYTE||LA48_0==CHAR||LA48_0==DOUBLE||LA48_0==FLOAT||LA48_0==INT||LA48_0==LONG||LA48_0==SHORT||LA48_0==VOID) ) {
                alt48=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 48, 0, input);

                throw nvae;

            }
            switch (alt48) {
                case 1 :
                    // grammar/Java.g:577:2: 
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    }
                    break;
                case 2 :
                    // grammar/Java.g:577:4: methodRetValueInner
                    {
                    pushFollow(FOLLOW_methodRetValueInner_in_methodRetValue2170);
                    methodRetValueInner136=methodRetValueInner();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_methodRetValueInner.add(methodRetValueInner136.getTree());

                    // AST REWRITE
                    // elements: methodRetValueInner
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 577:24: -> ^( METHOD_RET_VAL_T methodRetValueInner )
                    {
                        // grammar/Java.g:577:27: ^( METHOD_RET_VAL_T methodRetValueInner )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(METHOD_RET_VAL_T, "METHOD_RET_VAL_T")
                        , root_1);

                        adaptor.addChild(root_1, stream_methodRetValueInner.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 32, methodRetValue_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "methodRetValue"


    public static class methodRetValueInner_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "methodRetValueInner"
    // grammar/Java.g:580:1: methodRetValueInner : ( type | 'void' );
    public final JavaParser.methodRetValueInner_return methodRetValueInner() throws RecognitionException {
        JavaParser.methodRetValueInner_return retval = new JavaParser.methodRetValueInner_return();
        retval.start = input.LT(1);

        int methodRetValueInner_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal138=null;
        JavaParser.type_return type137 =null;


        CommonTree string_literal138_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 33) ) { return retval; }

            // grammar/Java.g:581:5: ( type | 'void' )
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==BOOLEAN||LA49_0==BYTE||LA49_0==CHAR||LA49_0==DOUBLE||LA49_0==FLOAT||LA49_0==IDENTIFIER||LA49_0==INT||LA49_0==LONG||LA49_0==SHORT) ) {
                alt49=1;
            }
            else if ( (LA49_0==VOID) ) {
                alt49=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 49, 0, input);

                throw nvae;

            }
            switch (alt49) {
                case 1 :
                    // grammar/Java.g:581:6: type
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_type_in_methodRetValueInner2192);
                    type137=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type137.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:582:7: 'void'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal138=(Token)match(input,VOID,FOLLOW_VOID_in_methodRetValueInner2200); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal138_tree = 
                    (CommonTree)adaptor.create(string_literal138)
                    ;
                    adaptor.addChild(root_0, string_literal138_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 33, methodRetValueInner_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "methodRetValueInner"


    public static class fieldDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "fieldDeclaration"
    // grammar/Java.g:586:1: fieldDeclaration : fieldDeclarationInner -> ^( VARIABLE_T fieldDeclarationInner ) ;
    public final JavaParser.fieldDeclaration_return fieldDeclaration() throws RecognitionException {
        JavaParser.fieldDeclaration_return retval = new JavaParser.fieldDeclaration_return();
        retval.start = input.LT(1);

        int fieldDeclaration_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.fieldDeclarationInner_return fieldDeclarationInner139 =null;


        RewriteRuleSubtreeStream stream_fieldDeclarationInner=new RewriteRuleSubtreeStream(adaptor,"rule fieldDeclarationInner");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 34) ) { return retval; }

            // grammar/Java.g:587:2: ( fieldDeclarationInner -> ^( VARIABLE_T fieldDeclarationInner ) )
            // grammar/Java.g:587:3: fieldDeclarationInner
            {
            pushFollow(FOLLOW_fieldDeclarationInner_in_fieldDeclaration2212);
            fieldDeclarationInner139=fieldDeclarationInner();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_fieldDeclarationInner.add(fieldDeclarationInner139.getTree());

            // AST REWRITE
            // elements: fieldDeclarationInner
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 587:25: -> ^( VARIABLE_T fieldDeclarationInner )
            {
                // grammar/Java.g:587:28: ^( VARIABLE_T fieldDeclarationInner )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(VARIABLE_T, "VARIABLE_T")
                , root_1);

                adaptor.addChild(root_1, stream_fieldDeclarationInner.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 34, fieldDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "fieldDeclaration"


    public static class fieldDeclarationInner_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "fieldDeclarationInner"
    // grammar/Java.g:592:1: fieldDeclarationInner : modifiers type variableDeclarator ( ',' variableDeclarator )* ';' ;
    public final JavaParser.fieldDeclarationInner_return fieldDeclarationInner() throws RecognitionException {
        JavaParser.fieldDeclarationInner_return retval = new JavaParser.fieldDeclarationInner_return();
        retval.start = input.LT(1);

        int fieldDeclarationInner_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal143=null;
        Token char_literal145=null;
        JavaParser.modifiers_return modifiers140 =null;

        JavaParser.type_return type141 =null;

        JavaParser.variableDeclarator_return variableDeclarator142 =null;

        JavaParser.variableDeclarator_return variableDeclarator144 =null;


        CommonTree char_literal143_tree=null;
        CommonTree char_literal145_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 35) ) { return retval; }

            // grammar/Java.g:593:5: ( modifiers type variableDeclarator ( ',' variableDeclarator )* ';' )
            // grammar/Java.g:593:9: modifiers type variableDeclarator ( ',' variableDeclarator )* ';'
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_fieldDeclarationInner2240);
            modifiers140=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers140.getTree());

            pushFollow(FOLLOW_type_in_fieldDeclarationInner2250);
            type141=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type141.getTree());

            pushFollow(FOLLOW_variableDeclarator_in_fieldDeclarationInner2260);
            variableDeclarator142=variableDeclarator();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarator142.getTree());

            // grammar/Java.g:596:9: ( ',' variableDeclarator )*
            loop50:
            do {
                int alt50=2;
                int LA50_0 = input.LA(1);

                if ( (LA50_0==COMMA) ) {
                    alt50=1;
                }


                switch (alt50) {
            	case 1 :
            	    // grammar/Java.g:596:10: ',' variableDeclarator
            	    {
            	    char_literal143=(Token)match(input,COMMA,FOLLOW_COMMA_in_fieldDeclarationInner2271); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal143_tree = 
            	    (CommonTree)adaptor.create(char_literal143)
            	    ;
            	    adaptor.addChild(root_0, char_literal143_tree);
            	    }

            	    pushFollow(FOLLOW_variableDeclarator_in_fieldDeclarationInner2273);
            	    variableDeclarator144=variableDeclarator();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarator144.getTree());

            	    }
            	    break;

            	default :
            	    break loop50;
                }
            } while (true);


            char_literal145=(Token)match(input,SEMI,FOLLOW_SEMI_in_fieldDeclarationInner2294); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal145_tree = 
            (CommonTree)adaptor.create(char_literal145)
            ;
            adaptor.addChild(root_0, char_literal145_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 35, fieldDeclarationInner_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "fieldDeclarationInner"


    public static class variableDeclarator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "variableDeclarator"
    // grammar/Java.g:601:1: variableDeclarator : navigableIdentifier ( '[' ']' )* ( '=' variableInitializer )? ;
    public final JavaParser.variableDeclarator_return variableDeclarator() throws RecognitionException {
        JavaParser.variableDeclarator_return retval = new JavaParser.variableDeclarator_return();
        retval.start = input.LT(1);

        int variableDeclarator_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal147=null;
        Token char_literal148=null;
        Token char_literal149=null;
        JavaParser.navigableIdentifier_return navigableIdentifier146 =null;

        JavaParser.variableInitializer_return variableInitializer150 =null;


        CommonTree char_literal147_tree=null;
        CommonTree char_literal148_tree=null;
        CommonTree char_literal149_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 36) ) { return retval; }

            // grammar/Java.g:602:5: ( navigableIdentifier ( '[' ']' )* ( '=' variableInitializer )? )
            // grammar/Java.g:602:9: navigableIdentifier ( '[' ']' )* ( '=' variableInitializer )?
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_navigableIdentifier_in_variableDeclarator2313);
            navigableIdentifier146=navigableIdentifier();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, navigableIdentifier146.getTree());

            // grammar/Java.g:603:9: ( '[' ']' )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);

                if ( (LA51_0==LBRACKET) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // grammar/Java.g:603:10: '[' ']'
            	    {
            	    char_literal147=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_variableDeclarator2324); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal147_tree = 
            	    (CommonTree)adaptor.create(char_literal147)
            	    ;
            	    adaptor.addChild(root_0, char_literal147_tree);
            	    }

            	    char_literal148=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_variableDeclarator2326); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal148_tree = 
            	    (CommonTree)adaptor.create(char_literal148)
            	    ;
            	    adaptor.addChild(root_0, char_literal148_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop51;
                }
            } while (true);


            // grammar/Java.g:605:9: ( '=' variableInitializer )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==EQ) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // grammar/Java.g:605:10: '=' variableInitializer
                    {
                    char_literal149=(Token)match(input,EQ,FOLLOW_EQ_in_variableDeclarator2348); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal149_tree = 
                    (CommonTree)adaptor.create(char_literal149)
                    ;
                    adaptor.addChild(root_0, char_literal149_tree);
                    }

                    pushFollow(FOLLOW_variableInitializer_in_variableDeclarator2350);
                    variableInitializer150=variableInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer150.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 36, variableDeclarator_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "variableDeclarator"


    public static class interfaceBodyDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "interfaceBodyDeclaration"
    // grammar/Java.g:612:1: interfaceBodyDeclaration : ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' );
    public final JavaParser.interfaceBodyDeclaration_return interfaceBodyDeclaration() throws RecognitionException {
        JavaParser.interfaceBodyDeclaration_return retval = new JavaParser.interfaceBodyDeclaration_return();
        retval.start = input.LT(1);

        int interfaceBodyDeclaration_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal155=null;
        JavaParser.interfaceFieldDeclaration_return interfaceFieldDeclaration151 =null;

        JavaParser.interfaceMethodDeclaration_return interfaceMethodDeclaration152 =null;

        JavaParser.interfaceDeclaration_return interfaceDeclaration153 =null;

        JavaParser.classDeclaration_return classDeclaration154 =null;


        CommonTree char_literal155_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 37) ) { return retval; }

            // grammar/Java.g:613:5: ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' )
            int alt53=5;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA53_1 = input.LA(2);

                if ( (synpred68_Java()) ) {
                    alt53=1;
                }
                else if ( (synpred69_Java()) ) {
                    alt53=2;
                }
                else if ( (synpred70_Java()) ) {
                    alt53=3;
                }
                else if ( (synpred71_Java()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 53, 1, input);

                    throw nvae;

                }
                }
                break;
            case PUBLIC:
                {
                int LA53_2 = input.LA(2);

                if ( (synpred68_Java()) ) {
                    alt53=1;
                }
                else if ( (synpred69_Java()) ) {
                    alt53=2;
                }
                else if ( (synpred70_Java()) ) {
                    alt53=3;
                }
                else if ( (synpred71_Java()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 53, 2, input);

                    throw nvae;

                }
                }
                break;
            case PROTECTED:
                {
                int LA53_3 = input.LA(2);

                if ( (synpred68_Java()) ) {
                    alt53=1;
                }
                else if ( (synpred69_Java()) ) {
                    alt53=2;
                }
                else if ( (synpred70_Java()) ) {
                    alt53=3;
                }
                else if ( (synpred71_Java()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 53, 3, input);

                    throw nvae;

                }
                }
                break;
            case PRIVATE:
                {
                int LA53_4 = input.LA(2);

                if ( (synpred68_Java()) ) {
                    alt53=1;
                }
                else if ( (synpred69_Java()) ) {
                    alt53=2;
                }
                else if ( (synpred70_Java()) ) {
                    alt53=3;
                }
                else if ( (synpred71_Java()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 53, 4, input);

                    throw nvae;

                }
                }
                break;
            case STATIC:
                {
                int LA53_5 = input.LA(2);

                if ( (synpred68_Java()) ) {
                    alt53=1;
                }
                else if ( (synpred69_Java()) ) {
                    alt53=2;
                }
                else if ( (synpred70_Java()) ) {
                    alt53=3;
                }
                else if ( (synpred71_Java()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 53, 5, input);

                    throw nvae;

                }
                }
                break;
            case ABSTRACT:
                {
                int LA53_6 = input.LA(2);

                if ( (synpred68_Java()) ) {
                    alt53=1;
                }
                else if ( (synpred69_Java()) ) {
                    alt53=2;
                }
                else if ( (synpred70_Java()) ) {
                    alt53=3;
                }
                else if ( (synpred71_Java()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 53, 6, input);

                    throw nvae;

                }
                }
                break;
            case FINAL:
                {
                int LA53_7 = input.LA(2);

                if ( (synpred68_Java()) ) {
                    alt53=1;
                }
                else if ( (synpred69_Java()) ) {
                    alt53=2;
                }
                else if ( (synpred70_Java()) ) {
                    alt53=3;
                }
                else if ( (synpred71_Java()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 53, 7, input);

                    throw nvae;

                }
                }
                break;
            case NATIVE:
                {
                int LA53_8 = input.LA(2);

                if ( (synpred68_Java()) ) {
                    alt53=1;
                }
                else if ( (synpred69_Java()) ) {
                    alt53=2;
                }
                else if ( (synpred70_Java()) ) {
                    alt53=3;
                }
                else if ( (synpred71_Java()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 53, 8, input);

                    throw nvae;

                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA53_9 = input.LA(2);

                if ( (synpred68_Java()) ) {
                    alt53=1;
                }
                else if ( (synpred69_Java()) ) {
                    alt53=2;
                }
                else if ( (synpred70_Java()) ) {
                    alt53=3;
                }
                else if ( (synpred71_Java()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 53, 9, input);

                    throw nvae;

                }
                }
                break;
            case TRANSIENT:
                {
                int LA53_10 = input.LA(2);

                if ( (synpred68_Java()) ) {
                    alt53=1;
                }
                else if ( (synpred69_Java()) ) {
                    alt53=2;
                }
                else if ( (synpred70_Java()) ) {
                    alt53=3;
                }
                else if ( (synpred71_Java()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 53, 10, input);

                    throw nvae;

                }
                }
                break;
            case VOLATILE:
                {
                int LA53_11 = input.LA(2);

                if ( (synpred68_Java()) ) {
                    alt53=1;
                }
                else if ( (synpred69_Java()) ) {
                    alt53=2;
                }
                else if ( (synpred70_Java()) ) {
                    alt53=3;
                }
                else if ( (synpred71_Java()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 53, 11, input);

                    throw nvae;

                }
                }
                break;
            case STRICTFP:
                {
                int LA53_12 = input.LA(2);

                if ( (synpred68_Java()) ) {
                    alt53=1;
                }
                else if ( (synpred69_Java()) ) {
                    alt53=2;
                }
                else if ( (synpred70_Java()) ) {
                    alt53=3;
                }
                else if ( (synpred71_Java()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 53, 12, input);

                    throw nvae;

                }
                }
                break;
            case IDENTIFIER:
                {
                int LA53_13 = input.LA(2);

                if ( (synpred68_Java()) ) {
                    alt53=1;
                }
                else if ( (synpred69_Java()) ) {
                    alt53=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 53, 13, input);

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

                if ( (synpred68_Java()) ) {
                    alt53=1;
                }
                else if ( (synpred69_Java()) ) {
                    alt53=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 53, 14, input);

                    throw nvae;

                }
                }
                break;
            case LT:
            case VOID:
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
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 53, 0, input);

                throw nvae;

            }

            switch (alt53) {
                case 1 :
                    // grammar/Java.g:614:9: interfaceFieldDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_interfaceFieldDeclaration_in_interfaceBodyDeclaration2388);
                    interfaceFieldDeclaration151=interfaceFieldDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceFieldDeclaration151.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:615:9: interfaceMethodDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_interfaceMethodDeclaration_in_interfaceBodyDeclaration2398);
                    interfaceMethodDeclaration152=interfaceMethodDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceMethodDeclaration152.getTree());

                    }
                    break;
                case 3 :
                    // grammar/Java.g:616:9: interfaceDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_interfaceDeclaration_in_interfaceBodyDeclaration2408);
                    interfaceDeclaration153=interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceDeclaration153.getTree());

                    }
                    break;
                case 4 :
                    // grammar/Java.g:617:9: classDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_classDeclaration_in_interfaceBodyDeclaration2418);
                    classDeclaration154=classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classDeclaration154.getTree());

                    }
                    break;
                case 5 :
                    // grammar/Java.g:618:9: ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal155=(Token)match(input,SEMI,FOLLOW_SEMI_in_interfaceBodyDeclaration2428); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal155_tree = 
                    (CommonTree)adaptor.create(char_literal155)
                    ;
                    adaptor.addChild(root_0, char_literal155_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 37, interfaceBodyDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "interfaceBodyDeclaration"


    public static class interfaceMethodDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "interfaceMethodDeclaration"
    // grammar/Java.g:621:1: interfaceMethodDeclaration : modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' ;
    public final JavaParser.interfaceMethodDeclaration_return interfaceMethodDeclaration() throws RecognitionException {
        JavaParser.interfaceMethodDeclaration_return retval = new JavaParser.interfaceMethodDeclaration_return();
        retval.start = input.LT(1);

        int interfaceMethodDeclaration_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal159=null;
        Token IDENTIFIER160=null;
        Token char_literal162=null;
        Token char_literal163=null;
        Token string_literal164=null;
        Token char_literal166=null;
        JavaParser.modifiers_return modifiers156 =null;

        JavaParser.typeParameters_return typeParameters157 =null;

        JavaParser.type_return type158 =null;

        JavaParser.formalParameters_return formalParameters161 =null;

        JavaParser.qualifiedNameList_return qualifiedNameList165 =null;


        CommonTree string_literal159_tree=null;
        CommonTree IDENTIFIER160_tree=null;
        CommonTree char_literal162_tree=null;
        CommonTree char_literal163_tree=null;
        CommonTree string_literal164_tree=null;
        CommonTree char_literal166_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 38) ) { return retval; }

            // grammar/Java.g:622:5: ( modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' )
            // grammar/Java.g:622:9: modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';'
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_interfaceMethodDeclaration2447);
            modifiers156=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers156.getTree());

            // grammar/Java.g:623:9: ( typeParameters )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==LT) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // grammar/Java.g:623:10: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_interfaceMethodDeclaration2458);
                    typeParameters157=typeParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters157.getTree());

                    }
                    break;

            }


            // grammar/Java.g:625:9: ( type | 'void' )
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==BOOLEAN||LA55_0==BYTE||LA55_0==CHAR||LA55_0==DOUBLE||LA55_0==FLOAT||LA55_0==IDENTIFIER||LA55_0==INT||LA55_0==LONG||LA55_0==SHORT) ) {
                alt55=1;
            }
            else if ( (LA55_0==VOID) ) {
                alt55=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;

            }
            switch (alt55) {
                case 1 :
                    // grammar/Java.g:625:10: type
                    {
                    pushFollow(FOLLOW_type_in_interfaceMethodDeclaration2480);
                    type158=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type158.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:626:10: 'void'
                    {
                    string_literal159=(Token)match(input,VOID,FOLLOW_VOID_in_interfaceMethodDeclaration2491); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal159_tree = 
                    (CommonTree)adaptor.create(string_literal159)
                    ;
                    adaptor.addChild(root_0, string_literal159_tree);
                    }

                    }
                    break;

            }


            IDENTIFIER160=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_interfaceMethodDeclaration2511); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER160_tree = 
            (CommonTree)adaptor.create(IDENTIFIER160)
            ;
            adaptor.addChild(root_0, IDENTIFIER160_tree);
            }

            pushFollow(FOLLOW_formalParameters_in_interfaceMethodDeclaration2521);
            formalParameters161=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters161.getTree());

            // grammar/Java.g:630:9: ( '[' ']' )*
            loop56:
            do {
                int alt56=2;
                int LA56_0 = input.LA(1);

                if ( (LA56_0==LBRACKET) ) {
                    alt56=1;
                }


                switch (alt56) {
            	case 1 :
            	    // grammar/Java.g:630:10: '[' ']'
            	    {
            	    char_literal162=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_interfaceMethodDeclaration2532); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal162_tree = 
            	    (CommonTree)adaptor.create(char_literal162)
            	    ;
            	    adaptor.addChild(root_0, char_literal162_tree);
            	    }

            	    char_literal163=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_interfaceMethodDeclaration2534); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal163_tree = 
            	    (CommonTree)adaptor.create(char_literal163)
            	    ;
            	    adaptor.addChild(root_0, char_literal163_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop56;
                }
            } while (true);


            // grammar/Java.g:632:9: ( 'throws' qualifiedNameList )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==THROWS) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // grammar/Java.g:632:10: 'throws' qualifiedNameList
                    {
                    string_literal164=(Token)match(input,THROWS,FOLLOW_THROWS_in_interfaceMethodDeclaration2556); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal164_tree = 
                    (CommonTree)adaptor.create(string_literal164)
                    ;
                    adaptor.addChild(root_0, string_literal164_tree);
                    }

                    pushFollow(FOLLOW_qualifiedNameList_in_interfaceMethodDeclaration2558);
                    qualifiedNameList165=qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList165.getTree());

                    }
                    break;

            }


            char_literal166=(Token)match(input,SEMI,FOLLOW_SEMI_in_interfaceMethodDeclaration2571); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal166_tree = 
            (CommonTree)adaptor.create(char_literal166)
            ;
            adaptor.addChild(root_0, char_literal166_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 38, interfaceMethodDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "interfaceMethodDeclaration"


    public static class interfaceFieldDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "interfaceFieldDeclaration"
    // grammar/Java.g:641:1: interfaceFieldDeclaration : modifiers type variableDeclarator ( ',' variableDeclarator )* ';' ;
    public final JavaParser.interfaceFieldDeclaration_return interfaceFieldDeclaration() throws RecognitionException {
        JavaParser.interfaceFieldDeclaration_return retval = new JavaParser.interfaceFieldDeclaration_return();
        retval.start = input.LT(1);

        int interfaceFieldDeclaration_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal170=null;
        Token char_literal172=null;
        JavaParser.modifiers_return modifiers167 =null;

        JavaParser.type_return type168 =null;

        JavaParser.variableDeclarator_return variableDeclarator169 =null;

        JavaParser.variableDeclarator_return variableDeclarator171 =null;


        CommonTree char_literal170_tree=null;
        CommonTree char_literal172_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 39) ) { return retval; }

            // grammar/Java.g:642:5: ( modifiers type variableDeclarator ( ',' variableDeclarator )* ';' )
            // grammar/Java.g:642:9: modifiers type variableDeclarator ( ',' variableDeclarator )* ';'
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_interfaceFieldDeclaration2592);
            modifiers167=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers167.getTree());

            pushFollow(FOLLOW_type_in_interfaceFieldDeclaration2594);
            type168=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type168.getTree());

            pushFollow(FOLLOW_variableDeclarator_in_interfaceFieldDeclaration2596);
            variableDeclarator169=variableDeclarator();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarator169.getTree());

            // grammar/Java.g:643:9: ( ',' variableDeclarator )*
            loop58:
            do {
                int alt58=2;
                int LA58_0 = input.LA(1);

                if ( (LA58_0==COMMA) ) {
                    alt58=1;
                }


                switch (alt58) {
            	case 1 :
            	    // grammar/Java.g:643:10: ',' variableDeclarator
            	    {
            	    char_literal170=(Token)match(input,COMMA,FOLLOW_COMMA_in_interfaceFieldDeclaration2607); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal170_tree = 
            	    (CommonTree)adaptor.create(char_literal170)
            	    ;
            	    adaptor.addChild(root_0, char_literal170_tree);
            	    }

            	    pushFollow(FOLLOW_variableDeclarator_in_interfaceFieldDeclaration2609);
            	    variableDeclarator171=variableDeclarator();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarator171.getTree());

            	    }
            	    break;

            	default :
            	    break loop58;
                }
            } while (true);


            char_literal172=(Token)match(input,SEMI,FOLLOW_SEMI_in_interfaceFieldDeclaration2630); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal172_tree = 
            (CommonTree)adaptor.create(char_literal172)
            ;
            adaptor.addChild(root_0, char_literal172_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 39, interfaceFieldDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "interfaceFieldDeclaration"


    public static class type_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "type"
    // grammar/Java.g:649:1: type : ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* );
    public final JavaParser.type_return type() throws RecognitionException {
        JavaParser.type_return retval = new JavaParser.type_return();
        retval.start = input.LT(1);

        int type_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal174=null;
        Token char_literal175=null;
        Token char_literal177=null;
        Token char_literal178=null;
        JavaParser.classOrInterfaceType_return classOrInterfaceType173 =null;

        JavaParser.primitiveType_return primitiveType176 =null;


        CommonTree char_literal174_tree=null;
        CommonTree char_literal175_tree=null;
        CommonTree char_literal177_tree=null;
        CommonTree char_literal178_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 40) ) { return retval; }

            // grammar/Java.g:650:5: ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* )
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==IDENTIFIER) ) {
                alt61=1;
            }
            else if ( (LA61_0==BOOLEAN||LA61_0==BYTE||LA61_0==CHAR||LA61_0==DOUBLE||LA61_0==FLOAT||LA61_0==INT||LA61_0==LONG||LA61_0==SHORT) ) {
                alt61=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 61, 0, input);

                throw nvae;

            }
            switch (alt61) {
                case 1 :
                    // grammar/Java.g:650:9: classOrInterfaceType ( '[' ']' )*
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_classOrInterfaceType_in_type2650);
                    classOrInterfaceType173=classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceType173.getTree());

                    // grammar/Java.g:651:9: ( '[' ']' )*
                    loop59:
                    do {
                        int alt59=2;
                        int LA59_0 = input.LA(1);

                        if ( (LA59_0==LBRACKET) ) {
                            alt59=1;
                        }


                        switch (alt59) {
                    	case 1 :
                    	    // grammar/Java.g:651:10: '[' ']'
                    	    {
                    	    char_literal174=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_type2661); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal174_tree = 
                    	    (CommonTree)adaptor.create(char_literal174)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal174_tree);
                    	    }

                    	    char_literal175=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_type2663); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal175_tree = 
                    	    (CommonTree)adaptor.create(char_literal175)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal175_tree);
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
                    // grammar/Java.g:653:9: primitiveType ( '[' ']' )*
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_primitiveType_in_type2684);
                    primitiveType176=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType176.getTree());

                    // grammar/Java.g:654:9: ( '[' ']' )*
                    loop60:
                    do {
                        int alt60=2;
                        int LA60_0 = input.LA(1);

                        if ( (LA60_0==LBRACKET) ) {
                            alt60=1;
                        }


                        switch (alt60) {
                    	case 1 :
                    	    // grammar/Java.g:654:10: '[' ']'
                    	    {
                    	    char_literal177=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_type2695); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal177_tree = 
                    	    (CommonTree)adaptor.create(char_literal177)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal177_tree);
                    	    }

                    	    char_literal178=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_type2697); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal178_tree = 
                    	    (CommonTree)adaptor.create(char_literal178)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal178_tree);
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


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 40, type_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "type"


    public static class classOrInterfaceType_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "classOrInterfaceType"
    // grammar/Java.g:660:1: classOrInterfaceType : classOrInterfaceTypeInner -> ^( TYPE_NAME_T classOrInterfaceTypeInner ) ;
    public final JavaParser.classOrInterfaceType_return classOrInterfaceType() throws RecognitionException {
        JavaParser.classOrInterfaceType_return retval = new JavaParser.classOrInterfaceType_return();
        retval.start = input.LT(1);

        int classOrInterfaceType_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.classOrInterfaceTypeInner_return classOrInterfaceTypeInner179 =null;


        RewriteRuleSubtreeStream stream_classOrInterfaceTypeInner=new RewriteRuleSubtreeStream(adaptor,"rule classOrInterfaceTypeInner");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 41) ) { return retval; }

            // grammar/Java.g:661:2: ( classOrInterfaceTypeInner -> ^( TYPE_NAME_T classOrInterfaceTypeInner ) )
            // grammar/Java.g:661:4: classOrInterfaceTypeInner
            {
            pushFollow(FOLLOW_classOrInterfaceTypeInner_in_classOrInterfaceType2724);
            classOrInterfaceTypeInner179=classOrInterfaceTypeInner();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_classOrInterfaceTypeInner.add(classOrInterfaceTypeInner179.getTree());

            // AST REWRITE
            // elements: classOrInterfaceTypeInner
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 661:30: -> ^( TYPE_NAME_T classOrInterfaceTypeInner )
            {
                // grammar/Java.g:661:33: ^( TYPE_NAME_T classOrInterfaceTypeInner )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TYPE_NAME_T, "TYPE_NAME_T")
                , root_1);

                adaptor.addChild(root_1, stream_classOrInterfaceTypeInner.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 41, classOrInterfaceType_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "classOrInterfaceType"


    public static class classOrInterfaceTypeInner_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "classOrInterfaceTypeInner"
    // grammar/Java.g:666:1: classOrInterfaceTypeInner : IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )* ;
    public final JavaParser.classOrInterfaceTypeInner_return classOrInterfaceTypeInner() throws RecognitionException {
        JavaParser.classOrInterfaceTypeInner_return retval = new JavaParser.classOrInterfaceTypeInner_return();
        retval.start = input.LT(1);

        int classOrInterfaceTypeInner_StartIndex = input.index();

        CommonTree root_0 = null;

        Token IDENTIFIER180=null;
        Token char_literal182=null;
        Token IDENTIFIER183=null;
        JavaParser.typeArguments_return typeArguments181 =null;

        JavaParser.typeArguments_return typeArguments184 =null;


        CommonTree IDENTIFIER180_tree=null;
        CommonTree char_literal182_tree=null;
        CommonTree IDENTIFIER183_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 42) ) { return retval; }

            // grammar/Java.g:667:5: ( IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )* )
            // grammar/Java.g:667:9: IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )*
            {
            root_0 = (CommonTree)adaptor.nil();


            IDENTIFIER180=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classOrInterfaceTypeInner2752); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER180_tree = 
            (CommonTree)adaptor.create(IDENTIFIER180)
            ;
            adaptor.addChild(root_0, IDENTIFIER180_tree);
            }

            // grammar/Java.g:668:9: ( typeArguments )?
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==LT) ) {
                int LA62_1 = input.LA(2);

                if ( (LA62_1==BOOLEAN||LA62_1==BYTE||LA62_1==CHAR||LA62_1==DOUBLE||LA62_1==FLOAT||LA62_1==IDENTIFIER||LA62_1==INT||LA62_1==LONG||LA62_1==QUES||LA62_1==SHORT) ) {
                    alt62=1;
                }
            }
            switch (alt62) {
                case 1 :
                    // grammar/Java.g:668:10: typeArguments
                    {
                    pushFollow(FOLLOW_typeArguments_in_classOrInterfaceTypeInner2763);
                    typeArguments181=typeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments181.getTree());

                    }
                    break;

            }


            // grammar/Java.g:670:9: ( '.' IDENTIFIER ( typeArguments )? )*
            loop64:
            do {
                int alt64=2;
                int LA64_0 = input.LA(1);

                if ( (LA64_0==DOT) ) {
                    alt64=1;
                }


                switch (alt64) {
            	case 1 :
            	    // grammar/Java.g:670:10: '.' IDENTIFIER ( typeArguments )?
            	    {
            	    char_literal182=(Token)match(input,DOT,FOLLOW_DOT_in_classOrInterfaceTypeInner2785); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal182_tree = 
            	    (CommonTree)adaptor.create(char_literal182)
            	    ;
            	    adaptor.addChild(root_0, char_literal182_tree);
            	    }

            	    IDENTIFIER183=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classOrInterfaceTypeInner2787); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    IDENTIFIER183_tree = 
            	    (CommonTree)adaptor.create(IDENTIFIER183)
            	    ;
            	    adaptor.addChild(root_0, IDENTIFIER183_tree);
            	    }

            	    // grammar/Java.g:671:13: ( typeArguments )?
            	    int alt63=2;
            	    int LA63_0 = input.LA(1);

            	    if ( (LA63_0==LT) ) {
            	        int LA63_1 = input.LA(2);

            	        if ( (LA63_1==BOOLEAN||LA63_1==BYTE||LA63_1==CHAR||LA63_1==DOUBLE||LA63_1==FLOAT||LA63_1==IDENTIFIER||LA63_1==INT||LA63_1==LONG||LA63_1==QUES||LA63_1==SHORT) ) {
            	            alt63=1;
            	        }
            	    }
            	    switch (alt63) {
            	        case 1 :
            	            // grammar/Java.g:671:14: typeArguments
            	            {
            	            pushFollow(FOLLOW_typeArguments_in_classOrInterfaceTypeInner2802);
            	            typeArguments184=typeArguments();

            	            state._fsp--;
            	            if (state.failed) return retval;
            	            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments184.getTree());

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


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 42, classOrInterfaceTypeInner_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "classOrInterfaceTypeInner"


    public static class primitiveType_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "primitiveType"
    // grammar/Java.g:677:1: primitiveType : primitiveTypeInner -> ^( TYPE_NAME_T primitiveTypeInner ) ;
    public final JavaParser.primitiveType_return primitiveType() throws RecognitionException {
        JavaParser.primitiveType_return retval = new JavaParser.primitiveType_return();
        retval.start = input.LT(1);

        int primitiveType_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.primitiveTypeInner_return primitiveTypeInner185 =null;


        RewriteRuleSubtreeStream stream_primitiveTypeInner=new RewriteRuleSubtreeStream(adaptor,"rule primitiveTypeInner");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 43) ) { return retval; }

            // grammar/Java.g:678:2: ( primitiveTypeInner -> ^( TYPE_NAME_T primitiveTypeInner ) )
            // grammar/Java.g:678:3: primitiveTypeInner
            {
            pushFollow(FOLLOW_primitiveTypeInner_in_primitiveType2842);
            primitiveTypeInner185=primitiveTypeInner();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_primitiveTypeInner.add(primitiveTypeInner185.getTree());

            // AST REWRITE
            // elements: primitiveTypeInner
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 678:22: -> ^( TYPE_NAME_T primitiveTypeInner )
            {
                // grammar/Java.g:678:25: ^( TYPE_NAME_T primitiveTypeInner )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TYPE_NAME_T, "TYPE_NAME_T")
                , root_1);

                adaptor.addChild(root_1, stream_primitiveTypeInner.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 43, primitiveType_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "primitiveType"


    public static class primitiveTypeInner_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "primitiveTypeInner"
    // grammar/Java.g:681:1: primitiveTypeInner : ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' );
    public final JavaParser.primitiveTypeInner_return primitiveTypeInner() throws RecognitionException {
        JavaParser.primitiveTypeInner_return retval = new JavaParser.primitiveTypeInner_return();
        retval.start = input.LT(1);

        int primitiveTypeInner_StartIndex = input.index();

        CommonTree root_0 = null;

        Token set186=null;

        CommonTree set186_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 44) ) { return retval; }

            // grammar/Java.g:682:5: ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' )
            // grammar/Java.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set186=(Token)input.LT(1);

            if ( input.LA(1)==BOOLEAN||input.LA(1)==BYTE||input.LA(1)==CHAR||input.LA(1)==DOUBLE||input.LA(1)==FLOAT||input.LA(1)==INT||input.LA(1)==LONG||input.LA(1)==SHORT ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set186)
                );
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 44, primitiveTypeInner_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "primitiveTypeInner"


    public static class typeArguments_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "typeArguments"
    // grammar/Java.g:692:1: typeArguments : '<' typeArgument ( ',' typeArgument )* '>' ;
    public final JavaParser.typeArguments_return typeArguments() throws RecognitionException {
        JavaParser.typeArguments_return retval = new JavaParser.typeArguments_return();
        retval.start = input.LT(1);

        int typeArguments_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal187=null;
        Token char_literal189=null;
        Token char_literal191=null;
        JavaParser.typeArgument_return typeArgument188 =null;

        JavaParser.typeArgument_return typeArgument190 =null;


        CommonTree char_literal187_tree=null;
        CommonTree char_literal189_tree=null;
        CommonTree char_literal191_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 45) ) { return retval; }

            // grammar/Java.g:693:5: ( '<' typeArgument ( ',' typeArgument )* '>' )
            // grammar/Java.g:693:9: '<' typeArgument ( ',' typeArgument )* '>'
            {
            root_0 = (CommonTree)adaptor.nil();


            char_literal187=(Token)match(input,LT,FOLLOW_LT_in_typeArguments2955); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal187_tree = 
            (CommonTree)adaptor.create(char_literal187)
            ;
            adaptor.addChild(root_0, char_literal187_tree);
            }

            pushFollow(FOLLOW_typeArgument_in_typeArguments2957);
            typeArgument188=typeArgument();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArgument188.getTree());

            // grammar/Java.g:694:9: ( ',' typeArgument )*
            loop65:
            do {
                int alt65=2;
                int LA65_0 = input.LA(1);

                if ( (LA65_0==COMMA) ) {
                    alt65=1;
                }


                switch (alt65) {
            	case 1 :
            	    // grammar/Java.g:694:10: ',' typeArgument
            	    {
            	    char_literal189=(Token)match(input,COMMA,FOLLOW_COMMA_in_typeArguments2968); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal189_tree = 
            	    (CommonTree)adaptor.create(char_literal189)
            	    ;
            	    adaptor.addChild(root_0, char_literal189_tree);
            	    }

            	    pushFollow(FOLLOW_typeArgument_in_typeArguments2970);
            	    typeArgument190=typeArgument();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArgument190.getTree());

            	    }
            	    break;

            	default :
            	    break loop65;
                }
            } while (true);


            char_literal191=(Token)match(input,GT,FOLLOW_GT_in_typeArguments2991); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal191_tree = 
            (CommonTree)adaptor.create(char_literal191)
            ;
            adaptor.addChild(root_0, char_literal191_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 45, typeArguments_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "typeArguments"


    public static class typeArgument_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "typeArgument"
    // grammar/Java.g:699:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );
    public final JavaParser.typeArgument_return typeArgument() throws RecognitionException {
        JavaParser.typeArgument_return retval = new JavaParser.typeArgument_return();
        retval.start = input.LT(1);

        int typeArgument_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal193=null;
        Token set194=null;
        JavaParser.type_return type192 =null;

        JavaParser.type_return type195 =null;


        CommonTree char_literal193_tree=null;
        CommonTree set194_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 46) ) { return retval; }

            // grammar/Java.g:700:5: ( type | '?' ( ( 'extends' | 'super' ) type )? )
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==BOOLEAN||LA67_0==BYTE||LA67_0==CHAR||LA67_0==DOUBLE||LA67_0==FLOAT||LA67_0==IDENTIFIER||LA67_0==INT||LA67_0==LONG||LA67_0==SHORT) ) {
                alt67=1;
            }
            else if ( (LA67_0==QUES) ) {
                alt67=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 67, 0, input);

                throw nvae;

            }
            switch (alt67) {
                case 1 :
                    // grammar/Java.g:700:9: type
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_type_in_typeArgument3010);
                    type192=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type192.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:701:9: '?' ( ( 'extends' | 'super' ) type )?
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal193=(Token)match(input,QUES,FOLLOW_QUES_in_typeArgument3020); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal193_tree = 
                    (CommonTree)adaptor.create(char_literal193)
                    ;
                    adaptor.addChild(root_0, char_literal193_tree);
                    }

                    // grammar/Java.g:702:9: ( ( 'extends' | 'super' ) type )?
                    int alt66=2;
                    int LA66_0 = input.LA(1);

                    if ( (LA66_0==EXTENDS||LA66_0==SUPER) ) {
                        alt66=1;
                    }
                    switch (alt66) {
                        case 1 :
                            // grammar/Java.g:703:13: ( 'extends' | 'super' ) type
                            {
                            set194=(Token)input.LT(1);

                            if ( input.LA(1)==EXTENDS||input.LA(1)==SUPER ) {
                                input.consume();
                                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                                (CommonTree)adaptor.create(set194)
                                );
                                state.errorRecovery=false;
                                state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            pushFollow(FOLLOW_type_in_typeArgument3088);
                            type195=type();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, type195.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 46, typeArgument_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "typeArgument"


    public static class qualifiedNameList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "qualifiedNameList"
    // grammar/Java.g:710:1: qualifiedNameList : qualifiedName ( ',' qualifiedName )* ;
    public final JavaParser.qualifiedNameList_return qualifiedNameList() throws RecognitionException {
        JavaParser.qualifiedNameList_return retval = new JavaParser.qualifiedNameList_return();
        retval.start = input.LT(1);

        int qualifiedNameList_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal197=null;
        JavaParser.qualifiedName_return qualifiedName196 =null;

        JavaParser.qualifiedName_return qualifiedName198 =null;


        CommonTree char_literal197_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 47) ) { return retval; }

            // grammar/Java.g:711:5: ( qualifiedName ( ',' qualifiedName )* )
            // grammar/Java.g:711:9: qualifiedName ( ',' qualifiedName )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList3118);
            qualifiedName196=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName196.getTree());

            // grammar/Java.g:712:9: ( ',' qualifiedName )*
            loop68:
            do {
                int alt68=2;
                int LA68_0 = input.LA(1);

                if ( (LA68_0==COMMA) ) {
                    alt68=1;
                }


                switch (alt68) {
            	case 1 :
            	    // grammar/Java.g:712:10: ',' qualifiedName
            	    {
            	    char_literal197=(Token)match(input,COMMA,FOLLOW_COMMA_in_qualifiedNameList3129); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal197_tree = 
            	    (CommonTree)adaptor.create(char_literal197)
            	    ;
            	    adaptor.addChild(root_0, char_literal197_tree);
            	    }

            	    pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList3131);
            	    qualifiedName198=qualifiedName();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName198.getTree());

            	    }
            	    break;

            	default :
            	    break loop68;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 47, qualifiedNameList_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "qualifiedNameList"


    public static class formalParameters_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "formalParameters"
    // grammar/Java.g:716:1: formalParameters : '(' ( formalParameterDecls )? ')' ;
    public final JavaParser.formalParameters_return formalParameters() throws RecognitionException {
        JavaParser.formalParameters_return retval = new JavaParser.formalParameters_return();
        retval.start = input.LT(1);

        int formalParameters_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal199=null;
        Token char_literal201=null;
        JavaParser.formalParameterDecls_return formalParameterDecls200 =null;


        CommonTree char_literal199_tree=null;
        CommonTree char_literal201_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 48) ) { return retval; }

            // grammar/Java.g:717:5: ( '(' ( formalParameterDecls )? ')' )
            // grammar/Java.g:717:9: '(' ( formalParameterDecls )? ')'
            {
            root_0 = (CommonTree)adaptor.nil();


            char_literal199=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_formalParameters3161); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal199_tree = 
            (CommonTree)adaptor.create(char_literal199)
            ;
            adaptor.addChild(root_0, char_literal199_tree);
            }

            // grammar/Java.g:718:9: ( formalParameterDecls )?
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==EOF||LA69_0==BOOLEAN||LA69_0==BYTE||LA69_0==CHAR||LA69_0==DOUBLE||LA69_0==FINAL||LA69_0==FLOAT||LA69_0==IDENTIFIER||LA69_0==INT||LA69_0==LONG||LA69_0==MONKEYS_AT||LA69_0==SHORT) ) {
                alt69=1;
            }
            switch (alt69) {
                case 1 :
                    // grammar/Java.g:718:10: formalParameterDecls
                    {
                    pushFollow(FOLLOW_formalParameterDecls_in_formalParameters3172);
                    formalParameterDecls200=formalParameterDecls();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameterDecls200.getTree());

                    }
                    break;

            }


            char_literal201=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_formalParameters3193); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal201_tree = 
            (CommonTree)adaptor.create(char_literal201)
            ;
            adaptor.addChild(root_0, char_literal201_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 48, formalParameters_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "formalParameters"


    public static class formalParameterDecls_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "formalParameterDecls"
    // grammar/Java.g:723:1: formalParameterDecls : ( ellipsisParameterDecl | normalParameterDecl ( ',' normalParameterDecl )* | ( normalParameterDecl ',' )+ ellipsisParameterDecl );
    public final JavaParser.formalParameterDecls_return formalParameterDecls() throws RecognitionException {
        JavaParser.formalParameterDecls_return retval = new JavaParser.formalParameterDecls_return();
        retval.start = input.LT(1);

        int formalParameterDecls_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal204=null;
        Token char_literal207=null;
        JavaParser.ellipsisParameterDecl_return ellipsisParameterDecl202 =null;

        JavaParser.normalParameterDecl_return normalParameterDecl203 =null;

        JavaParser.normalParameterDecl_return normalParameterDecl205 =null;

        JavaParser.normalParameterDecl_return normalParameterDecl206 =null;

        JavaParser.ellipsisParameterDecl_return ellipsisParameterDecl208 =null;


        CommonTree char_literal204_tree=null;
        CommonTree char_literal207_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 49) ) { return retval; }

            // grammar/Java.g:724:5: ( ellipsisParameterDecl | normalParameterDecl ( ',' normalParameterDecl )* | ( normalParameterDecl ',' )+ ellipsisParameterDecl )
            int alt72=3;
            switch ( input.LA(1) ) {
            case FINAL:
                {
                int LA72_1 = input.LA(2);

                if ( (synpred96_Java()) ) {
                    alt72=1;
                }
                else if ( (synpred98_Java()) ) {
                    alt72=2;
                }
                else if ( (true) ) {
                    alt72=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 1, input);

                    throw nvae;

                }
                }
                break;
            case MONKEYS_AT:
                {
                int LA72_2 = input.LA(2);

                if ( (synpred96_Java()) ) {
                    alt72=1;
                }
                else if ( (synpred98_Java()) ) {
                    alt72=2;
                }
                else if ( (true) ) {
                    alt72=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 2, input);

                    throw nvae;

                }
                }
                break;
            case IDENTIFIER:
                {
                int LA72_3 = input.LA(2);

                if ( (synpred96_Java()) ) {
                    alt72=1;
                }
                else if ( (synpred98_Java()) ) {
                    alt72=2;
                }
                else if ( (true) ) {
                    alt72=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 3, input);

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

                if ( (synpred96_Java()) ) {
                    alt72=1;
                }
                else if ( (synpred98_Java()) ) {
                    alt72=2;
                }
                else if ( (true) ) {
                    alt72=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 4, input);

                    throw nvae;

                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 72, 0, input);

                throw nvae;

            }

            switch (alt72) {
                case 1 :
                    // grammar/Java.g:724:9: ellipsisParameterDecl
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_ellipsisParameterDecl_in_formalParameterDecls3212);
                    ellipsisParameterDecl202=ellipsisParameterDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ellipsisParameterDecl202.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:725:9: normalParameterDecl ( ',' normalParameterDecl )*
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_normalParameterDecl_in_formalParameterDecls3222);
                    normalParameterDecl203=normalParameterDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalParameterDecl203.getTree());

                    // grammar/Java.g:726:9: ( ',' normalParameterDecl )*
                    loop70:
                    do {
                        int alt70=2;
                        int LA70_0 = input.LA(1);

                        if ( (LA70_0==COMMA) ) {
                            alt70=1;
                        }


                        switch (alt70) {
                    	case 1 :
                    	    // grammar/Java.g:726:10: ',' normalParameterDecl
                    	    {
                    	    char_literal204=(Token)match(input,COMMA,FOLLOW_COMMA_in_formalParameterDecls3233); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal204_tree = 
                    	    (CommonTree)adaptor.create(char_literal204)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal204_tree);
                    	    }

                    	    pushFollow(FOLLOW_normalParameterDecl_in_formalParameterDecls3235);
                    	    normalParameterDecl205=normalParameterDecl();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalParameterDecl205.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop70;
                        }
                    } while (true);


                    }
                    break;
                case 3 :
                    // grammar/Java.g:728:9: ( normalParameterDecl ',' )+ ellipsisParameterDecl
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    // grammar/Java.g:728:9: ( normalParameterDecl ',' )+
                    int cnt71=0;
                    loop71:
                    do {
                        int alt71=2;
                        switch ( input.LA(1) ) {
                        case FINAL:
                            {
                            int LA71_1 = input.LA(2);

                            if ( (synpred99_Java()) ) {
                                alt71=1;
                            }


                            }
                            break;
                        case MONKEYS_AT:
                            {
                            int LA71_2 = input.LA(2);

                            if ( (synpred99_Java()) ) {
                                alt71=1;
                            }


                            }
                            break;
                        case IDENTIFIER:
                            {
                            int LA71_3 = input.LA(2);

                            if ( (synpred99_Java()) ) {
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

                            if ( (synpred99_Java()) ) {
                                alt71=1;
                            }


                            }
                            break;

                        }

                        switch (alt71) {
                    	case 1 :
                    	    // grammar/Java.g:728:10: normalParameterDecl ','
                    	    {
                    	    pushFollow(FOLLOW_normalParameterDecl_in_formalParameterDecls3257);
                    	    normalParameterDecl206=normalParameterDecl();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalParameterDecl206.getTree());

                    	    char_literal207=(Token)match(input,COMMA,FOLLOW_COMMA_in_formalParameterDecls3267); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal207_tree = 
                    	    (CommonTree)adaptor.create(char_literal207)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal207_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt71 >= 1 ) break loop71;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(71, input);
                                throw eee;
                        }
                        cnt71++;
                    } while (true);


                    pushFollow(FOLLOW_ellipsisParameterDecl_in_formalParameterDecls3288);
                    ellipsisParameterDecl208=ellipsisParameterDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ellipsisParameterDecl208.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 49, formalParameterDecls_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "formalParameterDecls"


    public static class normalParameterDecl_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "normalParameterDecl"
    // grammar/Java.g:734:1: normalParameterDecl : ( variableModifiers )? type IDENTIFIER ( '[' ']' )* -> ^( FUN_ARG_T ( variableModifiers )? type IDENTIFIER ( '[' ']' )* ) ;
    public final JavaParser.normalParameterDecl_return normalParameterDecl() throws RecognitionException {
        JavaParser.normalParameterDecl_return retval = new JavaParser.normalParameterDecl_return();
        retval.start = input.LT(1);

        int normalParameterDecl_StartIndex = input.index();

        CommonTree root_0 = null;

        Token IDENTIFIER211=null;
        Token char_literal212=null;
        Token char_literal213=null;
        JavaParser.variableModifiers_return variableModifiers209 =null;

        JavaParser.type_return type210 =null;


        CommonTree IDENTIFIER211_tree=null;
        CommonTree char_literal212_tree=null;
        CommonTree char_literal213_tree=null;
        RewriteRuleTokenStream stream_LBRACKET=new RewriteRuleTokenStream(adaptor,"token LBRACKET");
        RewriteRuleTokenStream stream_RBRACKET=new RewriteRuleTokenStream(adaptor,"token RBRACKET");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleSubtreeStream stream_variableModifiers=new RewriteRuleSubtreeStream(adaptor,"rule variableModifiers");
        RewriteRuleSubtreeStream stream_type=new RewriteRuleSubtreeStream(adaptor,"rule type");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 50) ) { return retval; }

            // grammar/Java.g:735:5: ( ( variableModifiers )? type IDENTIFIER ( '[' ']' )* -> ^( FUN_ARG_T ( variableModifiers )? type IDENTIFIER ( '[' ']' )* ) )
            // grammar/Java.g:735:9: ( variableModifiers )? type IDENTIFIER ( '[' ']' )*
            {
            // grammar/Java.g:735:9: ( variableModifiers )?
            int alt73=2;
            switch ( input.LA(1) ) {
                case FINAL:
                case MONKEYS_AT:
                    {
                    alt73=1;
                    }
                    break;
                case IDENTIFIER:
                    {
                    int LA73_2 = input.LA(2);

                    if ( (synpred100_Java()) ) {
                        alt73=1;
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
                    int LA73_3 = input.LA(2);

                    if ( (synpred100_Java()) ) {
                        alt73=1;
                    }
                    }
                    break;
            }

            switch (alt73) {
                case 1 :
                    // grammar/Java.g:735:9: variableModifiers
                    {
                    pushFollow(FOLLOW_variableModifiers_in_normalParameterDecl3307);
                    variableModifiers209=variableModifiers();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_variableModifiers.add(variableModifiers209.getTree());

                    }
                    break;

            }


            pushFollow(FOLLOW_type_in_normalParameterDecl3310);
            type210=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_type.add(type210.getTree());

            IDENTIFIER211=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_normalParameterDecl3312); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER211);


            // grammar/Java.g:736:9: ( '[' ']' )*
            loop74:
            do {
                int alt74=2;
                int LA74_0 = input.LA(1);

                if ( (LA74_0==LBRACKET) ) {
                    alt74=1;
                }


                switch (alt74) {
            	case 1 :
            	    // grammar/Java.g:736:10: '[' ']'
            	    {
            	    char_literal212=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_normalParameterDecl3323); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_LBRACKET.add(char_literal212);


            	    char_literal213=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_normalParameterDecl3325); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_RBRACKET.add(char_literal213);


            	    }
            	    break;

            	default :
            	    break loop74;
                }
            } while (true);


            // AST REWRITE
            // elements: variableModifiers, LBRACKET, type, RBRACKET, IDENTIFIER
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 737:13: -> ^( FUN_ARG_T ( variableModifiers )? type IDENTIFIER ( '[' ']' )* )
            {
                // grammar/Java.g:737:16: ^( FUN_ARG_T ( variableModifiers )? type IDENTIFIER ( '[' ']' )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(FUN_ARG_T, "FUN_ARG_T")
                , root_1);

                // grammar/Java.g:737:29: ( variableModifiers )?
                if ( stream_variableModifiers.hasNext() ) {
                    adaptor.addChild(root_1, stream_variableModifiers.nextTree());

                }
                stream_variableModifiers.reset();

                adaptor.addChild(root_1, stream_type.nextTree());

                adaptor.addChild(root_1, 
                stream_IDENTIFIER.nextNode()
                );

                // grammar/Java.g:738:38: ( '[' ']' )*
                while ( stream_LBRACKET.hasNext()||stream_RBRACKET.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_LBRACKET.nextNode()
                    );

                    adaptor.addChild(root_1, 
                    stream_RBRACKET.nextNode()
                    );

                }
                stream_LBRACKET.reset();
                stream_RBRACKET.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 50, normalParameterDecl_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "normalParameterDecl"


    public static class ellipsisParameterDecl_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "ellipsisParameterDecl"
    // grammar/Java.g:742:1: ellipsisParameterDecl : variableModifiers type '...' IDENTIFIER ;
    public final JavaParser.ellipsisParameterDecl_return ellipsisParameterDecl() throws RecognitionException {
        JavaParser.ellipsisParameterDecl_return retval = new JavaParser.ellipsisParameterDecl_return();
        retval.start = input.LT(1);

        int ellipsisParameterDecl_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal216=null;
        Token IDENTIFIER217=null;
        JavaParser.variableModifiers_return variableModifiers214 =null;

        JavaParser.type_return type215 =null;


        CommonTree string_literal216_tree=null;
        CommonTree IDENTIFIER217_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 51) ) { return retval; }

            // grammar/Java.g:743:5: ( variableModifiers type '...' IDENTIFIER )
            // grammar/Java.g:743:9: variableModifiers type '...' IDENTIFIER
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_variableModifiers_in_ellipsisParameterDecl3453);
            variableModifiers214=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers214.getTree());

            pushFollow(FOLLOW_type_in_ellipsisParameterDecl3463);
            type215=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type215.getTree());

            string_literal216=(Token)match(input,ELLIPSIS,FOLLOW_ELLIPSIS_in_ellipsisParameterDecl3466); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal216_tree = 
            (CommonTree)adaptor.create(string_literal216)
            ;
            adaptor.addChild(root_0, string_literal216_tree);
            }

            IDENTIFIER217=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_ellipsisParameterDecl3476); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER217_tree = 
            (CommonTree)adaptor.create(IDENTIFIER217)
            ;
            adaptor.addChild(root_0, IDENTIFIER217_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 51, ellipsisParameterDecl_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "ellipsisParameterDecl"


    public static class explicitConstructorInvocation_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "explicitConstructorInvocation"
    // grammar/Java.g:749:1: explicitConstructorInvocation : ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' );
    public final JavaParser.explicitConstructorInvocation_return explicitConstructorInvocation() throws RecognitionException {
        JavaParser.explicitConstructorInvocation_return retval = new JavaParser.explicitConstructorInvocation_return();
        retval.start = input.LT(1);

        int explicitConstructorInvocation_StartIndex = input.index();

        CommonTree root_0 = null;

        Token set219=null;
        Token char_literal221=null;
        Token char_literal223=null;
        Token string_literal225=null;
        Token char_literal227=null;
        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments218 =null;

        JavaParser.arguments_return arguments220 =null;

        JavaParser.primary_return primary222 =null;

        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments224 =null;

        JavaParser.arguments_return arguments226 =null;


        CommonTree set219_tree=null;
        CommonTree char_literal221_tree=null;
        CommonTree char_literal223_tree=null;
        CommonTree string_literal225_tree=null;
        CommonTree char_literal227_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 52) ) { return retval; }

            // grammar/Java.g:750:5: ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' )
            int alt77=2;
            switch ( input.LA(1) ) {
            case LT:
                {
                alt77=1;
                }
                break;
            case THIS:
                {
                int LA77_2 = input.LA(2);

                if ( (synpred104_Java()) ) {
                    alt77=1;
                }
                else if ( (true) ) {
                    alt77=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 77, 2, input);

                    throw nvae;

                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case CHARLITERAL:
            case DOUBLE:
            case DOUBLELITERAL:
            case FALSE:
            case FLOAT:
            case FLOATLITERAL:
            case IDENTIFIER:
            case INT:
            case INTLITERAL:
            case LONG:
            case LONGLITERAL:
            case LPAREN:
            case NEW:
            case NULL:
            case SHORT:
            case STRINGLITERAL:
            case TRUE:
            case VOID:
                {
                alt77=2;
                }
                break;
            case SUPER:
                {
                int LA77_4 = input.LA(2);

                if ( (synpred104_Java()) ) {
                    alt77=1;
                }
                else if ( (true) ) {
                    alt77=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 77, 4, input);

                    throw nvae;

                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 77, 0, input);

                throw nvae;

            }

            switch (alt77) {
                case 1 :
                    // grammar/Java.g:750:9: ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    // grammar/Java.g:750:9: ( nonWildcardTypeArguments )?
                    int alt75=2;
                    int LA75_0 = input.LA(1);

                    if ( (LA75_0==LT) ) {
                        alt75=1;
                    }
                    switch (alt75) {
                        case 1 :
                            // grammar/Java.g:750:10: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3497);
                            nonWildcardTypeArguments218=nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments218.getTree());

                            }
                            break;

                    }


                    set219=(Token)input.LT(1);

                    if ( input.LA(1)==SUPER||input.LA(1)==THIS ) {
                        input.consume();
                        if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                        (CommonTree)adaptor.create(set219)
                        );
                        state.errorRecovery=false;
                        state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation3555);
                    arguments220=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments220.getTree());

                    char_literal221=(Token)match(input,SEMI,FOLLOW_SEMI_in_explicitConstructorInvocation3557); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal221_tree = 
                    (CommonTree)adaptor.create(char_literal221)
                    ;
                    adaptor.addChild(root_0, char_literal221_tree);
                    }

                    }
                    break;
                case 2 :
                    // grammar/Java.g:757:9: primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_primary_in_explicitConstructorInvocation3568);
                    primary222=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primary222.getTree());

                    char_literal223=(Token)match(input,DOT,FOLLOW_DOT_in_explicitConstructorInvocation3578); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal223_tree = 
                    (CommonTree)adaptor.create(char_literal223)
                    ;
                    adaptor.addChild(root_0, char_literal223_tree);
                    }

                    // grammar/Java.g:759:9: ( nonWildcardTypeArguments )?
                    int alt76=2;
                    int LA76_0 = input.LA(1);

                    if ( (LA76_0==LT) ) {
                        alt76=1;
                    }
                    switch (alt76) {
                        case 1 :
                            // grammar/Java.g:759:10: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3589);
                            nonWildcardTypeArguments224=nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments224.getTree());

                            }
                            break;

                    }


                    string_literal225=(Token)match(input,SUPER,FOLLOW_SUPER_in_explicitConstructorInvocation3610); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal225_tree = 
                    (CommonTree)adaptor.create(string_literal225)
                    ;
                    adaptor.addChild(root_0, string_literal225_tree);
                    }

                    pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation3620);
                    arguments226=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments226.getTree());

                    char_literal227=(Token)match(input,SEMI,FOLLOW_SEMI_in_explicitConstructorInvocation3622); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal227_tree = 
                    (CommonTree)adaptor.create(char_literal227)
                    ;
                    adaptor.addChild(root_0, char_literal227_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 52, explicitConstructorInvocation_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "explicitConstructorInvocation"


    public static class qualifiedName_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "qualifiedName"
    // grammar/Java.g:766:1: qualifiedName : IDENTIFIER ( '.' IDENTIFIER )* ;
    public final JavaParser.qualifiedName_return qualifiedName() throws RecognitionException {
        JavaParser.qualifiedName_return retval = new JavaParser.qualifiedName_return();
        retval.start = input.LT(1);

        int qualifiedName_StartIndex = input.index();

        CommonTree root_0 = null;

        Token IDENTIFIER228=null;
        Token char_literal229=null;
        Token IDENTIFIER230=null;

        CommonTree IDENTIFIER228_tree=null;
        CommonTree char_literal229_tree=null;
        CommonTree IDENTIFIER230_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 53) ) { return retval; }

            // grammar/Java.g:767:5: ( IDENTIFIER ( '.' IDENTIFIER )* )
            // grammar/Java.g:767:9: IDENTIFIER ( '.' IDENTIFIER )*
            {
            root_0 = (CommonTree)adaptor.nil();


            IDENTIFIER228=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedName3642); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER228_tree = 
            (CommonTree)adaptor.create(IDENTIFIER228)
            ;
            adaptor.addChild(root_0, IDENTIFIER228_tree);
            }

            // grammar/Java.g:768:9: ( '.' IDENTIFIER )*
            loop78:
            do {
                int alt78=2;
                int LA78_0 = input.LA(1);

                if ( (LA78_0==DOT) ) {
                    alt78=1;
                }


                switch (alt78) {
            	case 1 :
            	    // grammar/Java.g:768:10: '.' IDENTIFIER
            	    {
            	    char_literal229=(Token)match(input,DOT,FOLLOW_DOT_in_qualifiedName3653); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal229_tree = 
            	    (CommonTree)adaptor.create(char_literal229)
            	    ;
            	    adaptor.addChild(root_0, char_literal229_tree);
            	    }

            	    IDENTIFIER230=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedName3655); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    IDENTIFIER230_tree = 
            	    (CommonTree)adaptor.create(IDENTIFIER230)
            	    ;
            	    adaptor.addChild(root_0, IDENTIFIER230_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop78;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 53, qualifiedName_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "qualifiedName"


    public static class annotations_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "annotations"
    // grammar/Java.g:772:1: annotations : ( annotation )+ ;
    public final JavaParser.annotations_return annotations() throws RecognitionException {
        JavaParser.annotations_return retval = new JavaParser.annotations_return();
        retval.start = input.LT(1);

        int annotations_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.annotation_return annotation231 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 54) ) { return retval; }

            // grammar/Java.g:773:5: ( ( annotation )+ )
            // grammar/Java.g:773:9: ( annotation )+
            {
            root_0 = (CommonTree)adaptor.nil();


            // grammar/Java.g:773:9: ( annotation )+
            int cnt79=0;
            loop79:
            do {
                int alt79=2;
                int LA79_0 = input.LA(1);

                if ( (LA79_0==MONKEYS_AT) ) {
                    alt79=1;
                }


                switch (alt79) {
            	case 1 :
            	    // grammar/Java.g:773:10: annotation
            	    {
            	    pushFollow(FOLLOW_annotation_in_annotations3686);
            	    annotation231=annotation();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation231.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt79 >= 1 ) break loop79;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(79, input);
                        throw eee;
                }
                cnt79++;
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 54, annotations_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "annotations"


    public static class annotation_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "annotation"
    // grammar/Java.g:781:1: annotation : '@' qualifiedName ( '(' ( elementValuePairs | elementValue )? ')' )? ;
    public final JavaParser.annotation_return annotation() throws RecognitionException {
        JavaParser.annotation_return retval = new JavaParser.annotation_return();
        retval.start = input.LT(1);

        int annotation_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal232=null;
        Token char_literal234=null;
        Token char_literal237=null;
        JavaParser.qualifiedName_return qualifiedName233 =null;

        JavaParser.elementValuePairs_return elementValuePairs235 =null;

        JavaParser.elementValue_return elementValue236 =null;


        CommonTree char_literal232_tree=null;
        CommonTree char_literal234_tree=null;
        CommonTree char_literal237_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 55) ) { return retval; }

            // grammar/Java.g:782:5: ( '@' qualifiedName ( '(' ( elementValuePairs | elementValue )? ')' )? )
            // grammar/Java.g:782:9: '@' qualifiedName ( '(' ( elementValuePairs | elementValue )? ')' )?
            {
            root_0 = (CommonTree)adaptor.nil();


            char_literal232=(Token)match(input,MONKEYS_AT,FOLLOW_MONKEYS_AT_in_annotation3718); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal232_tree = 
            (CommonTree)adaptor.create(char_literal232)
            ;
            adaptor.addChild(root_0, char_literal232_tree);
            }

            pushFollow(FOLLOW_qualifiedName_in_annotation3720);
            qualifiedName233=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName233.getTree());

            // grammar/Java.g:783:9: ( '(' ( elementValuePairs | elementValue )? ')' )?
            int alt81=2;
            int LA81_0 = input.LA(1);

            if ( (LA81_0==LPAREN) ) {
                alt81=1;
            }
            switch (alt81) {
                case 1 :
                    // grammar/Java.g:783:13: '(' ( elementValuePairs | elementValue )? ')'
                    {
                    char_literal234=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_annotation3734); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal234_tree = 
                    (CommonTree)adaptor.create(char_literal234)
                    ;
                    adaptor.addChild(root_0, char_literal234_tree);
                    }

                    // grammar/Java.g:784:19: ( elementValuePairs | elementValue )?
                    int alt80=3;
                    int LA80_0 = input.LA(1);

                    if ( (LA80_0==IDENTIFIER) ) {
                        int LA80_1 = input.LA(2);

                        if ( (LA80_1==EQ) ) {
                            alt80=1;
                        }
                        else if ( ((LA80_1 >= AMP && LA80_1 <= AMPAMP)||(LA80_1 >= BANGEQ && LA80_1 <= BARBAR)||LA80_1==CARET||LA80_1==DOT||LA80_1==EQEQ||LA80_1==GT||LA80_1==INSTANCEOF||LA80_1==LBRACKET||(LA80_1 >= LPAREN && LA80_1 <= LT)||LA80_1==PERCENT||LA80_1==PLUS||LA80_1==PLUSPLUS||LA80_1==QUES||LA80_1==RPAREN||LA80_1==SLASH||LA80_1==STAR||LA80_1==SUB||LA80_1==SUBSUB) ) {
                            alt80=2;
                        }
                    }
                    else if ( (LA80_0==BANG||LA80_0==BOOLEAN||LA80_0==BYTE||(LA80_0 >= CHAR && LA80_0 <= CHARLITERAL)||(LA80_0 >= DOUBLE && LA80_0 <= DOUBLELITERAL)||LA80_0==FALSE||(LA80_0 >= FLOAT && LA80_0 <= FLOATLITERAL)||LA80_0==INT||LA80_0==INTLITERAL||LA80_0==LBRACE||(LA80_0 >= LONG && LA80_0 <= LPAREN)||LA80_0==MONKEYS_AT||(LA80_0 >= NEW && LA80_0 <= NULL)||LA80_0==PLUS||LA80_0==PLUSPLUS||LA80_0==SHORT||(LA80_0 >= STRINGLITERAL && LA80_0 <= SUB)||(LA80_0 >= SUBSUB && LA80_0 <= SUPER)||LA80_0==THIS||LA80_0==TILDE||LA80_0==TRUE||LA80_0==VOID) ) {
                        alt80=2;
                    }
                    switch (alt80) {
                        case 1 :
                            // grammar/Java.g:784:23: elementValuePairs
                            {
                            pushFollow(FOLLOW_elementValuePairs_in_annotation3758);
                            elementValuePairs235=elementValuePairs();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValuePairs235.getTree());

                            }
                            break;
                        case 2 :
                            // grammar/Java.g:785:23: elementValue
                            {
                            pushFollow(FOLLOW_elementValue_in_annotation3782);
                            elementValue236=elementValue();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue236.getTree());

                            }
                            break;

                    }


                    char_literal237=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_annotation3817); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal237_tree = 
                    (CommonTree)adaptor.create(char_literal237)
                    ;
                    adaptor.addChild(root_0, char_literal237_tree);
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 55, annotation_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "annotation"


    public static class elementValuePairs_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "elementValuePairs"
    // grammar/Java.g:791:1: elementValuePairs : elementValuePair ( ',' elementValuePair )* ;
    public final JavaParser.elementValuePairs_return elementValuePairs() throws RecognitionException {
        JavaParser.elementValuePairs_return retval = new JavaParser.elementValuePairs_return();
        retval.start = input.LT(1);

        int elementValuePairs_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal239=null;
        JavaParser.elementValuePair_return elementValuePair238 =null;

        JavaParser.elementValuePair_return elementValuePair240 =null;


        CommonTree char_literal239_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 56) ) { return retval; }

            // grammar/Java.g:792:5: ( elementValuePair ( ',' elementValuePair )* )
            // grammar/Java.g:792:9: elementValuePair ( ',' elementValuePair )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_elementValuePair_in_elementValuePairs3847);
            elementValuePair238=elementValuePair();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValuePair238.getTree());

            // grammar/Java.g:793:9: ( ',' elementValuePair )*
            loop82:
            do {
                int alt82=2;
                int LA82_0 = input.LA(1);

                if ( (LA82_0==COMMA) ) {
                    alt82=1;
                }


                switch (alt82) {
            	case 1 :
            	    // grammar/Java.g:793:10: ',' elementValuePair
            	    {
            	    char_literal239=(Token)match(input,COMMA,FOLLOW_COMMA_in_elementValuePairs3858); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal239_tree = 
            	    (CommonTree)adaptor.create(char_literal239)
            	    ;
            	    adaptor.addChild(root_0, char_literal239_tree);
            	    }

            	    pushFollow(FOLLOW_elementValuePair_in_elementValuePairs3860);
            	    elementValuePair240=elementValuePair();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValuePair240.getTree());

            	    }
            	    break;

            	default :
            	    break loop82;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 56, elementValuePairs_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "elementValuePairs"


    public static class elementValuePair_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "elementValuePair"
    // grammar/Java.g:797:1: elementValuePair : navigableIdentifier '=' elementValue ;
    public final JavaParser.elementValuePair_return elementValuePair() throws RecognitionException {
        JavaParser.elementValuePair_return retval = new JavaParser.elementValuePair_return();
        retval.start = input.LT(1);

        int elementValuePair_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal242=null;
        JavaParser.navigableIdentifier_return navigableIdentifier241 =null;

        JavaParser.elementValue_return elementValue243 =null;


        CommonTree char_literal242_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 57) ) { return retval; }

            // grammar/Java.g:798:5: ( navigableIdentifier '=' elementValue )
            // grammar/Java.g:798:9: navigableIdentifier '=' elementValue
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_navigableIdentifier_in_elementValuePair3890);
            navigableIdentifier241=navigableIdentifier();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, navigableIdentifier241.getTree());

            char_literal242=(Token)match(input,EQ,FOLLOW_EQ_in_elementValuePair3892); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal242_tree = 
            (CommonTree)adaptor.create(char_literal242)
            ;
            adaptor.addChild(root_0, char_literal242_tree);
            }

            pushFollow(FOLLOW_elementValue_in_elementValuePair3894);
            elementValue243=elementValue();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue243.getTree());

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 57, elementValuePair_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "elementValuePair"


    public static class elementValue_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "elementValue"
    // grammar/Java.g:801:1: elementValue : ( conditionalExpression | annotation | elementValueArrayInitializer );
    public final JavaParser.elementValue_return elementValue() throws RecognitionException {
        JavaParser.elementValue_return retval = new JavaParser.elementValue_return();
        retval.start = input.LT(1);

        int elementValue_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.conditionalExpression_return conditionalExpression244 =null;

        JavaParser.annotation_return annotation245 =null;

        JavaParser.elementValueArrayInitializer_return elementValueArrayInitializer246 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 58) ) { return retval; }

            // grammar/Java.g:802:5: ( conditionalExpression | annotation | elementValueArrayInitializer )
            int alt83=3;
            switch ( input.LA(1) ) {
            case BANG:
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case CHARLITERAL:
            case DOUBLE:
            case DOUBLELITERAL:
            case FALSE:
            case FLOAT:
            case FLOATLITERAL:
            case IDENTIFIER:
            case INT:
            case INTLITERAL:
            case LONG:
            case LONGLITERAL:
            case LPAREN:
            case NEW:
            case NULL:
            case PLUS:
            case PLUSPLUS:
            case SHORT:
            case STRINGLITERAL:
            case SUB:
            case SUBSUB:
            case SUPER:
            case THIS:
            case TILDE:
            case TRUE:
            case VOID:
                {
                alt83=1;
                }
                break;
            case MONKEYS_AT:
                {
                alt83=2;
                }
                break;
            case LBRACE:
                {
                alt83=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 83, 0, input);

                throw nvae;

            }

            switch (alt83) {
                case 1 :
                    // grammar/Java.g:802:9: conditionalExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_conditionalExpression_in_elementValue3913);
                    conditionalExpression244=conditionalExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression244.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:803:9: annotation
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_annotation_in_elementValue3923);
                    annotation245=annotation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation245.getTree());

                    }
                    break;
                case 3 :
                    // grammar/Java.g:804:9: elementValueArrayInitializer
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_elementValueArrayInitializer_in_elementValue3933);
                    elementValueArrayInitializer246=elementValueArrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValueArrayInitializer246.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 58, elementValue_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "elementValue"


    public static class elementValueArrayInitializer_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "elementValueArrayInitializer"
    // grammar/Java.g:807:1: elementValueArrayInitializer : '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}' ;
    public final JavaParser.elementValueArrayInitializer_return elementValueArrayInitializer() throws RecognitionException {
        JavaParser.elementValueArrayInitializer_return retval = new JavaParser.elementValueArrayInitializer_return();
        retval.start = input.LT(1);

        int elementValueArrayInitializer_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal247=null;
        Token char_literal249=null;
        Token char_literal251=null;
        Token char_literal252=null;
        JavaParser.elementValue_return elementValue248 =null;

        JavaParser.elementValue_return elementValue250 =null;


        CommonTree char_literal247_tree=null;
        CommonTree char_literal249_tree=null;
        CommonTree char_literal251_tree=null;
        CommonTree char_literal252_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 59) ) { return retval; }

            // grammar/Java.g:808:5: ( '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}' )
            // grammar/Java.g:808:9: '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}'
            {
            root_0 = (CommonTree)adaptor.nil();


            char_literal247=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_elementValueArrayInitializer3952); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal247_tree = 
            (CommonTree)adaptor.create(char_literal247)
            ;
            adaptor.addChild(root_0, char_literal247_tree);
            }

            // grammar/Java.g:809:9: ( elementValue ( ',' elementValue )* )?
            int alt85=2;
            int LA85_0 = input.LA(1);

            if ( (LA85_0==BANG||LA85_0==BOOLEAN||LA85_0==BYTE||(LA85_0 >= CHAR && LA85_0 <= CHARLITERAL)||(LA85_0 >= DOUBLE && LA85_0 <= DOUBLELITERAL)||LA85_0==FALSE||(LA85_0 >= FLOAT && LA85_0 <= FLOATLITERAL)||LA85_0==IDENTIFIER||LA85_0==INT||LA85_0==INTLITERAL||LA85_0==LBRACE||(LA85_0 >= LONG && LA85_0 <= LPAREN)||LA85_0==MONKEYS_AT||(LA85_0 >= NEW && LA85_0 <= NULL)||LA85_0==PLUS||LA85_0==PLUSPLUS||LA85_0==SHORT||(LA85_0 >= STRINGLITERAL && LA85_0 <= SUB)||(LA85_0 >= SUBSUB && LA85_0 <= SUPER)||LA85_0==THIS||LA85_0==TILDE||LA85_0==TRUE||LA85_0==VOID) ) {
                alt85=1;
            }
            switch (alt85) {
                case 1 :
                    // grammar/Java.g:809:10: elementValue ( ',' elementValue )*
                    {
                    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer3963);
                    elementValue248=elementValue();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue248.getTree());

                    // grammar/Java.g:810:13: ( ',' elementValue )*
                    loop84:
                    do {
                        int alt84=2;
                        int LA84_0 = input.LA(1);

                        if ( (LA84_0==COMMA) ) {
                            int LA84_1 = input.LA(2);

                            if ( (LA84_1==BANG||LA84_1==BOOLEAN||LA84_1==BYTE||(LA84_1 >= CHAR && LA84_1 <= CHARLITERAL)||(LA84_1 >= DOUBLE && LA84_1 <= DOUBLELITERAL)||LA84_1==FALSE||(LA84_1 >= FLOAT && LA84_1 <= FLOATLITERAL)||LA84_1==IDENTIFIER||LA84_1==INT||LA84_1==INTLITERAL||LA84_1==LBRACE||(LA84_1 >= LONG && LA84_1 <= LPAREN)||LA84_1==MONKEYS_AT||(LA84_1 >= NEW && LA84_1 <= NULL)||LA84_1==PLUS||LA84_1==PLUSPLUS||LA84_1==SHORT||(LA84_1 >= STRINGLITERAL && LA84_1 <= SUB)||(LA84_1 >= SUBSUB && LA84_1 <= SUPER)||LA84_1==THIS||LA84_1==TILDE||LA84_1==TRUE||LA84_1==VOID) ) {
                                alt84=1;
                            }


                        }


                        switch (alt84) {
                    	case 1 :
                    	    // grammar/Java.g:810:14: ',' elementValue
                    	    {
                    	    char_literal249=(Token)match(input,COMMA,FOLLOW_COMMA_in_elementValueArrayInitializer3978); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal249_tree = 
                    	    (CommonTree)adaptor.create(char_literal249)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal249_tree);
                    	    }

                    	    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer3980);
                    	    elementValue250=elementValue();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue250.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop84;
                        }
                    } while (true);


                    }
                    break;

            }


            // grammar/Java.g:812:12: ( ',' )?
            int alt86=2;
            int LA86_0 = input.LA(1);

            if ( (LA86_0==COMMA) ) {
                alt86=1;
            }
            switch (alt86) {
                case 1 :
                    // grammar/Java.g:812:13: ','
                    {
                    char_literal251=(Token)match(input,COMMA,FOLLOW_COMMA_in_elementValueArrayInitializer4009); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal251_tree = 
                    (CommonTree)adaptor.create(char_literal251)
                    ;
                    adaptor.addChild(root_0, char_literal251_tree);
                    }

                    }
                    break;

            }


            char_literal252=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_elementValueArrayInitializer4013); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal252_tree = 
            (CommonTree)adaptor.create(char_literal252)
            ;
            adaptor.addChild(root_0, char_literal252_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 59, elementValueArrayInitializer_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "elementValueArrayInitializer"


    public static class annotationTypeDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "annotationTypeDeclaration"
    // grammar/Java.g:819:1: annotationTypeDeclaration : modifiers '@' 'interface' IDENTIFIER annotationTypeBody ;
    public final JavaParser.annotationTypeDeclaration_return annotationTypeDeclaration() throws RecognitionException {
        JavaParser.annotationTypeDeclaration_return retval = new JavaParser.annotationTypeDeclaration_return();
        retval.start = input.LT(1);

        int annotationTypeDeclaration_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal254=null;
        Token string_literal255=null;
        Token IDENTIFIER256=null;
        JavaParser.modifiers_return modifiers253 =null;

        JavaParser.annotationTypeBody_return annotationTypeBody257 =null;


        CommonTree char_literal254_tree=null;
        CommonTree string_literal255_tree=null;
        CommonTree IDENTIFIER256_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 60) ) { return retval; }

            // grammar/Java.g:820:5: ( modifiers '@' 'interface' IDENTIFIER annotationTypeBody )
            // grammar/Java.g:820:9: modifiers '@' 'interface' IDENTIFIER annotationTypeBody
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_annotationTypeDeclaration4035);
            modifiers253=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers253.getTree());

            char_literal254=(Token)match(input,MONKEYS_AT,FOLLOW_MONKEYS_AT_in_annotationTypeDeclaration4037); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal254_tree = 
            (CommonTree)adaptor.create(char_literal254)
            ;
            adaptor.addChild(root_0, char_literal254_tree);
            }

            string_literal255=(Token)match(input,INTERFACE,FOLLOW_INTERFACE_in_annotationTypeDeclaration4047); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal255_tree = 
            (CommonTree)adaptor.create(string_literal255)
            ;
            adaptor.addChild(root_0, string_literal255_tree);
            }

            IDENTIFIER256=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_annotationTypeDeclaration4057); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER256_tree = 
            (CommonTree)adaptor.create(IDENTIFIER256)
            ;
            adaptor.addChild(root_0, IDENTIFIER256_tree);
            }

            pushFollow(FOLLOW_annotationTypeBody_in_annotationTypeDeclaration4067);
            annotationTypeBody257=annotationTypeBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeBody257.getTree());

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 60, annotationTypeDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "annotationTypeDeclaration"


    public static class annotationTypeBody_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "annotationTypeBody"
    // grammar/Java.g:827:1: annotationTypeBody : '{' ( annotationTypeElementDeclaration )* '}' ;
    public final JavaParser.annotationTypeBody_return annotationTypeBody() throws RecognitionException {
        JavaParser.annotationTypeBody_return retval = new JavaParser.annotationTypeBody_return();
        retval.start = input.LT(1);

        int annotationTypeBody_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal258=null;
        Token char_literal260=null;
        JavaParser.annotationTypeElementDeclaration_return annotationTypeElementDeclaration259 =null;


        CommonTree char_literal258_tree=null;
        CommonTree char_literal260_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 61) ) { return retval; }

            // grammar/Java.g:828:5: ( '{' ( annotationTypeElementDeclaration )* '}' )
            // grammar/Java.g:828:9: '{' ( annotationTypeElementDeclaration )* '}'
            {
            root_0 = (CommonTree)adaptor.nil();


            char_literal258=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_annotationTypeBody4087); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal258_tree = 
            (CommonTree)adaptor.create(char_literal258)
            ;
            adaptor.addChild(root_0, char_literal258_tree);
            }

            // grammar/Java.g:829:9: ( annotationTypeElementDeclaration )*
            loop87:
            do {
                int alt87=2;
                int LA87_0 = input.LA(1);

                if ( (LA87_0==ABSTRACT||LA87_0==BOOLEAN||LA87_0==BYTE||LA87_0==CHAR||LA87_0==CLASS||LA87_0==DOUBLE||LA87_0==ENUM||LA87_0==FINAL||LA87_0==FLOAT||LA87_0==IDENTIFIER||(LA87_0 >= INT && LA87_0 <= INTERFACE)||LA87_0==LONG||LA87_0==LT||(LA87_0 >= MONKEYS_AT && LA87_0 <= NATIVE)||(LA87_0 >= PRIVATE && LA87_0 <= PUBLIC)||(LA87_0 >= SEMI && LA87_0 <= SHORT)||(LA87_0 >= STATIC && LA87_0 <= STRICTFP)||LA87_0==SYNCHRONIZED||LA87_0==TRANSIENT||(LA87_0 >= VOID && LA87_0 <= VOLATILE)) ) {
                    alt87=1;
                }


                switch (alt87) {
            	case 1 :
            	    // grammar/Java.g:829:10: annotationTypeElementDeclaration
            	    {
            	    pushFollow(FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody4098);
            	    annotationTypeElementDeclaration259=annotationTypeElementDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeElementDeclaration259.getTree());

            	    }
            	    break;

            	default :
            	    break loop87;
                }
            } while (true);


            char_literal260=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_annotationTypeBody4119); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal260_tree = 
            (CommonTree)adaptor.create(char_literal260)
            ;
            adaptor.addChild(root_0, char_literal260_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 61, annotationTypeBody_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "annotationTypeBody"


    public static class annotationTypeElementDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "annotationTypeElementDeclaration"
    // grammar/Java.g:837:1: annotationTypeElementDeclaration : ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' );
    public final JavaParser.annotationTypeElementDeclaration_return annotationTypeElementDeclaration() throws RecognitionException {
        JavaParser.annotationTypeElementDeclaration_return retval = new JavaParser.annotationTypeElementDeclaration_return();
        retval.start = input.LT(1);

        int annotationTypeElementDeclaration_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal267=null;
        JavaParser.annotationMethodDeclaration_return annotationMethodDeclaration261 =null;

        JavaParser.interfaceFieldDeclaration_return interfaceFieldDeclaration262 =null;

        JavaParser.normalClassDeclaration_return normalClassDeclaration263 =null;

        JavaParser.normalInterfaceDeclaration_return normalInterfaceDeclaration264 =null;

        JavaParser.enumDeclaration_return enumDeclaration265 =null;

        JavaParser.annotationTypeDeclaration_return annotationTypeDeclaration266 =null;


        CommonTree char_literal267_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 62) ) { return retval; }

            // grammar/Java.g:838:5: ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' )
            int alt88=7;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA88_1 = input.LA(2);

                if ( (synpred118_Java()) ) {
                    alt88=1;
                }
                else if ( (synpred119_Java()) ) {
                    alt88=2;
                }
                else if ( (synpred120_Java()) ) {
                    alt88=3;
                }
                else if ( (synpred121_Java()) ) {
                    alt88=4;
                }
                else if ( (synpred122_Java()) ) {
                    alt88=5;
                }
                else if ( (synpred123_Java()) ) {
                    alt88=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 88, 1, input);

                    throw nvae;

                }
                }
                break;
            case PUBLIC:
                {
                int LA88_2 = input.LA(2);

                if ( (synpred118_Java()) ) {
                    alt88=1;
                }
                else if ( (synpred119_Java()) ) {
                    alt88=2;
                }
                else if ( (synpred120_Java()) ) {
                    alt88=3;
                }
                else if ( (synpred121_Java()) ) {
                    alt88=4;
                }
                else if ( (synpred122_Java()) ) {
                    alt88=5;
                }
                else if ( (synpred123_Java()) ) {
                    alt88=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 88, 2, input);

                    throw nvae;

                }
                }
                break;
            case PROTECTED:
                {
                int LA88_3 = input.LA(2);

                if ( (synpred118_Java()) ) {
                    alt88=1;
                }
                else if ( (synpred119_Java()) ) {
                    alt88=2;
                }
                else if ( (synpred120_Java()) ) {
                    alt88=3;
                }
                else if ( (synpred121_Java()) ) {
                    alt88=4;
                }
                else if ( (synpred122_Java()) ) {
                    alt88=5;
                }
                else if ( (synpred123_Java()) ) {
                    alt88=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 88, 3, input);

                    throw nvae;

                }
                }
                break;
            case PRIVATE:
                {
                int LA88_4 = input.LA(2);

                if ( (synpred118_Java()) ) {
                    alt88=1;
                }
                else if ( (synpred119_Java()) ) {
                    alt88=2;
                }
                else if ( (synpred120_Java()) ) {
                    alt88=3;
                }
                else if ( (synpred121_Java()) ) {
                    alt88=4;
                }
                else if ( (synpred122_Java()) ) {
                    alt88=5;
                }
                else if ( (synpred123_Java()) ) {
                    alt88=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 88, 4, input);

                    throw nvae;

                }
                }
                break;
            case STATIC:
                {
                int LA88_5 = input.LA(2);

                if ( (synpred118_Java()) ) {
                    alt88=1;
                }
                else if ( (synpred119_Java()) ) {
                    alt88=2;
                }
                else if ( (synpred120_Java()) ) {
                    alt88=3;
                }
                else if ( (synpred121_Java()) ) {
                    alt88=4;
                }
                else if ( (synpred122_Java()) ) {
                    alt88=5;
                }
                else if ( (synpred123_Java()) ) {
                    alt88=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 88, 5, input);

                    throw nvae;

                }
                }
                break;
            case ABSTRACT:
                {
                int LA88_6 = input.LA(2);

                if ( (synpred118_Java()) ) {
                    alt88=1;
                }
                else if ( (synpred119_Java()) ) {
                    alt88=2;
                }
                else if ( (synpred120_Java()) ) {
                    alt88=3;
                }
                else if ( (synpred121_Java()) ) {
                    alt88=4;
                }
                else if ( (synpred122_Java()) ) {
                    alt88=5;
                }
                else if ( (synpred123_Java()) ) {
                    alt88=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 88, 6, input);

                    throw nvae;

                }
                }
                break;
            case FINAL:
                {
                int LA88_7 = input.LA(2);

                if ( (synpred118_Java()) ) {
                    alt88=1;
                }
                else if ( (synpred119_Java()) ) {
                    alt88=2;
                }
                else if ( (synpred120_Java()) ) {
                    alt88=3;
                }
                else if ( (synpred121_Java()) ) {
                    alt88=4;
                }
                else if ( (synpred122_Java()) ) {
                    alt88=5;
                }
                else if ( (synpred123_Java()) ) {
                    alt88=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 88, 7, input);

                    throw nvae;

                }
                }
                break;
            case NATIVE:
                {
                int LA88_8 = input.LA(2);

                if ( (synpred118_Java()) ) {
                    alt88=1;
                }
                else if ( (synpred119_Java()) ) {
                    alt88=2;
                }
                else if ( (synpred120_Java()) ) {
                    alt88=3;
                }
                else if ( (synpred121_Java()) ) {
                    alt88=4;
                }
                else if ( (synpred122_Java()) ) {
                    alt88=5;
                }
                else if ( (synpred123_Java()) ) {
                    alt88=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 88, 8, input);

                    throw nvae;

                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA88_9 = input.LA(2);

                if ( (synpred118_Java()) ) {
                    alt88=1;
                }
                else if ( (synpred119_Java()) ) {
                    alt88=2;
                }
                else if ( (synpred120_Java()) ) {
                    alt88=3;
                }
                else if ( (synpred121_Java()) ) {
                    alt88=4;
                }
                else if ( (synpred122_Java()) ) {
                    alt88=5;
                }
                else if ( (synpred123_Java()) ) {
                    alt88=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 88, 9, input);

                    throw nvae;

                }
                }
                break;
            case TRANSIENT:
                {
                int LA88_10 = input.LA(2);

                if ( (synpred118_Java()) ) {
                    alt88=1;
                }
                else if ( (synpred119_Java()) ) {
                    alt88=2;
                }
                else if ( (synpred120_Java()) ) {
                    alt88=3;
                }
                else if ( (synpred121_Java()) ) {
                    alt88=4;
                }
                else if ( (synpred122_Java()) ) {
                    alt88=5;
                }
                else if ( (synpred123_Java()) ) {
                    alt88=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 88, 10, input);

                    throw nvae;

                }
                }
                break;
            case VOLATILE:
                {
                int LA88_11 = input.LA(2);

                if ( (synpred118_Java()) ) {
                    alt88=1;
                }
                else if ( (synpred119_Java()) ) {
                    alt88=2;
                }
                else if ( (synpred120_Java()) ) {
                    alt88=3;
                }
                else if ( (synpred121_Java()) ) {
                    alt88=4;
                }
                else if ( (synpred122_Java()) ) {
                    alt88=5;
                }
                else if ( (synpred123_Java()) ) {
                    alt88=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 88, 11, input);

                    throw nvae;

                }
                }
                break;
            case STRICTFP:
                {
                int LA88_12 = input.LA(2);

                if ( (synpred118_Java()) ) {
                    alt88=1;
                }
                else if ( (synpred119_Java()) ) {
                    alt88=2;
                }
                else if ( (synpred120_Java()) ) {
                    alt88=3;
                }
                else if ( (synpred121_Java()) ) {
                    alt88=4;
                }
                else if ( (synpred122_Java()) ) {
                    alt88=5;
                }
                else if ( (synpred123_Java()) ) {
                    alt88=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 88, 12, input);

                    throw nvae;

                }
                }
                break;
            case IDENTIFIER:
                {
                int LA88_13 = input.LA(2);

                if ( (synpred118_Java()) ) {
                    alt88=1;
                }
                else if ( (synpred119_Java()) ) {
                    alt88=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 88, 13, input);

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
                int LA88_14 = input.LA(2);

                if ( (synpred118_Java()) ) {
                    alt88=1;
                }
                else if ( (synpred119_Java()) ) {
                    alt88=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 88, 14, input);

                    throw nvae;

                }
                }
                break;
            case CLASS:
                {
                alt88=3;
                }
                break;
            case INTERFACE:
                {
                alt88=4;
                }
                break;
            case ENUM:
                {
                alt88=5;
                }
                break;
            case SEMI:
                {
                alt88=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 88, 0, input);

                throw nvae;

            }

            switch (alt88) {
                case 1 :
                    // grammar/Java.g:838:9: annotationMethodDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_annotationMethodDeclaration_in_annotationTypeElementDeclaration4140);
                    annotationMethodDeclaration261=annotationMethodDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationMethodDeclaration261.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:839:9: interfaceFieldDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_interfaceFieldDeclaration_in_annotationTypeElementDeclaration4150);
                    interfaceFieldDeclaration262=interfaceFieldDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceFieldDeclaration262.getTree());

                    }
                    break;
                case 3 :
                    // grammar/Java.g:840:9: normalClassDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_normalClassDeclaration_in_annotationTypeElementDeclaration4160);
                    normalClassDeclaration263=normalClassDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalClassDeclaration263.getTree());

                    }
                    break;
                case 4 :
                    // grammar/Java.g:841:9: normalInterfaceDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementDeclaration4170);
                    normalInterfaceDeclaration264=normalInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalInterfaceDeclaration264.getTree());

                    }
                    break;
                case 5 :
                    // grammar/Java.g:842:9: enumDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_enumDeclaration_in_annotationTypeElementDeclaration4180);
                    enumDeclaration265=enumDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumDeclaration265.getTree());

                    }
                    break;
                case 6 :
                    // grammar/Java.g:843:9: annotationTypeDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_annotationTypeDeclaration_in_annotationTypeElementDeclaration4190);
                    annotationTypeDeclaration266=annotationTypeDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeDeclaration266.getTree());

                    }
                    break;
                case 7 :
                    // grammar/Java.g:844:9: ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal267=(Token)match(input,SEMI,FOLLOW_SEMI_in_annotationTypeElementDeclaration4200); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal267_tree = 
                    (CommonTree)adaptor.create(char_literal267)
                    ;
                    adaptor.addChild(root_0, char_literal267_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 62, annotationTypeElementDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "annotationTypeElementDeclaration"


    public static class annotationMethodDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "annotationMethodDeclaration"
    // grammar/Java.g:847:1: annotationMethodDeclaration : modifiers type IDENTIFIER '(' ')' ( 'default' elementValue )? ';' ;
    public final JavaParser.annotationMethodDeclaration_return annotationMethodDeclaration() throws RecognitionException {
        JavaParser.annotationMethodDeclaration_return retval = new JavaParser.annotationMethodDeclaration_return();
        retval.start = input.LT(1);

        int annotationMethodDeclaration_StartIndex = input.index();

        CommonTree root_0 = null;

        Token IDENTIFIER270=null;
        Token char_literal271=null;
        Token char_literal272=null;
        Token string_literal273=null;
        Token char_literal275=null;
        JavaParser.modifiers_return modifiers268 =null;

        JavaParser.type_return type269 =null;

        JavaParser.elementValue_return elementValue274 =null;


        CommonTree IDENTIFIER270_tree=null;
        CommonTree char_literal271_tree=null;
        CommonTree char_literal272_tree=null;
        CommonTree string_literal273_tree=null;
        CommonTree char_literal275_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 63) ) { return retval; }

            // grammar/Java.g:848:5: ( modifiers type IDENTIFIER '(' ')' ( 'default' elementValue )? ';' )
            // grammar/Java.g:848:9: modifiers type IDENTIFIER '(' ')' ( 'default' elementValue )? ';'
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_annotationMethodDeclaration4219);
            modifiers268=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers268.getTree());

            pushFollow(FOLLOW_type_in_annotationMethodDeclaration4221);
            type269=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type269.getTree());

            IDENTIFIER270=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_annotationMethodDeclaration4223); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER270_tree = 
            (CommonTree)adaptor.create(IDENTIFIER270)
            ;
            adaptor.addChild(root_0, IDENTIFIER270_tree);
            }

            char_literal271=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_annotationMethodDeclaration4233); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal271_tree = 
            (CommonTree)adaptor.create(char_literal271)
            ;
            adaptor.addChild(root_0, char_literal271_tree);
            }

            char_literal272=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_annotationMethodDeclaration4235); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal272_tree = 
            (CommonTree)adaptor.create(char_literal272)
            ;
            adaptor.addChild(root_0, char_literal272_tree);
            }

            // grammar/Java.g:849:17: ( 'default' elementValue )?
            int alt89=2;
            int LA89_0 = input.LA(1);

            if ( (LA89_0==DEFAULT) ) {
                alt89=1;
            }
            switch (alt89) {
                case 1 :
                    // grammar/Java.g:849:18: 'default' elementValue
                    {
                    string_literal273=(Token)match(input,DEFAULT,FOLLOW_DEFAULT_in_annotationMethodDeclaration4238); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal273_tree = 
                    (CommonTree)adaptor.create(string_literal273)
                    ;
                    adaptor.addChild(root_0, string_literal273_tree);
                    }

                    pushFollow(FOLLOW_elementValue_in_annotationMethodDeclaration4240);
                    elementValue274=elementValue();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue274.getTree());

                    }
                    break;

            }


            char_literal275=(Token)match(input,SEMI,FOLLOW_SEMI_in_annotationMethodDeclaration4269); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal275_tree = 
            (CommonTree)adaptor.create(char_literal275)
            ;
            adaptor.addChild(root_0, char_literal275_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 63, annotationMethodDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "annotationMethodDeclaration"


    public static class block_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "block"
    // grammar/Java.g:854:1: block : '{' blockInner ;
    public final JavaParser.block_return block() throws RecognitionException {
        JavaParser.block_return retval = new JavaParser.block_return();
        retval.start = input.LT(1);

        int block_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal276=null;
        JavaParser.blockInner_return blockInner277 =null;


        CommonTree char_literal276_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 64) ) { return retval; }

            // grammar/Java.g:855:2: ( '{' blockInner )
            // grammar/Java.g:855:4: '{' blockInner
            {
            root_0 = (CommonTree)adaptor.nil();


            char_literal276=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_block4287); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal276_tree = 
            (CommonTree)adaptor.create(char_literal276)
            ;
            adaptor.addChild(root_0, char_literal276_tree);
            }

            pushFollow(FOLLOW_blockInner_in_block4289);
            blockInner277=blockInner();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, blockInner277.getTree());

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 64, block_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "block"


    public static class blockInner_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "blockInner"
    // grammar/Java.g:858:1: blockInner : ( blockStatement )* '}' -> ^( BLOCK_T ( blockStatement )* '}' ) ;
    public final JavaParser.blockInner_return blockInner() throws RecognitionException {
        JavaParser.blockInner_return retval = new JavaParser.blockInner_return();
        retval.start = input.LT(1);

        int blockInner_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal279=null;
        JavaParser.blockStatement_return blockStatement278 =null;


        CommonTree char_literal279_tree=null;
        RewriteRuleTokenStream stream_RBRACE=new RewriteRuleTokenStream(adaptor,"token RBRACE");
        RewriteRuleSubtreeStream stream_blockStatement=new RewriteRuleSubtreeStream(adaptor,"rule blockStatement");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 65) ) { return retval; }

            // grammar/Java.g:859:5: ( ( blockStatement )* '}' -> ^( BLOCK_T ( blockStatement )* '}' ) )
            // grammar/Java.g:859:8: ( blockStatement )* '}'
            {
            // grammar/Java.g:859:8: ( blockStatement )*
            loop90:
            do {
                int alt90=2;
                int LA90_0 = input.LA(1);

                if ( (LA90_0==EOF||LA90_0==ABSTRACT||(LA90_0 >= ASSERT && LA90_0 <= BANG)||(LA90_0 >= BOOLEAN && LA90_0 <= BYTE)||(LA90_0 >= CHAR && LA90_0 <= CLASS)||LA90_0==CONTINUE||LA90_0==DO||(LA90_0 >= DOUBLE && LA90_0 <= DOUBLELITERAL)||LA90_0==ENUM||(LA90_0 >= FALSE && LA90_0 <= FINAL)||(LA90_0 >= FLOAT && LA90_0 <= FOR)||(LA90_0 >= IDENTIFIER && LA90_0 <= IF)||(LA90_0 >= INT && LA90_0 <= INTLITERAL)||LA90_0==LBRACE||(LA90_0 >= LONG && LA90_0 <= LT)||(LA90_0 >= MONKEYS_AT && LA90_0 <= NULL)||LA90_0==PLUS||(LA90_0 >= PLUSPLUS && LA90_0 <= PUBLIC)||LA90_0==RETURN||(LA90_0 >= SEMI && LA90_0 <= SHORT)||(LA90_0 >= STATIC && LA90_0 <= SUB)||(LA90_0 >= SUBSUB && LA90_0 <= SYNCHRONIZED)||(LA90_0 >= THIS && LA90_0 <= THROW)||(LA90_0 >= TILDE && LA90_0 <= TRY)||(LA90_0 >= VOID && LA90_0 <= WHILE)) ) {
                    alt90=1;
                }


                switch (alt90) {
            	case 1 :
            	    // grammar/Java.g:859:9: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_blockInner4305);
            	    blockStatement278=blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_blockStatement.add(blockStatement278.getTree());

            	    }
            	    break;

            	default :
            	    break loop90;
                }
            } while (true);


            char_literal279=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_blockInner4326); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RBRACE.add(char_literal279);


            // AST REWRITE
            // elements: RBRACE, blockStatement
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 861:13: -> ^( BLOCK_T ( blockStatement )* '}' )
            {
                // grammar/Java.g:861:16: ^( BLOCK_T ( blockStatement )* '}' )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(BLOCK_T, "BLOCK_T")
                , root_1);

                // grammar/Java.g:861:27: ( blockStatement )*
                while ( stream_blockStatement.hasNext() ) {
                    adaptor.addChild(root_1, stream_blockStatement.nextTree());

                }
                stream_blockStatement.reset();

                adaptor.addChild(root_1, 
                stream_RBRACE.nextNode()
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 65, blockInner_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "blockInner"


    public static class blockStatement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "blockStatement"
    // grammar/Java.g:888:1: blockStatement : blockStatementInner -> ^( BLOCK_T blockStatementInner ) ;
    public final JavaParser.blockStatement_return blockStatement() throws RecognitionException {
        JavaParser.blockStatement_return retval = new JavaParser.blockStatement_return();
        retval.start = input.LT(1);

        int blockStatement_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.blockStatementInner_return blockStatementInner280 =null;


        RewriteRuleSubtreeStream stream_blockStatementInner=new RewriteRuleSubtreeStream(adaptor,"rule blockStatementInner");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 66) ) { return retval; }

            // grammar/Java.g:889:2: ( blockStatementInner -> ^( BLOCK_T blockStatementInner ) )
            // grammar/Java.g:889:3: blockStatementInner
            {
            pushFollow(FOLLOW_blockStatementInner_in_blockStatement4354);
            blockStatementInner280=blockStatementInner();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_blockStatementInner.add(blockStatementInner280.getTree());

            // AST REWRITE
            // elements: blockStatementInner
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 889:23: -> ^( BLOCK_T blockStatementInner )
            {
                // grammar/Java.g:889:26: ^( BLOCK_T blockStatementInner )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(BLOCK_T, "BLOCK_T")
                , root_1);

                adaptor.addChild(root_1, stream_blockStatementInner.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 66, blockStatement_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "blockStatement"


    public static class blockStatementInner_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "blockStatementInner"
    // grammar/Java.g:892:1: blockStatementInner : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );
    public final JavaParser.blockStatementInner_return blockStatementInner() throws RecognitionException {
        JavaParser.blockStatementInner_return retval = new JavaParser.blockStatementInner_return();
        retval.start = input.LT(1);

        int blockStatementInner_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.localVariableDeclarationStatement_return localVariableDeclarationStatement281 =null;

        JavaParser.classOrInterfaceDeclaration_return classOrInterfaceDeclaration282 =null;

        JavaParser.statement_return statement283 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 67) ) { return retval; }

            // grammar/Java.g:893:5: ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement )
            int alt91=3;
            switch ( input.LA(1) ) {
            case FINAL:
                {
                int LA91_1 = input.LA(2);

                if ( (synpred126_Java()) ) {
                    alt91=1;
                }
                else if ( (synpred127_Java()) ) {
                    alt91=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 91, 1, input);

                    throw nvae;

                }
                }
                break;
            case MONKEYS_AT:
                {
                int LA91_2 = input.LA(2);

                if ( (synpred126_Java()) ) {
                    alt91=1;
                }
                else if ( (synpred127_Java()) ) {
                    alt91=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 91, 2, input);

                    throw nvae;

                }
                }
                break;
            case IDENTIFIER:
                {
                int LA91_3 = input.LA(2);

                if ( (synpred126_Java()) ) {
                    alt91=1;
                }
                else if ( (true) ) {
                    alt91=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 91, 3, input);

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
                int LA91_4 = input.LA(2);

                if ( (synpred126_Java()) ) {
                    alt91=1;
                }
                else if ( (true) ) {
                    alt91=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 91, 4, input);

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
                alt91=2;
                }
                break;
            case SYNCHRONIZED:
                {
                int LA91_11 = input.LA(2);

                if ( (synpred127_Java()) ) {
                    alt91=2;
                }
                else if ( (true) ) {
                    alt91=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 91, 11, input);

                    throw nvae;

                }
                }
                break;
            case ASSERT:
            case BANG:
            case BREAK:
            case CHARLITERAL:
            case CONTINUE:
            case DO:
            case DOUBLELITERAL:
            case FALSE:
            case FLOATLITERAL:
            case FOR:
            case IF:
            case INTLITERAL:
            case LBRACE:
            case LONGLITERAL:
            case LPAREN:
            case NEW:
            case NULL:
            case PLUS:
            case PLUSPLUS:
            case RETURN:
            case SEMI:
            case STRINGLITERAL:
            case SUB:
            case SUBSUB:
            case SUPER:
            case SWITCH:
            case THIS:
            case THROW:
            case TILDE:
            case TRUE:
            case TRY:
            case VOID:
            case WHILE:
                {
                alt91=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 91, 0, input);

                throw nvae;

            }

            switch (alt91) {
                case 1 :
                    // grammar/Java.g:893:9: localVariableDeclarationStatement
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_localVariableDeclarationStatement_in_blockStatementInner4378);
                    localVariableDeclarationStatement281=localVariableDeclarationStatement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, localVariableDeclarationStatement281.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:894:9: classOrInterfaceDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_blockStatementInner4388);
                    classOrInterfaceDeclaration282=classOrInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceDeclaration282.getTree());

                    }
                    break;
                case 3 :
                    // grammar/Java.g:895:9: statement
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_statement_in_blockStatementInner4398);
                    statement283=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement283.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 67, blockStatementInner_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "blockStatementInner"


    public static class localVariableDeclarationStatement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "localVariableDeclarationStatement"
    // grammar/Java.g:899:1: localVariableDeclarationStatement : localVariableDeclaration ';' -> ^( VARIABLE_T localVariableDeclaration ';' ) ;
    public final JavaParser.localVariableDeclarationStatement_return localVariableDeclarationStatement() throws RecognitionException {
        JavaParser.localVariableDeclarationStatement_return retval = new JavaParser.localVariableDeclarationStatement_return();
        retval.start = input.LT(1);

        int localVariableDeclarationStatement_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal285=null;
        JavaParser.localVariableDeclaration_return localVariableDeclaration284 =null;


        CommonTree char_literal285_tree=null;
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleSubtreeStream stream_localVariableDeclaration=new RewriteRuleSubtreeStream(adaptor,"rule localVariableDeclaration");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 68) ) { return retval; }

            // grammar/Java.g:900:5: ( localVariableDeclaration ';' -> ^( VARIABLE_T localVariableDeclaration ';' ) )
            // grammar/Java.g:900:9: localVariableDeclaration ';'
            {
            pushFollow(FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement4418);
            localVariableDeclaration284=localVariableDeclaration();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_localVariableDeclaration.add(localVariableDeclaration284.getTree());

            char_literal285=(Token)match(input,SEMI,FOLLOW_SEMI_in_localVariableDeclarationStatement4428); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMI.add(char_literal285);


            // AST REWRITE
            // elements: localVariableDeclaration, SEMI
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 901:13: -> ^( VARIABLE_T localVariableDeclaration ';' )
            {
                // grammar/Java.g:901:16: ^( VARIABLE_T localVariableDeclaration ';' )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(VARIABLE_T, "VARIABLE_T")
                , root_1);

                adaptor.addChild(root_1, stream_localVariableDeclaration.nextTree());

                adaptor.addChild(root_1, 
                stream_SEMI.nextNode()
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 68, localVariableDeclarationStatement_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "localVariableDeclarationStatement"


    public static class localVariableDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "localVariableDeclaration"
    // grammar/Java.g:904:1: localVariableDeclaration : variableModifiers type variableDeclarator ( ',' variableDeclarator )* ;
    public final JavaParser.localVariableDeclaration_return localVariableDeclaration() throws RecognitionException {
        JavaParser.localVariableDeclaration_return retval = new JavaParser.localVariableDeclaration_return();
        retval.start = input.LT(1);

        int localVariableDeclaration_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal289=null;
        JavaParser.variableModifiers_return variableModifiers286 =null;

        JavaParser.type_return type287 =null;

        JavaParser.variableDeclarator_return variableDeclarator288 =null;

        JavaParser.variableDeclarator_return variableDeclarator290 =null;


        CommonTree char_literal289_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 69) ) { return retval; }

            // grammar/Java.g:905:5: ( variableModifiers type variableDeclarator ( ',' variableDeclarator )* )
            // grammar/Java.g:905:9: variableModifiers type variableDeclarator ( ',' variableDeclarator )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_variableModifiers_in_localVariableDeclaration4457);
            variableModifiers286=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers286.getTree());

            pushFollow(FOLLOW_type_in_localVariableDeclaration4459);
            type287=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type287.getTree());

            pushFollow(FOLLOW_variableDeclarator_in_localVariableDeclaration4469);
            variableDeclarator288=variableDeclarator();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarator288.getTree());

            // grammar/Java.g:907:9: ( ',' variableDeclarator )*
            loop92:
            do {
                int alt92=2;
                int LA92_0 = input.LA(1);

                if ( (LA92_0==COMMA) ) {
                    alt92=1;
                }


                switch (alt92) {
            	case 1 :
            	    // grammar/Java.g:907:10: ',' variableDeclarator
            	    {
            	    char_literal289=(Token)match(input,COMMA,FOLLOW_COMMA_in_localVariableDeclaration4480); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal289_tree = 
            	    (CommonTree)adaptor.create(char_literal289)
            	    ;
            	    adaptor.addChild(root_0, char_literal289_tree);
            	    }

            	    pushFollow(FOLLOW_variableDeclarator_in_localVariableDeclaration4482);
            	    variableDeclarator290=variableDeclarator();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarator290.getTree());

            	    }
            	    break;

            	default :
            	    break loop92;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 69, localVariableDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "localVariableDeclaration"


    public static class statement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "statement"
    // grammar/Java.g:911:1: statement : ( block | ( 'assert' ) expression ( ':' expression )? ';' | 'assert' expression ( ':' expression )? ';' | 'if' parExpression statement ( 'else' statement )? | forstatement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | trystatement | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( IDENTIFIER )? ';' | 'continue' ( IDENTIFIER )? ';' | expression ';' | IDENTIFIER ':' statement | ';' );
    public final JavaParser.statement_return statement() throws RecognitionException {
        JavaParser.statement_return retval = new JavaParser.statement_return();
        retval.start = input.LT(1);

        int statement_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal292=null;
        Token char_literal294=null;
        Token char_literal296=null;
        Token string_literal297=null;
        Token char_literal299=null;
        Token char_literal301=null;
        Token string_literal302=null;
        Token string_literal305=null;
        Token string_literal308=null;
        Token string_literal311=null;
        Token string_literal313=null;
        Token char_literal315=null;
        Token string_literal317=null;
        Token char_literal319=null;
        Token char_literal321=null;
        Token string_literal322=null;
        Token string_literal325=null;
        Token char_literal327=null;
        Token string_literal328=null;
        Token char_literal330=null;
        Token string_literal331=null;
        Token IDENTIFIER332=null;
        Token char_literal333=null;
        Token string_literal334=null;
        Token IDENTIFIER335=null;
        Token char_literal336=null;
        Token char_literal338=null;
        Token IDENTIFIER339=null;
        Token char_literal340=null;
        Token char_literal342=null;
        JavaParser.block_return block291 =null;

        JavaParser.expression_return expression293 =null;

        JavaParser.expression_return expression295 =null;

        JavaParser.expression_return expression298 =null;

        JavaParser.expression_return expression300 =null;

        JavaParser.parExpression_return parExpression303 =null;

        JavaParser.statement_return statement304 =null;

        JavaParser.statement_return statement306 =null;

        JavaParser.forstatement_return forstatement307 =null;

        JavaParser.parExpression_return parExpression309 =null;

        JavaParser.statement_return statement310 =null;

        JavaParser.statement_return statement312 =null;

        JavaParser.parExpression_return parExpression314 =null;

        JavaParser.trystatement_return trystatement316 =null;

        JavaParser.parExpression_return parExpression318 =null;

        JavaParser.switchBlockStatementGroups_return switchBlockStatementGroups320 =null;

        JavaParser.parExpression_return parExpression323 =null;

        JavaParser.block_return block324 =null;

        JavaParser.expression_return expression326 =null;

        JavaParser.expression_return expression329 =null;

        JavaParser.expression_return expression337 =null;

        JavaParser.statement_return statement341 =null;


        CommonTree string_literal292_tree=null;
        CommonTree char_literal294_tree=null;
        CommonTree char_literal296_tree=null;
        CommonTree string_literal297_tree=null;
        CommonTree char_literal299_tree=null;
        CommonTree char_literal301_tree=null;
        CommonTree string_literal302_tree=null;
        CommonTree string_literal305_tree=null;
        CommonTree string_literal308_tree=null;
        CommonTree string_literal311_tree=null;
        CommonTree string_literal313_tree=null;
        CommonTree char_literal315_tree=null;
        CommonTree string_literal317_tree=null;
        CommonTree char_literal319_tree=null;
        CommonTree char_literal321_tree=null;
        CommonTree string_literal322_tree=null;
        CommonTree string_literal325_tree=null;
        CommonTree char_literal327_tree=null;
        CommonTree string_literal328_tree=null;
        CommonTree char_literal330_tree=null;
        CommonTree string_literal331_tree=null;
        CommonTree IDENTIFIER332_tree=null;
        CommonTree char_literal333_tree=null;
        CommonTree string_literal334_tree=null;
        CommonTree IDENTIFIER335_tree=null;
        CommonTree char_literal336_tree=null;
        CommonTree char_literal338_tree=null;
        CommonTree IDENTIFIER339_tree=null;
        CommonTree char_literal340_tree=null;
        CommonTree char_literal342_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 70) ) { return retval; }

            // grammar/Java.g:912:5: ( block | ( 'assert' ) expression ( ':' expression )? ';' | 'assert' expression ( ':' expression )? ';' | 'if' parExpression statement ( 'else' statement )? | forstatement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | trystatement | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( IDENTIFIER )? ';' | 'continue' ( IDENTIFIER )? ';' | expression ';' | IDENTIFIER ':' statement | ';' )
            int alt99=17;
            switch ( input.LA(1) ) {
            case LBRACE:
                {
                alt99=1;
                }
                break;
            case ASSERT:
                {
                int LA99_2 = input.LA(2);

                if ( (synpred131_Java()) ) {
                    alt99=2;
                }
                else if ( (synpred133_Java()) ) {
                    alt99=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 99, 2, input);

                    throw nvae;

                }
                }
                break;
            case IF:
                {
                alt99=4;
                }
                break;
            case FOR:
                {
                alt99=5;
                }
                break;
            case WHILE:
                {
                alt99=6;
                }
                break;
            case DO:
                {
                alt99=7;
                }
                break;
            case TRY:
                {
                alt99=8;
                }
                break;
            case SWITCH:
                {
                alt99=9;
                }
                break;
            case SYNCHRONIZED:
                {
                alt99=10;
                }
                break;
            case RETURN:
                {
                alt99=11;
                }
                break;
            case THROW:
                {
                alt99=12;
                }
                break;
            case BREAK:
                {
                alt99=13;
                }
                break;
            case CONTINUE:
                {
                alt99=14;
                }
                break;
            case BANG:
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case CHARLITERAL:
            case DOUBLE:
            case DOUBLELITERAL:
            case FALSE:
            case FLOAT:
            case FLOATLITERAL:
            case INT:
            case INTLITERAL:
            case LONG:
            case LONGLITERAL:
            case LPAREN:
            case NEW:
            case NULL:
            case PLUS:
            case PLUSPLUS:
            case SHORT:
            case STRINGLITERAL:
            case SUB:
            case SUBSUB:
            case SUPER:
            case THIS:
            case TILDE:
            case TRUE:
            case VOID:
                {
                alt99=15;
                }
                break;
            case IDENTIFIER:
                {
                int LA99_22 = input.LA(2);

                if ( (synpred149_Java()) ) {
                    alt99=15;
                }
                else if ( (synpred150_Java()) ) {
                    alt99=16;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 99, 22, input);

                    throw nvae;

                }
                }
                break;
            case SEMI:
                {
                alt99=17;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 99, 0, input);

                throw nvae;

            }

            switch (alt99) {
                case 1 :
                    // grammar/Java.g:912:9: block
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_block_in_statement4512);
                    block291=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block291.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:914:9: ( 'assert' ) expression ( ':' expression )? ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    // grammar/Java.g:914:9: ( 'assert' )
                    // grammar/Java.g:914:10: 'assert'
                    {
                    string_literal292=(Token)match(input,ASSERT,FOLLOW_ASSERT_in_statement4524); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal292_tree = 
                    (CommonTree)adaptor.create(string_literal292)
                    ;
                    adaptor.addChild(root_0, string_literal292_tree);
                    }

                    }


                    pushFollow(FOLLOW_expression_in_statement4544);
                    expression293=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression293.getTree());

                    // grammar/Java.g:916:20: ( ':' expression )?
                    int alt93=2;
                    int LA93_0 = input.LA(1);

                    if ( (LA93_0==COLON) ) {
                        alt93=1;
                    }
                    switch (alt93) {
                        case 1 :
                            // grammar/Java.g:916:21: ':' expression
                            {
                            char_literal294=(Token)match(input,COLON,FOLLOW_COLON_in_statement4547); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal294_tree = 
                            (CommonTree)adaptor.create(char_literal294)
                            ;
                            adaptor.addChild(root_0, char_literal294_tree);
                            }

                            pushFollow(FOLLOW_expression_in_statement4549);
                            expression295=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression295.getTree());

                            }
                            break;

                    }


                    char_literal296=(Token)match(input,SEMI,FOLLOW_SEMI_in_statement4553); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal296_tree = 
                    (CommonTree)adaptor.create(char_literal296)
                    ;
                    adaptor.addChild(root_0, char_literal296_tree);
                    }

                    }
                    break;
                case 3 :
                    // grammar/Java.g:917:9: 'assert' expression ( ':' expression )? ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal297=(Token)match(input,ASSERT,FOLLOW_ASSERT_in_statement4563); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal297_tree = 
                    (CommonTree)adaptor.create(string_literal297)
                    ;
                    adaptor.addChild(root_0, string_literal297_tree);
                    }

                    pushFollow(FOLLOW_expression_in_statement4566);
                    expression298=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression298.getTree());

                    // grammar/Java.g:917:30: ( ':' expression )?
                    int alt94=2;
                    int LA94_0 = input.LA(1);

                    if ( (LA94_0==COLON) ) {
                        alt94=1;
                    }
                    switch (alt94) {
                        case 1 :
                            // grammar/Java.g:917:31: ':' expression
                            {
                            char_literal299=(Token)match(input,COLON,FOLLOW_COLON_in_statement4569); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal299_tree = 
                            (CommonTree)adaptor.create(char_literal299)
                            ;
                            adaptor.addChild(root_0, char_literal299_tree);
                            }

                            pushFollow(FOLLOW_expression_in_statement4571);
                            expression300=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression300.getTree());

                            }
                            break;

                    }


                    char_literal301=(Token)match(input,SEMI,FOLLOW_SEMI_in_statement4575); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal301_tree = 
                    (CommonTree)adaptor.create(char_literal301)
                    ;
                    adaptor.addChild(root_0, char_literal301_tree);
                    }

                    }
                    break;
                case 4 :
                    // grammar/Java.g:918:9: 'if' parExpression statement ( 'else' statement )?
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal302=(Token)match(input,IF,FOLLOW_IF_in_statement4585); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal302_tree = 
                    (CommonTree)adaptor.create(string_literal302)
                    ;
                    adaptor.addChild(root_0, string_literal302_tree);
                    }

                    pushFollow(FOLLOW_parExpression_in_statement4587);
                    parExpression303=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression303.getTree());

                    pushFollow(FOLLOW_statement_in_statement4589);
                    statement304=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement304.getTree());

                    // grammar/Java.g:918:38: ( 'else' statement )?
                    int alt95=2;
                    int LA95_0 = input.LA(1);

                    if ( (LA95_0==ELSE) ) {
                        int LA95_1 = input.LA(2);

                        if ( (synpred134_Java()) ) {
                            alt95=1;
                        }
                    }
                    switch (alt95) {
                        case 1 :
                            // grammar/Java.g:918:39: 'else' statement
                            {
                            string_literal305=(Token)match(input,ELSE,FOLLOW_ELSE_in_statement4592); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal305_tree = 
                            (CommonTree)adaptor.create(string_literal305)
                            ;
                            adaptor.addChild(root_0, string_literal305_tree);
                            }

                            pushFollow(FOLLOW_statement_in_statement4594);
                            statement306=statement();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, statement306.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // grammar/Java.g:919:9: forstatement
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_forstatement_in_statement4606);
                    forstatement307=forstatement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, forstatement307.getTree());

                    }
                    break;
                case 6 :
                    // grammar/Java.g:920:9: 'while' parExpression statement
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal308=(Token)match(input,WHILE,FOLLOW_WHILE_in_statement4616); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal308_tree = 
                    (CommonTree)adaptor.create(string_literal308)
                    ;
                    adaptor.addChild(root_0, string_literal308_tree);
                    }

                    pushFollow(FOLLOW_parExpression_in_statement4618);
                    parExpression309=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression309.getTree());

                    pushFollow(FOLLOW_statement_in_statement4620);
                    statement310=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement310.getTree());

                    }
                    break;
                case 7 :
                    // grammar/Java.g:921:9: 'do' statement 'while' parExpression ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal311=(Token)match(input,DO,FOLLOW_DO_in_statement4630); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal311_tree = 
                    (CommonTree)adaptor.create(string_literal311)
                    ;
                    adaptor.addChild(root_0, string_literal311_tree);
                    }

                    pushFollow(FOLLOW_statement_in_statement4632);
                    statement312=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement312.getTree());

                    string_literal313=(Token)match(input,WHILE,FOLLOW_WHILE_in_statement4634); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal313_tree = 
                    (CommonTree)adaptor.create(string_literal313)
                    ;
                    adaptor.addChild(root_0, string_literal313_tree);
                    }

                    pushFollow(FOLLOW_parExpression_in_statement4636);
                    parExpression314=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression314.getTree());

                    char_literal315=(Token)match(input,SEMI,FOLLOW_SEMI_in_statement4638); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal315_tree = 
                    (CommonTree)adaptor.create(char_literal315)
                    ;
                    adaptor.addChild(root_0, char_literal315_tree);
                    }

                    }
                    break;
                case 8 :
                    // grammar/Java.g:922:9: trystatement
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_trystatement_in_statement4648);
                    trystatement316=trystatement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, trystatement316.getTree());

                    }
                    break;
                case 9 :
                    // grammar/Java.g:923:9: 'switch' parExpression '{' switchBlockStatementGroups '}'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal317=(Token)match(input,SWITCH,FOLLOW_SWITCH_in_statement4658); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal317_tree = 
                    (CommonTree)adaptor.create(string_literal317)
                    ;
                    adaptor.addChild(root_0, string_literal317_tree);
                    }

                    pushFollow(FOLLOW_parExpression_in_statement4660);
                    parExpression318=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression318.getTree());

                    char_literal319=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_statement4662); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal319_tree = 
                    (CommonTree)adaptor.create(char_literal319)
                    ;
                    adaptor.addChild(root_0, char_literal319_tree);
                    }

                    pushFollow(FOLLOW_switchBlockStatementGroups_in_statement4664);
                    switchBlockStatementGroups320=switchBlockStatementGroups();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, switchBlockStatementGroups320.getTree());

                    char_literal321=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_statement4666); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal321_tree = 
                    (CommonTree)adaptor.create(char_literal321)
                    ;
                    adaptor.addChild(root_0, char_literal321_tree);
                    }

                    }
                    break;
                case 10 :
                    // grammar/Java.g:924:9: 'synchronized' parExpression block
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal322=(Token)match(input,SYNCHRONIZED,FOLLOW_SYNCHRONIZED_in_statement4676); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal322_tree = 
                    (CommonTree)adaptor.create(string_literal322)
                    ;
                    adaptor.addChild(root_0, string_literal322_tree);
                    }

                    pushFollow(FOLLOW_parExpression_in_statement4678);
                    parExpression323=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression323.getTree());

                    pushFollow(FOLLOW_block_in_statement4680);
                    block324=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block324.getTree());

                    }
                    break;
                case 11 :
                    // grammar/Java.g:925:9: 'return' ( expression )? ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal325=(Token)match(input,RETURN,FOLLOW_RETURN_in_statement4690); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal325_tree = 
                    (CommonTree)adaptor.create(string_literal325)
                    ;
                    adaptor.addChild(root_0, string_literal325_tree);
                    }

                    // grammar/Java.g:925:18: ( expression )?
                    int alt96=2;
                    int LA96_0 = input.LA(1);

                    if ( (LA96_0==BANG||LA96_0==BOOLEAN||LA96_0==BYTE||(LA96_0 >= CHAR && LA96_0 <= CHARLITERAL)||(LA96_0 >= DOUBLE && LA96_0 <= DOUBLELITERAL)||LA96_0==FALSE||(LA96_0 >= FLOAT && LA96_0 <= FLOATLITERAL)||LA96_0==IDENTIFIER||LA96_0==INT||LA96_0==INTLITERAL||(LA96_0 >= LONG && LA96_0 <= LPAREN)||(LA96_0 >= NEW && LA96_0 <= NULL)||LA96_0==PLUS||LA96_0==PLUSPLUS||LA96_0==SHORT||(LA96_0 >= STRINGLITERAL && LA96_0 <= SUB)||(LA96_0 >= SUBSUB && LA96_0 <= SUPER)||LA96_0==THIS||LA96_0==TILDE||LA96_0==TRUE||LA96_0==VOID) ) {
                        alt96=1;
                    }
                    switch (alt96) {
                        case 1 :
                            // grammar/Java.g:925:19: expression
                            {
                            pushFollow(FOLLOW_expression_in_statement4693);
                            expression326=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression326.getTree());

                            }
                            break;

                    }


                    char_literal327=(Token)match(input,SEMI,FOLLOW_SEMI_in_statement4698); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal327_tree = 
                    (CommonTree)adaptor.create(char_literal327)
                    ;
                    adaptor.addChild(root_0, char_literal327_tree);
                    }

                    }
                    break;
                case 12 :
                    // grammar/Java.g:926:9: 'throw' expression ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal328=(Token)match(input,THROW,FOLLOW_THROW_in_statement4708); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal328_tree = 
                    (CommonTree)adaptor.create(string_literal328)
                    ;
                    adaptor.addChild(root_0, string_literal328_tree);
                    }

                    pushFollow(FOLLOW_expression_in_statement4710);
                    expression329=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression329.getTree());

                    char_literal330=(Token)match(input,SEMI,FOLLOW_SEMI_in_statement4712); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal330_tree = 
                    (CommonTree)adaptor.create(char_literal330)
                    ;
                    adaptor.addChild(root_0, char_literal330_tree);
                    }

                    }
                    break;
                case 13 :
                    // grammar/Java.g:927:9: 'break' ( IDENTIFIER )? ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal331=(Token)match(input,BREAK,FOLLOW_BREAK_in_statement4722); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal331_tree = 
                    (CommonTree)adaptor.create(string_literal331)
                    ;
                    adaptor.addChild(root_0, string_literal331_tree);
                    }

                    // grammar/Java.g:928:13: ( IDENTIFIER )?
                    int alt97=2;
                    int LA97_0 = input.LA(1);

                    if ( (LA97_0==IDENTIFIER) ) {
                        alt97=1;
                    }
                    switch (alt97) {
                        case 1 :
                            // grammar/Java.g:928:14: IDENTIFIER
                            {
                            IDENTIFIER332=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_statement4737); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            IDENTIFIER332_tree = 
                            (CommonTree)adaptor.create(IDENTIFIER332)
                            ;
                            adaptor.addChild(root_0, IDENTIFIER332_tree);
                            }

                            }
                            break;

                    }


                    char_literal333=(Token)match(input,SEMI,FOLLOW_SEMI_in_statement4754); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal333_tree = 
                    (CommonTree)adaptor.create(char_literal333)
                    ;
                    adaptor.addChild(root_0, char_literal333_tree);
                    }

                    }
                    break;
                case 14 :
                    // grammar/Java.g:930:9: 'continue' ( IDENTIFIER )? ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal334=(Token)match(input,CONTINUE,FOLLOW_CONTINUE_in_statement4764); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal334_tree = 
                    (CommonTree)adaptor.create(string_literal334)
                    ;
                    adaptor.addChild(root_0, string_literal334_tree);
                    }

                    // grammar/Java.g:931:13: ( IDENTIFIER )?
                    int alt98=2;
                    int LA98_0 = input.LA(1);

                    if ( (LA98_0==IDENTIFIER) ) {
                        alt98=1;
                    }
                    switch (alt98) {
                        case 1 :
                            // grammar/Java.g:931:14: IDENTIFIER
                            {
                            IDENTIFIER335=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_statement4779); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            IDENTIFIER335_tree = 
                            (CommonTree)adaptor.create(IDENTIFIER335)
                            ;
                            adaptor.addChild(root_0, IDENTIFIER335_tree);
                            }

                            }
                            break;

                    }


                    char_literal336=(Token)match(input,SEMI,FOLLOW_SEMI_in_statement4796); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal336_tree = 
                    (CommonTree)adaptor.create(char_literal336)
                    ;
                    adaptor.addChild(root_0, char_literal336_tree);
                    }

                    }
                    break;
                case 15 :
                    // grammar/Java.g:933:9: expression ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_expression_in_statement4806);
                    expression337=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression337.getTree());

                    char_literal338=(Token)match(input,SEMI,FOLLOW_SEMI_in_statement4809); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal338_tree = 
                    (CommonTree)adaptor.create(char_literal338)
                    ;
                    adaptor.addChild(root_0, char_literal338_tree);
                    }

                    }
                    break;
                case 16 :
                    // grammar/Java.g:934:9: IDENTIFIER ':' statement
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    IDENTIFIER339=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_statement4819); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER339_tree = 
                    (CommonTree)adaptor.create(IDENTIFIER339)
                    ;
                    adaptor.addChild(root_0, IDENTIFIER339_tree);
                    }

                    char_literal340=(Token)match(input,COLON,FOLLOW_COLON_in_statement4821); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal340_tree = 
                    (CommonTree)adaptor.create(char_literal340)
                    ;
                    adaptor.addChild(root_0, char_literal340_tree);
                    }

                    pushFollow(FOLLOW_statement_in_statement4823);
                    statement341=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement341.getTree());

                    }
                    break;
                case 17 :
                    // grammar/Java.g:935:9: ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal342=(Token)match(input,SEMI,FOLLOW_SEMI_in_statement4836); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal342_tree = 
                    (CommonTree)adaptor.create(char_literal342)
                    ;
                    adaptor.addChild(root_0, char_literal342_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 70, statement_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "statement"


    public static class switchBlockStatementGroups_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "switchBlockStatementGroups"
    // grammar/Java.g:939:1: switchBlockStatementGroups : ( switchBlockStatementGroup )* ;
    public final JavaParser.switchBlockStatementGroups_return switchBlockStatementGroups() throws RecognitionException {
        JavaParser.switchBlockStatementGroups_return retval = new JavaParser.switchBlockStatementGroups_return();
        retval.start = input.LT(1);

        int switchBlockStatementGroups_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.switchBlockStatementGroup_return switchBlockStatementGroup343 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 71) ) { return retval; }

            // grammar/Java.g:940:5: ( ( switchBlockStatementGroup )* )
            // grammar/Java.g:940:9: ( switchBlockStatementGroup )*
            {
            root_0 = (CommonTree)adaptor.nil();


            // grammar/Java.g:940:9: ( switchBlockStatementGroup )*
            loop100:
            do {
                int alt100=2;
                int LA100_0 = input.LA(1);

                if ( (LA100_0==CASE||LA100_0==DEFAULT) ) {
                    alt100=1;
                }


                switch (alt100) {
            	case 1 :
            	    // grammar/Java.g:940:10: switchBlockStatementGroup
            	    {
            	    pushFollow(FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups4857);
            	    switchBlockStatementGroup343=switchBlockStatementGroup();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, switchBlockStatementGroup343.getTree());

            	    }
            	    break;

            	default :
            	    break loop100;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 71, switchBlockStatementGroups_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "switchBlockStatementGroups"


    public static class switchBlockStatementGroup_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "switchBlockStatementGroup"
    // grammar/Java.g:943:1: switchBlockStatementGroup : switchLabel ( blockStatement )* ;
    public final JavaParser.switchBlockStatementGroup_return switchBlockStatementGroup() throws RecognitionException {
        JavaParser.switchBlockStatementGroup_return retval = new JavaParser.switchBlockStatementGroup_return();
        retval.start = input.LT(1);

        int switchBlockStatementGroup_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.switchLabel_return switchLabel344 =null;

        JavaParser.blockStatement_return blockStatement345 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 72) ) { return retval; }

            // grammar/Java.g:944:5: ( switchLabel ( blockStatement )* )
            // grammar/Java.g:945:9: switchLabel ( blockStatement )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_switchLabel_in_switchBlockStatementGroup4885);
            switchLabel344=switchLabel();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, switchLabel344.getTree());

            // grammar/Java.g:946:9: ( blockStatement )*
            loop101:
            do {
                int alt101=2;
                int LA101_0 = input.LA(1);

                if ( (LA101_0==ABSTRACT||(LA101_0 >= ASSERT && LA101_0 <= BANG)||(LA101_0 >= BOOLEAN && LA101_0 <= BYTE)||(LA101_0 >= CHAR && LA101_0 <= CLASS)||LA101_0==CONTINUE||LA101_0==DO||(LA101_0 >= DOUBLE && LA101_0 <= DOUBLELITERAL)||LA101_0==ENUM||(LA101_0 >= FALSE && LA101_0 <= FINAL)||(LA101_0 >= FLOAT && LA101_0 <= FOR)||(LA101_0 >= IDENTIFIER && LA101_0 <= IF)||(LA101_0 >= INT && LA101_0 <= INTLITERAL)||LA101_0==LBRACE||(LA101_0 >= LONG && LA101_0 <= LPAREN)||(LA101_0 >= MONKEYS_AT && LA101_0 <= NULL)||LA101_0==PLUS||(LA101_0 >= PLUSPLUS && LA101_0 <= PUBLIC)||LA101_0==RETURN||(LA101_0 >= SEMI && LA101_0 <= SHORT)||(LA101_0 >= STATIC && LA101_0 <= SUB)||(LA101_0 >= SUBSUB && LA101_0 <= SYNCHRONIZED)||(LA101_0 >= THIS && LA101_0 <= THROW)||(LA101_0 >= TILDE && LA101_0 <= TRY)||(LA101_0 >= VOID && LA101_0 <= WHILE)) ) {
                    alt101=1;
                }


                switch (alt101) {
            	case 1 :
            	    // grammar/Java.g:946:10: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_switchBlockStatementGroup4896);
            	    blockStatement345=blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, blockStatement345.getTree());

            	    }
            	    break;

            	default :
            	    break loop101;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 72, switchBlockStatementGroup_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "switchBlockStatementGroup"


    public static class switchLabel_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "switchLabel"
    // grammar/Java.g:950:1: switchLabel : ( 'case' expression ':' | 'default' ':' );
    public final JavaParser.switchLabel_return switchLabel() throws RecognitionException {
        JavaParser.switchLabel_return retval = new JavaParser.switchLabel_return();
        retval.start = input.LT(1);

        int switchLabel_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal346=null;
        Token char_literal348=null;
        Token string_literal349=null;
        Token char_literal350=null;
        JavaParser.expression_return expression347 =null;


        CommonTree string_literal346_tree=null;
        CommonTree char_literal348_tree=null;
        CommonTree string_literal349_tree=null;
        CommonTree char_literal350_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 73) ) { return retval; }

            // grammar/Java.g:951:5: ( 'case' expression ':' | 'default' ':' )
            int alt102=2;
            int LA102_0 = input.LA(1);

            if ( (LA102_0==CASE) ) {
                alt102=1;
            }
            else if ( (LA102_0==DEFAULT) ) {
                alt102=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 102, 0, input);

                throw nvae;

            }
            switch (alt102) {
                case 1 :
                    // grammar/Java.g:951:9: 'case' expression ':'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal346=(Token)match(input,CASE,FOLLOW_CASE_in_switchLabel4926); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal346_tree = 
                    (CommonTree)adaptor.create(string_literal346)
                    ;
                    adaptor.addChild(root_0, string_literal346_tree);
                    }

                    pushFollow(FOLLOW_expression_in_switchLabel4928);
                    expression347=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression347.getTree());

                    char_literal348=(Token)match(input,COLON,FOLLOW_COLON_in_switchLabel4930); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal348_tree = 
                    (CommonTree)adaptor.create(char_literal348)
                    ;
                    adaptor.addChild(root_0, char_literal348_tree);
                    }

                    }
                    break;
                case 2 :
                    // grammar/Java.g:952:9: 'default' ':'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal349=(Token)match(input,DEFAULT,FOLLOW_DEFAULT_in_switchLabel4940); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal349_tree = 
                    (CommonTree)adaptor.create(string_literal349)
                    ;
                    adaptor.addChild(root_0, string_literal349_tree);
                    }

                    char_literal350=(Token)match(input,COLON,FOLLOW_COLON_in_switchLabel4942); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal350_tree = 
                    (CommonTree)adaptor.create(char_literal350)
                    ;
                    adaptor.addChild(root_0, char_literal350_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 73, switchLabel_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "switchLabel"


    public static class trystatement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "trystatement"
    // grammar/Java.g:956:1: trystatement : 'try' block ( catches 'finally' block | catches | 'finally' block ) ;
    public final JavaParser.trystatement_return trystatement() throws RecognitionException {
        JavaParser.trystatement_return retval = new JavaParser.trystatement_return();
        retval.start = input.LT(1);

        int trystatement_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal351=null;
        Token string_literal354=null;
        Token string_literal357=null;
        JavaParser.block_return block352 =null;

        JavaParser.catches_return catches353 =null;

        JavaParser.block_return block355 =null;

        JavaParser.catches_return catches356 =null;

        JavaParser.block_return block358 =null;


        CommonTree string_literal351_tree=null;
        CommonTree string_literal354_tree=null;
        CommonTree string_literal357_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 74) ) { return retval; }

            // grammar/Java.g:957:5: ( 'try' block ( catches 'finally' block | catches | 'finally' block ) )
            // grammar/Java.g:957:9: 'try' block ( catches 'finally' block | catches | 'finally' block )
            {
            root_0 = (CommonTree)adaptor.nil();


            string_literal351=(Token)match(input,TRY,FOLLOW_TRY_in_trystatement4962); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal351_tree = 
            (CommonTree)adaptor.create(string_literal351)
            ;
            adaptor.addChild(root_0, string_literal351_tree);
            }

            pushFollow(FOLLOW_block_in_trystatement4964);
            block352=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block352.getTree());

            // grammar/Java.g:958:9: ( catches 'finally' block | catches | 'finally' block )
            int alt103=3;
            int LA103_0 = input.LA(1);

            if ( (LA103_0==CATCH) ) {
                int LA103_1 = input.LA(2);

                if ( (synpred154_Java()) ) {
                    alt103=1;
                }
                else if ( (synpred155_Java()) ) {
                    alt103=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 103, 1, input);

                    throw nvae;

                }
            }
            else if ( (LA103_0==FINALLY) ) {
                alt103=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 103, 0, input);

                throw nvae;

            }
            switch (alt103) {
                case 1 :
                    // grammar/Java.g:958:13: catches 'finally' block
                    {
                    pushFollow(FOLLOW_catches_in_trystatement4978);
                    catches353=catches();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, catches353.getTree());

                    string_literal354=(Token)match(input,FINALLY,FOLLOW_FINALLY_in_trystatement4980); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal354_tree = 
                    (CommonTree)adaptor.create(string_literal354)
                    ;
                    adaptor.addChild(root_0, string_literal354_tree);
                    }

                    pushFollow(FOLLOW_block_in_trystatement4982);
                    block355=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block355.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:959:13: catches
                    {
                    pushFollow(FOLLOW_catches_in_trystatement4996);
                    catches356=catches();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, catches356.getTree());

                    }
                    break;
                case 3 :
                    // grammar/Java.g:960:13: 'finally' block
                    {
                    string_literal357=(Token)match(input,FINALLY,FOLLOW_FINALLY_in_trystatement5010); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal357_tree = 
                    (CommonTree)adaptor.create(string_literal357)
                    ;
                    adaptor.addChild(root_0, string_literal357_tree);
                    }

                    pushFollow(FOLLOW_block_in_trystatement5012);
                    block358=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block358.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 74, trystatement_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "trystatement"


    public static class catches_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "catches"
    // grammar/Java.g:964:1: catches : catchClause ( catchClause )* ;
    public final JavaParser.catches_return catches() throws RecognitionException {
        JavaParser.catches_return retval = new JavaParser.catches_return();
        retval.start = input.LT(1);

        int catches_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.catchClause_return catchClause359 =null;

        JavaParser.catchClause_return catchClause360 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 75) ) { return retval; }

            // grammar/Java.g:965:5: ( catchClause ( catchClause )* )
            // grammar/Java.g:965:9: catchClause ( catchClause )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_catchClause_in_catches5042);
            catchClause359=catchClause();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, catchClause359.getTree());

            // grammar/Java.g:966:9: ( catchClause )*
            loop104:
            do {
                int alt104=2;
                int LA104_0 = input.LA(1);

                if ( (LA104_0==CATCH) ) {
                    alt104=1;
                }


                switch (alt104) {
            	case 1 :
            	    // grammar/Java.g:966:10: catchClause
            	    {
            	    pushFollow(FOLLOW_catchClause_in_catches5053);
            	    catchClause360=catchClause();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, catchClause360.getTree());

            	    }
            	    break;

            	default :
            	    break loop104;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 75, catches_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "catches"


    public static class catchClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "catchClause"
    // grammar/Java.g:970:1: catchClause : 'catch' '(' formalParameter ')' block ;
    public final JavaParser.catchClause_return catchClause() throws RecognitionException {
        JavaParser.catchClause_return retval = new JavaParser.catchClause_return();
        retval.start = input.LT(1);

        int catchClause_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal361=null;
        Token char_literal362=null;
        Token char_literal364=null;
        JavaParser.formalParameter_return formalParameter363 =null;

        JavaParser.block_return block365 =null;


        CommonTree string_literal361_tree=null;
        CommonTree char_literal362_tree=null;
        CommonTree char_literal364_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 76) ) { return retval; }

            // grammar/Java.g:971:5: ( 'catch' '(' formalParameter ')' block )
            // grammar/Java.g:971:9: 'catch' '(' formalParameter ')' block
            {
            root_0 = (CommonTree)adaptor.nil();


            string_literal361=(Token)match(input,CATCH,FOLLOW_CATCH_in_catchClause5083); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal361_tree = 
            (CommonTree)adaptor.create(string_literal361)
            ;
            adaptor.addChild(root_0, string_literal361_tree);
            }

            char_literal362=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_catchClause5085); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal362_tree = 
            (CommonTree)adaptor.create(char_literal362)
            ;
            adaptor.addChild(root_0, char_literal362_tree);
            }

            pushFollow(FOLLOW_formalParameter_in_catchClause5087);
            formalParameter363=formalParameter();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameter363.getTree());

            char_literal364=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_catchClause5097); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal364_tree = 
            (CommonTree)adaptor.create(char_literal364)
            ;
            adaptor.addChild(root_0, char_literal364_tree);
            }

            pushFollow(FOLLOW_block_in_catchClause5099);
            block365=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block365.getTree());

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 76, catchClause_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "catchClause"


    public static class formalParameter_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "formalParameter"
    // grammar/Java.g:975:1: formalParameter : variableModifiers type IDENTIFIER ( '[' ']' )* ;
    public final JavaParser.formalParameter_return formalParameter() throws RecognitionException {
        JavaParser.formalParameter_return retval = new JavaParser.formalParameter_return();
        retval.start = input.LT(1);

        int formalParameter_StartIndex = input.index();

        CommonTree root_0 = null;

        Token IDENTIFIER368=null;
        Token char_literal369=null;
        Token char_literal370=null;
        JavaParser.variableModifiers_return variableModifiers366 =null;

        JavaParser.type_return type367 =null;


        CommonTree IDENTIFIER368_tree=null;
        CommonTree char_literal369_tree=null;
        CommonTree char_literal370_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 77) ) { return retval; }

            // grammar/Java.g:976:5: ( variableModifiers type IDENTIFIER ( '[' ']' )* )
            // grammar/Java.g:976:9: variableModifiers type IDENTIFIER ( '[' ']' )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_variableModifiers_in_formalParameter5118);
            variableModifiers366=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers366.getTree());

            pushFollow(FOLLOW_type_in_formalParameter5120);
            type367=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type367.getTree());

            IDENTIFIER368=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_formalParameter5122); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER368_tree = 
            (CommonTree)adaptor.create(IDENTIFIER368)
            ;
            adaptor.addChild(root_0, IDENTIFIER368_tree);
            }

            // grammar/Java.g:977:9: ( '[' ']' )*
            loop105:
            do {
                int alt105=2;
                int LA105_0 = input.LA(1);

                if ( (LA105_0==LBRACKET) ) {
                    alt105=1;
                }


                switch (alt105) {
            	case 1 :
            	    // grammar/Java.g:977:10: '[' ']'
            	    {
            	    char_literal369=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_formalParameter5136); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal369_tree = 
            	    (CommonTree)adaptor.create(char_literal369)
            	    ;
            	    adaptor.addChild(root_0, char_literal369_tree);
            	    }

            	    char_literal370=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_formalParameter5138); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal370_tree = 
            	    (CommonTree)adaptor.create(char_literal370)
            	    ;
            	    adaptor.addChild(root_0, char_literal370_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop105;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 77, formalParameter_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "formalParameter"


    public static class forstatement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "forstatement"
    // grammar/Java.g:981:1: forstatement : ( 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement | 'for' '(' ( forInit )? ';' ( expression )? ';' ( expressionList )? ')' statement );
    public final JavaParser.forstatement_return forstatement() throws RecognitionException {
        JavaParser.forstatement_return retval = new JavaParser.forstatement_return();
        retval.start = input.LT(1);

        int forstatement_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal371=null;
        Token char_literal372=null;
        Token IDENTIFIER375=null;
        Token char_literal376=null;
        Token char_literal378=null;
        Token string_literal380=null;
        Token char_literal381=null;
        Token char_literal383=null;
        Token char_literal385=null;
        Token char_literal387=null;
        JavaParser.variableModifiers_return variableModifiers373 =null;

        JavaParser.type_return type374 =null;

        JavaParser.expression_return expression377 =null;

        JavaParser.statement_return statement379 =null;

        JavaParser.forInit_return forInit382 =null;

        JavaParser.expression_return expression384 =null;

        JavaParser.expressionList_return expressionList386 =null;

        JavaParser.statement_return statement388 =null;


        CommonTree string_literal371_tree=null;
        CommonTree char_literal372_tree=null;
        CommonTree IDENTIFIER375_tree=null;
        CommonTree char_literal376_tree=null;
        CommonTree char_literal378_tree=null;
        CommonTree string_literal380_tree=null;
        CommonTree char_literal381_tree=null;
        CommonTree char_literal383_tree=null;
        CommonTree char_literal385_tree=null;
        CommonTree char_literal387_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 78) ) { return retval; }

            // grammar/Java.g:982:5: ( 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement | 'for' '(' ( forInit )? ';' ( expression )? ';' ( expressionList )? ')' statement )
            int alt109=2;
            int LA109_0 = input.LA(1);

            if ( (LA109_0==FOR) ) {
                int LA109_1 = input.LA(2);

                if ( (synpred158_Java()) ) {
                    alt109=1;
                }
                else if ( (true) ) {
                    alt109=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 109, 1, input);

                    throw nvae;

                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 109, 0, input);

                throw nvae;

            }
            switch (alt109) {
                case 1 :
                    // grammar/Java.g:984:9: 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal371=(Token)match(input,FOR,FOLLOW_FOR_in_forstatement5183); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal371_tree = 
                    (CommonTree)adaptor.create(string_literal371)
                    ;
                    adaptor.addChild(root_0, string_literal371_tree);
                    }

                    char_literal372=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_forstatement5185); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal372_tree = 
                    (CommonTree)adaptor.create(char_literal372)
                    ;
                    adaptor.addChild(root_0, char_literal372_tree);
                    }

                    pushFollow(FOLLOW_variableModifiers_in_forstatement5187);
                    variableModifiers373=variableModifiers();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers373.getTree());

                    pushFollow(FOLLOW_type_in_forstatement5189);
                    type374=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type374.getTree());

                    IDENTIFIER375=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_forstatement5191); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER375_tree = 
                    (CommonTree)adaptor.create(IDENTIFIER375)
                    ;
                    adaptor.addChild(root_0, IDENTIFIER375_tree);
                    }

                    char_literal376=(Token)match(input,COLON,FOLLOW_COLON_in_forstatement5193); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal376_tree = 
                    (CommonTree)adaptor.create(char_literal376)
                    ;
                    adaptor.addChild(root_0, char_literal376_tree);
                    }

                    pushFollow(FOLLOW_expression_in_forstatement5209);
                    expression377=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression377.getTree());

                    char_literal378=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_forstatement5211); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal378_tree = 
                    (CommonTree)adaptor.create(char_literal378)
                    ;
                    adaptor.addChild(root_0, char_literal378_tree);
                    }

                    pushFollow(FOLLOW_statement_in_forstatement5213);
                    statement379=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement379.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:988:9: 'for' '(' ( forInit )? ';' ( expression )? ';' ( expressionList )? ')' statement
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal380=(Token)match(input,FOR,FOLLOW_FOR_in_forstatement5233); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal380_tree = 
                    (CommonTree)adaptor.create(string_literal380)
                    ;
                    adaptor.addChild(root_0, string_literal380_tree);
                    }

                    char_literal381=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_forstatement5235); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal381_tree = 
                    (CommonTree)adaptor.create(char_literal381)
                    ;
                    adaptor.addChild(root_0, char_literal381_tree);
                    }

                    // grammar/Java.g:989:17: ( forInit )?
                    int alt106=2;
                    int LA106_0 = input.LA(1);

                    if ( (LA106_0==EOF||LA106_0==BANG||LA106_0==BOOLEAN||LA106_0==BYTE||(LA106_0 >= CHAR && LA106_0 <= CHARLITERAL)||(LA106_0 >= DOUBLE && LA106_0 <= DOUBLELITERAL)||(LA106_0 >= FALSE && LA106_0 <= FINAL)||(LA106_0 >= FLOAT && LA106_0 <= FLOATLITERAL)||LA106_0==IDENTIFIER||LA106_0==INT||LA106_0==INTLITERAL||(LA106_0 >= LONG && LA106_0 <= LPAREN)||LA106_0==MONKEYS_AT||(LA106_0 >= NEW && LA106_0 <= NULL)||LA106_0==PLUS||LA106_0==PLUSPLUS||LA106_0==SHORT||(LA106_0 >= STRINGLITERAL && LA106_0 <= SUB)||(LA106_0 >= SUBSUB && LA106_0 <= SUPER)||LA106_0==THIS||LA106_0==TILDE||LA106_0==TRUE||LA106_0==VOID) ) {
                        alt106=1;
                    }
                    switch (alt106) {
                        case 1 :
                            // grammar/Java.g:989:18: forInit
                            {
                            pushFollow(FOLLOW_forInit_in_forstatement5254);
                            forInit382=forInit();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, forInit382.getTree());

                            }
                            break;

                    }


                    char_literal383=(Token)match(input,SEMI,FOLLOW_SEMI_in_forstatement5275); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal383_tree = 
                    (CommonTree)adaptor.create(char_literal383)
                    ;
                    adaptor.addChild(root_0, char_literal383_tree);
                    }

                    // grammar/Java.g:991:17: ( expression )?
                    int alt107=2;
                    int LA107_0 = input.LA(1);

                    if ( (LA107_0==BANG||LA107_0==BOOLEAN||LA107_0==BYTE||(LA107_0 >= CHAR && LA107_0 <= CHARLITERAL)||(LA107_0 >= DOUBLE && LA107_0 <= DOUBLELITERAL)||LA107_0==FALSE||(LA107_0 >= FLOAT && LA107_0 <= FLOATLITERAL)||LA107_0==IDENTIFIER||LA107_0==INT||LA107_0==INTLITERAL||(LA107_0 >= LONG && LA107_0 <= LPAREN)||(LA107_0 >= NEW && LA107_0 <= NULL)||LA107_0==PLUS||LA107_0==PLUSPLUS||LA107_0==SHORT||(LA107_0 >= STRINGLITERAL && LA107_0 <= SUB)||(LA107_0 >= SUBSUB && LA107_0 <= SUPER)||LA107_0==THIS||LA107_0==TILDE||LA107_0==TRUE||LA107_0==VOID) ) {
                        alt107=1;
                    }
                    switch (alt107) {
                        case 1 :
                            // grammar/Java.g:991:18: expression
                            {
                            pushFollow(FOLLOW_expression_in_forstatement5294);
                            expression384=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression384.getTree());

                            }
                            break;

                    }


                    char_literal385=(Token)match(input,SEMI,FOLLOW_SEMI_in_forstatement5315); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal385_tree = 
                    (CommonTree)adaptor.create(char_literal385)
                    ;
                    adaptor.addChild(root_0, char_literal385_tree);
                    }

                    // grammar/Java.g:993:17: ( expressionList )?
                    int alt108=2;
                    int LA108_0 = input.LA(1);

                    if ( (LA108_0==BANG||LA108_0==BOOLEAN||LA108_0==BYTE||(LA108_0 >= CHAR && LA108_0 <= CHARLITERAL)||(LA108_0 >= DOUBLE && LA108_0 <= DOUBLELITERAL)||LA108_0==FALSE||(LA108_0 >= FLOAT && LA108_0 <= FLOATLITERAL)||LA108_0==IDENTIFIER||LA108_0==INT||LA108_0==INTLITERAL||(LA108_0 >= LONG && LA108_0 <= LPAREN)||(LA108_0 >= NEW && LA108_0 <= NULL)||LA108_0==PLUS||LA108_0==PLUSPLUS||LA108_0==SHORT||(LA108_0 >= STRINGLITERAL && LA108_0 <= SUB)||(LA108_0 >= SUBSUB && LA108_0 <= SUPER)||LA108_0==THIS||LA108_0==TILDE||LA108_0==TRUE||LA108_0==VOID) ) {
                        alt108=1;
                    }
                    switch (alt108) {
                        case 1 :
                            // grammar/Java.g:993:18: expressionList
                            {
                            pushFollow(FOLLOW_expressionList_in_forstatement5334);
                            expressionList386=expressionList();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList386.getTree());

                            }
                            break;

                    }


                    char_literal387=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_forstatement5355); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal387_tree = 
                    (CommonTree)adaptor.create(char_literal387)
                    ;
                    adaptor.addChild(root_0, char_literal387_tree);
                    }

                    pushFollow(FOLLOW_statement_in_forstatement5357);
                    statement388=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement388.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 78, forstatement_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "forstatement"


    public static class forInit_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "forInit"
    // grammar/Java.g:997:1: forInit : ( localVariableDeclaration | expressionList );
    public final JavaParser.forInit_return forInit() throws RecognitionException {
        JavaParser.forInit_return retval = new JavaParser.forInit_return();
        retval.start = input.LT(1);

        int forInit_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.localVariableDeclaration_return localVariableDeclaration389 =null;

        JavaParser.expressionList_return expressionList390 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 79) ) { return retval; }

            // grammar/Java.g:998:5: ( localVariableDeclaration | expressionList )
            int alt110=2;
            switch ( input.LA(1) ) {
            case FINAL:
            case MONKEYS_AT:
                {
                alt110=1;
                }
                break;
            case IDENTIFIER:
                {
                int LA110_3 = input.LA(2);

                if ( (synpred162_Java()) ) {
                    alt110=1;
                }
                else if ( (true) ) {
                    alt110=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 110, 3, input);

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
                int LA110_4 = input.LA(2);

                if ( (synpred162_Java()) ) {
                    alt110=1;
                }
                else if ( (true) ) {
                    alt110=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 110, 4, input);

                    throw nvae;

                }
                }
                break;
            case BANG:
            case CHARLITERAL:
            case DOUBLELITERAL:
            case FALSE:
            case FLOATLITERAL:
            case INTLITERAL:
            case LONGLITERAL:
            case LPAREN:
            case NEW:
            case NULL:
            case PLUS:
            case PLUSPLUS:
            case STRINGLITERAL:
            case SUB:
            case SUBSUB:
            case SUPER:
            case THIS:
            case TILDE:
            case TRUE:
            case VOID:
                {
                alt110=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 110, 0, input);

                throw nvae;

            }

            switch (alt110) {
                case 1 :
                    // grammar/Java.g:998:9: localVariableDeclaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_localVariableDeclaration_in_forInit5376);
                    localVariableDeclaration389=localVariableDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, localVariableDeclaration389.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:999:9: expressionList
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_expressionList_in_forInit5386);
                    expressionList390=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList390.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 79, forInit_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "forInit"


    public static class parExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "parExpression"
    // grammar/Java.g:1002:1: parExpression : '(' expression ')' ;
    public final JavaParser.parExpression_return parExpression() throws RecognitionException {
        JavaParser.parExpression_return retval = new JavaParser.parExpression_return();
        retval.start = input.LT(1);

        int parExpression_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal391=null;
        Token char_literal393=null;
        JavaParser.expression_return expression392 =null;


        CommonTree char_literal391_tree=null;
        CommonTree char_literal393_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 80) ) { return retval; }

            // grammar/Java.g:1003:5: ( '(' expression ')' )
            // grammar/Java.g:1003:9: '(' expression ')'
            {
            root_0 = (CommonTree)adaptor.nil();


            char_literal391=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_parExpression5405); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal391_tree = 
            (CommonTree)adaptor.create(char_literal391)
            ;
            adaptor.addChild(root_0, char_literal391_tree);
            }

            pushFollow(FOLLOW_expression_in_parExpression5407);
            expression392=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression392.getTree());

            char_literal393=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_parExpression5409); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal393_tree = 
            (CommonTree)adaptor.create(char_literal393)
            ;
            adaptor.addChild(root_0, char_literal393_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 80, parExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "parExpression"


    public static class expressionList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expressionList"
    // grammar/Java.g:1006:1: expressionList : expression ( ',' expression )* ;
    public final JavaParser.expressionList_return expressionList() throws RecognitionException {
        JavaParser.expressionList_return retval = new JavaParser.expressionList_return();
        retval.start = input.LT(1);

        int expressionList_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal395=null;
        JavaParser.expression_return expression394 =null;

        JavaParser.expression_return expression396 =null;


        CommonTree char_literal395_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 81) ) { return retval; }

            // grammar/Java.g:1007:5: ( expression ( ',' expression )* )
            // grammar/Java.g:1007:9: expression ( ',' expression )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_expression_in_expressionList5428);
            expression394=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression394.getTree());

            // grammar/Java.g:1008:9: ( ',' expression )*
            loop111:
            do {
                int alt111=2;
                int LA111_0 = input.LA(1);

                if ( (LA111_0==COMMA) ) {
                    alt111=1;
                }


                switch (alt111) {
            	case 1 :
            	    // grammar/Java.g:1008:10: ',' expression
            	    {
            	    char_literal395=(Token)match(input,COMMA,FOLLOW_COMMA_in_expressionList5439); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal395_tree = 
            	    (CommonTree)adaptor.create(char_literal395)
            	    ;
            	    adaptor.addChild(root_0, char_literal395_tree);
            	    }

            	    pushFollow(FOLLOW_expression_in_expressionList5441);
            	    expression396=expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression396.getTree());

            	    }
            	    break;

            	default :
            	    break loop111;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 81, expressionList_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "expressionList"


    public static class expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expression"
    // grammar/Java.g:1013:1: expression : conditionalExpression ( assignmentOperator expression )? -> ^( EXP_T conditionalExpression ( assignmentOperator expression )? ) ;
    public final JavaParser.expression_return expression() throws RecognitionException {
        JavaParser.expression_return retval = new JavaParser.expression_return();
        retval.start = input.LT(1);

        int expression_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.conditionalExpression_return conditionalExpression397 =null;

        JavaParser.assignmentOperator_return assignmentOperator398 =null;

        JavaParser.expression_return expression399 =null;


        RewriteRuleSubtreeStream stream_assignmentOperator=new RewriteRuleSubtreeStream(adaptor,"rule assignmentOperator");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_conditionalExpression=new RewriteRuleSubtreeStream(adaptor,"rule conditionalExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 82) ) { return retval; }

            // grammar/Java.g:1014:5: ( conditionalExpression ( assignmentOperator expression )? -> ^( EXP_T conditionalExpression ( assignmentOperator expression )? ) )
            // grammar/Java.g:1014:9: conditionalExpression ( assignmentOperator expression )?
            {
            pushFollow(FOLLOW_conditionalExpression_in_expression5472);
            conditionalExpression397=conditionalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_conditionalExpression.add(conditionalExpression397.getTree());

            // grammar/Java.g:1015:9: ( assignmentOperator expression )?
            int alt112=2;
            int LA112_0 = input.LA(1);

            if ( (LA112_0==AMPEQ||LA112_0==BAREQ||LA112_0==CARETEQ||LA112_0==EQ||LA112_0==GT||LA112_0==LT||LA112_0==PERCENTEQ||LA112_0==PLUSEQ||LA112_0==SLASHEQ||LA112_0==STAREQ||LA112_0==SUBEQ) ) {
                alt112=1;
            }
            switch (alt112) {
                case 1 :
                    // grammar/Java.g:1015:10: assignmentOperator expression
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression5483);
                    assignmentOperator398=assignmentOperator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_assignmentOperator.add(assignmentOperator398.getTree());

                    pushFollow(FOLLOW_expression_in_expression5485);
                    expression399=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression399.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: conditionalExpression, expression, assignmentOperator
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 1016:13: -> ^( EXP_T conditionalExpression ( assignmentOperator expression )? )
            {
                // grammar/Java.g:1016:16: ^( EXP_T conditionalExpression ( assignmentOperator expression )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(EXP_T, "EXP_T")
                , root_1);

                adaptor.addChild(root_1, stream_conditionalExpression.nextTree());

                // grammar/Java.g:1016:46: ( assignmentOperator expression )?
                if ( stream_expression.hasNext()||stream_assignmentOperator.hasNext() ) {
                    adaptor.addChild(root_1, stream_assignmentOperator.nextTree());

                    adaptor.addChild(root_1, stream_expression.nextTree());

                }
                stream_expression.reset();
                stream_assignmentOperator.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 82, expression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "expression"


    public static class assignmentOperator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "assignmentOperator"
    // grammar/Java.g:1020:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '>' '=' | '>' '>' '=' );
    public final JavaParser.assignmentOperator_return assignmentOperator() throws RecognitionException {
        JavaParser.assignmentOperator_return retval = new JavaParser.assignmentOperator_return();
        retval.start = input.LT(1);

        int assignmentOperator_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal400=null;
        Token string_literal401=null;
        Token string_literal402=null;
        Token string_literal403=null;
        Token string_literal404=null;
        Token string_literal405=null;
        Token string_literal406=null;
        Token string_literal407=null;
        Token string_literal408=null;
        Token char_literal409=null;
        Token char_literal410=null;
        Token char_literal411=null;
        Token char_literal412=null;
        Token char_literal413=null;
        Token char_literal414=null;
        Token char_literal415=null;
        Token char_literal416=null;
        Token char_literal417=null;
        Token char_literal418=null;

        CommonTree char_literal400_tree=null;
        CommonTree string_literal401_tree=null;
        CommonTree string_literal402_tree=null;
        CommonTree string_literal403_tree=null;
        CommonTree string_literal404_tree=null;
        CommonTree string_literal405_tree=null;
        CommonTree string_literal406_tree=null;
        CommonTree string_literal407_tree=null;
        CommonTree string_literal408_tree=null;
        CommonTree char_literal409_tree=null;
        CommonTree char_literal410_tree=null;
        CommonTree char_literal411_tree=null;
        CommonTree char_literal412_tree=null;
        CommonTree char_literal413_tree=null;
        CommonTree char_literal414_tree=null;
        CommonTree char_literal415_tree=null;
        CommonTree char_literal416_tree=null;
        CommonTree char_literal417_tree=null;
        CommonTree char_literal418_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 83) ) { return retval; }

            // grammar/Java.g:1021:5: ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '>' '=' | '>' '>' '=' )
            int alt113=12;
            switch ( input.LA(1) ) {
            case EQ:
                {
                alt113=1;
                }
                break;
            case PLUSEQ:
                {
                alt113=2;
                }
                break;
            case SUBEQ:
                {
                alt113=3;
                }
                break;
            case STAREQ:
                {
                alt113=4;
                }
                break;
            case SLASHEQ:
                {
                alt113=5;
                }
                break;
            case AMPEQ:
                {
                alt113=6;
                }
                break;
            case BAREQ:
                {
                alt113=7;
                }
                break;
            case CARETEQ:
                {
                alt113=8;
                }
                break;
            case PERCENTEQ:
                {
                alt113=9;
                }
                break;
            case LT:
                {
                alt113=10;
                }
                break;
            case GT:
                {
                int LA113_11 = input.LA(2);

                if ( (LA113_11==GT) ) {
                    int LA113_12 = input.LA(3);

                    if ( (LA113_12==GT) ) {
                        alt113=11;
                    }
                    else if ( (LA113_12==EQ) ) {
                        alt113=12;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 113, 12, input);

                        throw nvae;

                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 113, 11, input);

                    throw nvae;

                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 113, 0, input);

                throw nvae;

            }

            switch (alt113) {
                case 1 :
                    // grammar/Java.g:1021:9: '='
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal400=(Token)match(input,EQ,FOLLOW_EQ_in_assignmentOperator5534); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal400_tree = 
                    (CommonTree)adaptor.create(char_literal400)
                    ;
                    adaptor.addChild(root_0, char_literal400_tree);
                    }

                    }
                    break;
                case 2 :
                    // grammar/Java.g:1022:9: '+='
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal401=(Token)match(input,PLUSEQ,FOLLOW_PLUSEQ_in_assignmentOperator5544); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal401_tree = 
                    (CommonTree)adaptor.create(string_literal401)
                    ;
                    adaptor.addChild(root_0, string_literal401_tree);
                    }

                    }
                    break;
                case 3 :
                    // grammar/Java.g:1023:9: '-='
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal402=(Token)match(input,SUBEQ,FOLLOW_SUBEQ_in_assignmentOperator5554); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal402_tree = 
                    (CommonTree)adaptor.create(string_literal402)
                    ;
                    adaptor.addChild(root_0, string_literal402_tree);
                    }

                    }
                    break;
                case 4 :
                    // grammar/Java.g:1024:9: '*='
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal403=(Token)match(input,STAREQ,FOLLOW_STAREQ_in_assignmentOperator5564); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal403_tree = 
                    (CommonTree)adaptor.create(string_literal403)
                    ;
                    adaptor.addChild(root_0, string_literal403_tree);
                    }

                    }
                    break;
                case 5 :
                    // grammar/Java.g:1025:9: '/='
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal404=(Token)match(input,SLASHEQ,FOLLOW_SLASHEQ_in_assignmentOperator5574); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal404_tree = 
                    (CommonTree)adaptor.create(string_literal404)
                    ;
                    adaptor.addChild(root_0, string_literal404_tree);
                    }

                    }
                    break;
                case 6 :
                    // grammar/Java.g:1026:9: '&='
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal405=(Token)match(input,AMPEQ,FOLLOW_AMPEQ_in_assignmentOperator5584); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal405_tree = 
                    (CommonTree)adaptor.create(string_literal405)
                    ;
                    adaptor.addChild(root_0, string_literal405_tree);
                    }

                    }
                    break;
                case 7 :
                    // grammar/Java.g:1027:9: '|='
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal406=(Token)match(input,BAREQ,FOLLOW_BAREQ_in_assignmentOperator5594); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal406_tree = 
                    (CommonTree)adaptor.create(string_literal406)
                    ;
                    adaptor.addChild(root_0, string_literal406_tree);
                    }

                    }
                    break;
                case 8 :
                    // grammar/Java.g:1028:9: '^='
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal407=(Token)match(input,CARETEQ,FOLLOW_CARETEQ_in_assignmentOperator5604); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal407_tree = 
                    (CommonTree)adaptor.create(string_literal407)
                    ;
                    adaptor.addChild(root_0, string_literal407_tree);
                    }

                    }
                    break;
                case 9 :
                    // grammar/Java.g:1029:9: '%='
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal408=(Token)match(input,PERCENTEQ,FOLLOW_PERCENTEQ_in_assignmentOperator5614); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal408_tree = 
                    (CommonTree)adaptor.create(string_literal408)
                    ;
                    adaptor.addChild(root_0, string_literal408_tree);
                    }

                    }
                    break;
                case 10 :
                    // grammar/Java.g:1030:10: '<' '<' '='
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal409=(Token)match(input,LT,FOLLOW_LT_in_assignmentOperator5625); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal409_tree = 
                    (CommonTree)adaptor.create(char_literal409)
                    ;
                    adaptor.addChild(root_0, char_literal409_tree);
                    }

                    char_literal410=(Token)match(input,LT,FOLLOW_LT_in_assignmentOperator5627); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal410_tree = 
                    (CommonTree)adaptor.create(char_literal410)
                    ;
                    adaptor.addChild(root_0, char_literal410_tree);
                    }

                    char_literal411=(Token)match(input,EQ,FOLLOW_EQ_in_assignmentOperator5629); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal411_tree = 
                    (CommonTree)adaptor.create(char_literal411)
                    ;
                    adaptor.addChild(root_0, char_literal411_tree);
                    }

                    }
                    break;
                case 11 :
                    // grammar/Java.g:1031:10: '>' '>' '>' '='
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal412=(Token)match(input,GT,FOLLOW_GT_in_assignmentOperator5640); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal412_tree = 
                    (CommonTree)adaptor.create(char_literal412)
                    ;
                    adaptor.addChild(root_0, char_literal412_tree);
                    }

                    char_literal413=(Token)match(input,GT,FOLLOW_GT_in_assignmentOperator5642); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal413_tree = 
                    (CommonTree)adaptor.create(char_literal413)
                    ;
                    adaptor.addChild(root_0, char_literal413_tree);
                    }

                    char_literal414=(Token)match(input,GT,FOLLOW_GT_in_assignmentOperator5644); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal414_tree = 
                    (CommonTree)adaptor.create(char_literal414)
                    ;
                    adaptor.addChild(root_0, char_literal414_tree);
                    }

                    char_literal415=(Token)match(input,EQ,FOLLOW_EQ_in_assignmentOperator5646); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal415_tree = 
                    (CommonTree)adaptor.create(char_literal415)
                    ;
                    adaptor.addChild(root_0, char_literal415_tree);
                    }

                    }
                    break;
                case 12 :
                    // grammar/Java.g:1032:10: '>' '>' '='
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal416=(Token)match(input,GT,FOLLOW_GT_in_assignmentOperator5657); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal416_tree = 
                    (CommonTree)adaptor.create(char_literal416)
                    ;
                    adaptor.addChild(root_0, char_literal416_tree);
                    }

                    char_literal417=(Token)match(input,GT,FOLLOW_GT_in_assignmentOperator5659); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal417_tree = 
                    (CommonTree)adaptor.create(char_literal417)
                    ;
                    adaptor.addChild(root_0, char_literal417_tree);
                    }

                    char_literal418=(Token)match(input,EQ,FOLLOW_EQ_in_assignmentOperator5661); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal418_tree = 
                    (CommonTree)adaptor.create(char_literal418)
                    ;
                    adaptor.addChild(root_0, char_literal418_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 83, assignmentOperator_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "assignmentOperator"


    public static class conditionalExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "conditionalExpression"
    // grammar/Java.g:1036:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' conditionalExpression )? ;
    public final JavaParser.conditionalExpression_return conditionalExpression() throws RecognitionException {
        JavaParser.conditionalExpression_return retval = new JavaParser.conditionalExpression_return();
        retval.start = input.LT(1);

        int conditionalExpression_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal420=null;
        Token char_literal422=null;
        JavaParser.conditionalOrExpression_return conditionalOrExpression419 =null;

        JavaParser.expression_return expression421 =null;

        JavaParser.conditionalExpression_return conditionalExpression423 =null;


        CommonTree char_literal420_tree=null;
        CommonTree char_literal422_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 84) ) { return retval; }

            // grammar/Java.g:1037:5: ( conditionalOrExpression ( '?' expression ':' conditionalExpression )? )
            // grammar/Java.g:1037:9: conditionalOrExpression ( '?' expression ':' conditionalExpression )?
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression5681);
            conditionalOrExpression419=conditionalOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalOrExpression419.getTree());

            // grammar/Java.g:1038:9: ( '?' expression ':' conditionalExpression )?
            int alt114=2;
            int LA114_0 = input.LA(1);

            if ( (LA114_0==QUES) ) {
                alt114=1;
            }
            switch (alt114) {
                case 1 :
                    // grammar/Java.g:1038:10: '?' expression ':' conditionalExpression
                    {
                    char_literal420=(Token)match(input,QUES,FOLLOW_QUES_in_conditionalExpression5692); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal420_tree = 
                    (CommonTree)adaptor.create(char_literal420)
                    ;
                    adaptor.addChild(root_0, char_literal420_tree);
                    }

                    pushFollow(FOLLOW_expression_in_conditionalExpression5694);
                    expression421=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression421.getTree());

                    char_literal422=(Token)match(input,COLON,FOLLOW_COLON_in_conditionalExpression5696); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal422_tree = 
                    (CommonTree)adaptor.create(char_literal422)
                    ;
                    adaptor.addChild(root_0, char_literal422_tree);
                    }

                    pushFollow(FOLLOW_conditionalExpression_in_conditionalExpression5698);
                    conditionalExpression423=conditionalExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression423.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 84, conditionalExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "conditionalExpression"


    public static class conditionalOrExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "conditionalOrExpression"
    // grammar/Java.g:1042:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
    public final JavaParser.conditionalOrExpression_return conditionalOrExpression() throws RecognitionException {
        JavaParser.conditionalOrExpression_return retval = new JavaParser.conditionalOrExpression_return();
        retval.start = input.LT(1);

        int conditionalOrExpression_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal425=null;
        JavaParser.conditionalAndExpression_return conditionalAndExpression424 =null;

        JavaParser.conditionalAndExpression_return conditionalAndExpression426 =null;


        CommonTree string_literal425_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 85) ) { return retval; }

            // grammar/Java.g:1043:5: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
            // grammar/Java.g:1043:9: conditionalAndExpression ( '||' conditionalAndExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression5728);
            conditionalAndExpression424=conditionalAndExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression424.getTree());

            // grammar/Java.g:1044:9: ( '||' conditionalAndExpression )*
            loop115:
            do {
                int alt115=2;
                int LA115_0 = input.LA(1);

                if ( (LA115_0==BARBAR) ) {
                    alt115=1;
                }


                switch (alt115) {
            	case 1 :
            	    // grammar/Java.g:1044:10: '||' conditionalAndExpression
            	    {
            	    string_literal425=(Token)match(input,BARBAR,FOLLOW_BARBAR_in_conditionalOrExpression5739); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal425_tree = 
            	    (CommonTree)adaptor.create(string_literal425)
            	    ;
            	    adaptor.addChild(root_0, string_literal425_tree);
            	    }

            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression5741);
            	    conditionalAndExpression426=conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression426.getTree());

            	    }
            	    break;

            	default :
            	    break loop115;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 85, conditionalOrExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "conditionalOrExpression"


    public static class conditionalAndExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "conditionalAndExpression"
    // grammar/Java.g:1048:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
    public final JavaParser.conditionalAndExpression_return conditionalAndExpression() throws RecognitionException {
        JavaParser.conditionalAndExpression_return retval = new JavaParser.conditionalAndExpression_return();
        retval.start = input.LT(1);

        int conditionalAndExpression_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal428=null;
        JavaParser.inclusiveOrExpression_return inclusiveOrExpression427 =null;

        JavaParser.inclusiveOrExpression_return inclusiveOrExpression429 =null;


        CommonTree string_literal428_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 86) ) { return retval; }

            // grammar/Java.g:1049:5: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
            // grammar/Java.g:1049:9: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5771);
            inclusiveOrExpression427=inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression427.getTree());

            // grammar/Java.g:1050:9: ( '&&' inclusiveOrExpression )*
            loop116:
            do {
                int alt116=2;
                int LA116_0 = input.LA(1);

                if ( (LA116_0==AMPAMP) ) {
                    alt116=1;
                }


                switch (alt116) {
            	case 1 :
            	    // grammar/Java.g:1050:10: '&&' inclusiveOrExpression
            	    {
            	    string_literal428=(Token)match(input,AMPAMP,FOLLOW_AMPAMP_in_conditionalAndExpression5782); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal428_tree = 
            	    (CommonTree)adaptor.create(string_literal428)
            	    ;
            	    adaptor.addChild(root_0, string_literal428_tree);
            	    }

            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5784);
            	    inclusiveOrExpression429=inclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression429.getTree());

            	    }
            	    break;

            	default :
            	    break loop116;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 86, conditionalAndExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "conditionalAndExpression"


    public static class inclusiveOrExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "inclusiveOrExpression"
    // grammar/Java.g:1054:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
    public final JavaParser.inclusiveOrExpression_return inclusiveOrExpression() throws RecognitionException {
        JavaParser.inclusiveOrExpression_return retval = new JavaParser.inclusiveOrExpression_return();
        retval.start = input.LT(1);

        int inclusiveOrExpression_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal431=null;
        JavaParser.exclusiveOrExpression_return exclusiveOrExpression430 =null;

        JavaParser.exclusiveOrExpression_return exclusiveOrExpression432 =null;


        CommonTree char_literal431_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 87) ) { return retval; }

            // grammar/Java.g:1055:5: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
            // grammar/Java.g:1055:9: exclusiveOrExpression ( '|' exclusiveOrExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5814);
            exclusiveOrExpression430=exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression430.getTree());

            // grammar/Java.g:1056:9: ( '|' exclusiveOrExpression )*
            loop117:
            do {
                int alt117=2;
                int LA117_0 = input.LA(1);

                if ( (LA117_0==BAR) ) {
                    alt117=1;
                }


                switch (alt117) {
            	case 1 :
            	    // grammar/Java.g:1056:10: '|' exclusiveOrExpression
            	    {
            	    char_literal431=(Token)match(input,BAR,FOLLOW_BAR_in_inclusiveOrExpression5825); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal431_tree = 
            	    (CommonTree)adaptor.create(char_literal431)
            	    ;
            	    adaptor.addChild(root_0, char_literal431_tree);
            	    }

            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5827);
            	    exclusiveOrExpression432=exclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression432.getTree());

            	    }
            	    break;

            	default :
            	    break loop117;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 87, inclusiveOrExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "inclusiveOrExpression"


    public static class exclusiveOrExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "exclusiveOrExpression"
    // grammar/Java.g:1060:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
    public final JavaParser.exclusiveOrExpression_return exclusiveOrExpression() throws RecognitionException {
        JavaParser.exclusiveOrExpression_return retval = new JavaParser.exclusiveOrExpression_return();
        retval.start = input.LT(1);

        int exclusiveOrExpression_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal434=null;
        JavaParser.andExpression_return andExpression433 =null;

        JavaParser.andExpression_return andExpression435 =null;


        CommonTree char_literal434_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 88) ) { return retval; }

            // grammar/Java.g:1061:5: ( andExpression ( '^' andExpression )* )
            // grammar/Java.g:1061:9: andExpression ( '^' andExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression5857);
            andExpression433=andExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression433.getTree());

            // grammar/Java.g:1062:9: ( '^' andExpression )*
            loop118:
            do {
                int alt118=2;
                int LA118_0 = input.LA(1);

                if ( (LA118_0==CARET) ) {
                    alt118=1;
                }


                switch (alt118) {
            	case 1 :
            	    // grammar/Java.g:1062:10: '^' andExpression
            	    {
            	    char_literal434=(Token)match(input,CARET,FOLLOW_CARET_in_exclusiveOrExpression5868); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal434_tree = 
            	    (CommonTree)adaptor.create(char_literal434)
            	    ;
            	    adaptor.addChild(root_0, char_literal434_tree);
            	    }

            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression5870);
            	    andExpression435=andExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression435.getTree());

            	    }
            	    break;

            	default :
            	    break loop118;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 88, exclusiveOrExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "exclusiveOrExpression"


    public static class andExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "andExpression"
    // grammar/Java.g:1066:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
    public final JavaParser.andExpression_return andExpression() throws RecognitionException {
        JavaParser.andExpression_return retval = new JavaParser.andExpression_return();
        retval.start = input.LT(1);

        int andExpression_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal437=null;
        JavaParser.equalityExpression_return equalityExpression436 =null;

        JavaParser.equalityExpression_return equalityExpression438 =null;


        CommonTree char_literal437_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 89) ) { return retval; }

            // grammar/Java.g:1067:5: ( equalityExpression ( '&' equalityExpression )* )
            // grammar/Java.g:1067:9: equalityExpression ( '&' equalityExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_equalityExpression_in_andExpression5900);
            equalityExpression436=equalityExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression436.getTree());

            // grammar/Java.g:1068:9: ( '&' equalityExpression )*
            loop119:
            do {
                int alt119=2;
                int LA119_0 = input.LA(1);

                if ( (LA119_0==AMP) ) {
                    alt119=1;
                }


                switch (alt119) {
            	case 1 :
            	    // grammar/Java.g:1068:10: '&' equalityExpression
            	    {
            	    char_literal437=(Token)match(input,AMP,FOLLOW_AMP_in_andExpression5911); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal437_tree = 
            	    (CommonTree)adaptor.create(char_literal437)
            	    ;
            	    adaptor.addChild(root_0, char_literal437_tree);
            	    }

            	    pushFollow(FOLLOW_equalityExpression_in_andExpression5913);
            	    equalityExpression438=equalityExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression438.getTree());

            	    }
            	    break;

            	default :
            	    break loop119;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 89, andExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "andExpression"


    public static class equalityExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "equalityExpression"
    // grammar/Java.g:1072:1: equalityExpression : instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* ;
    public final JavaParser.equalityExpression_return equalityExpression() throws RecognitionException {
        JavaParser.equalityExpression_return retval = new JavaParser.equalityExpression_return();
        retval.start = input.LT(1);

        int equalityExpression_StartIndex = input.index();

        CommonTree root_0 = null;

        Token set440=null;
        JavaParser.instanceOfExpression_return instanceOfExpression439 =null;

        JavaParser.instanceOfExpression_return instanceOfExpression441 =null;


        CommonTree set440_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 90) ) { return retval; }

            // grammar/Java.g:1073:5: ( instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* )
            // grammar/Java.g:1073:9: instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression5943);
            instanceOfExpression439=instanceOfExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, instanceOfExpression439.getTree());

            // grammar/Java.g:1074:9: ( ( '==' | '!=' ) instanceOfExpression )*
            loop120:
            do {
                int alt120=2;
                int LA120_0 = input.LA(1);

                if ( (LA120_0==BANGEQ||LA120_0==EQEQ) ) {
                    alt120=1;
                }


                switch (alt120) {
            	case 1 :
            	    // grammar/Java.g:1075:13: ( '==' | '!=' ) instanceOfExpression
            	    {
            	    set440=(Token)input.LT(1);

            	    if ( input.LA(1)==BANGEQ||input.LA(1)==EQEQ ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, 
            	        (CommonTree)adaptor.create(set440)
            	        );
            	        state.errorRecovery=false;
            	        state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression6017);
            	    instanceOfExpression441=instanceOfExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, instanceOfExpression441.getTree());

            	    }
            	    break;

            	default :
            	    break loop120;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 90, equalityExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "equalityExpression"


    public static class instanceOfExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "instanceOfExpression"
    // grammar/Java.g:1082:1: instanceOfExpression : relationalExpression ( 'instanceof' type )? ;
    public final JavaParser.instanceOfExpression_return instanceOfExpression() throws RecognitionException {
        JavaParser.instanceOfExpression_return retval = new JavaParser.instanceOfExpression_return();
        retval.start = input.LT(1);

        int instanceOfExpression_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal443=null;
        JavaParser.relationalExpression_return relationalExpression442 =null;

        JavaParser.type_return type444 =null;


        CommonTree string_literal443_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 91) ) { return retval; }

            // grammar/Java.g:1083:5: ( relationalExpression ( 'instanceof' type )? )
            // grammar/Java.g:1083:9: relationalExpression ( 'instanceof' type )?
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_relationalExpression_in_instanceOfExpression6047);
            relationalExpression442=relationalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpression442.getTree());

            // grammar/Java.g:1084:9: ( 'instanceof' type )?
            int alt121=2;
            int LA121_0 = input.LA(1);

            if ( (LA121_0==INSTANCEOF) ) {
                alt121=1;
            }
            switch (alt121) {
                case 1 :
                    // grammar/Java.g:1084:10: 'instanceof' type
                    {
                    string_literal443=(Token)match(input,INSTANCEOF,FOLLOW_INSTANCEOF_in_instanceOfExpression6058); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal443_tree = 
                    (CommonTree)adaptor.create(string_literal443)
                    ;
                    adaptor.addChild(root_0, string_literal443_tree);
                    }

                    pushFollow(FOLLOW_type_in_instanceOfExpression6060);
                    type444=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type444.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 91, instanceOfExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "instanceOfExpression"


    public static class relationalExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "relationalExpression"
    // grammar/Java.g:1088:1: relationalExpression : shiftExpression ( relationalOp shiftExpression )* ;
    public final JavaParser.relationalExpression_return relationalExpression() throws RecognitionException {
        JavaParser.relationalExpression_return retval = new JavaParser.relationalExpression_return();
        retval.start = input.LT(1);

        int relationalExpression_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.shiftExpression_return shiftExpression445 =null;

        JavaParser.relationalOp_return relationalOp446 =null;

        JavaParser.shiftExpression_return shiftExpression447 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 92) ) { return retval; }

            // grammar/Java.g:1089:5: ( shiftExpression ( relationalOp shiftExpression )* )
            // grammar/Java.g:1089:9: shiftExpression ( relationalOp shiftExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_shiftExpression_in_relationalExpression6090);
            shiftExpression445=shiftExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression445.getTree());

            // grammar/Java.g:1090:9: ( relationalOp shiftExpression )*
            loop122:
            do {
                int alt122=2;
                int LA122_0 = input.LA(1);

                if ( (LA122_0==LT) ) {
                    int LA122_2 = input.LA(2);

                    if ( (LA122_2==BANG||LA122_2==BOOLEAN||LA122_2==BYTE||(LA122_2 >= CHAR && LA122_2 <= CHARLITERAL)||(LA122_2 >= DOUBLE && LA122_2 <= DOUBLELITERAL)||LA122_2==EQ||LA122_2==FALSE||(LA122_2 >= FLOAT && LA122_2 <= FLOATLITERAL)||LA122_2==IDENTIFIER||LA122_2==INT||LA122_2==INTLITERAL||(LA122_2 >= LONG && LA122_2 <= LPAREN)||(LA122_2 >= NEW && LA122_2 <= NULL)||LA122_2==PLUS||LA122_2==PLUSPLUS||LA122_2==SHORT||(LA122_2 >= STRINGLITERAL && LA122_2 <= SUB)||(LA122_2 >= SUBSUB && LA122_2 <= SUPER)||LA122_2==THIS||LA122_2==TILDE||LA122_2==TRUE||LA122_2==VOID) ) {
                        alt122=1;
                    }


                }
                else if ( (LA122_0==GT) ) {
                    int LA122_3 = input.LA(2);

                    if ( (LA122_3==BANG||LA122_3==BOOLEAN||LA122_3==BYTE||(LA122_3 >= CHAR && LA122_3 <= CHARLITERAL)||(LA122_3 >= DOUBLE && LA122_3 <= DOUBLELITERAL)||LA122_3==EQ||LA122_3==FALSE||(LA122_3 >= FLOAT && LA122_3 <= FLOATLITERAL)||LA122_3==IDENTIFIER||LA122_3==INT||LA122_3==INTLITERAL||(LA122_3 >= LONG && LA122_3 <= LPAREN)||(LA122_3 >= NEW && LA122_3 <= NULL)||LA122_3==PLUS||LA122_3==PLUSPLUS||LA122_3==SHORT||(LA122_3 >= STRINGLITERAL && LA122_3 <= SUB)||(LA122_3 >= SUBSUB && LA122_3 <= SUPER)||LA122_3==THIS||LA122_3==TILDE||LA122_3==TRUE||LA122_3==VOID) ) {
                        alt122=1;
                    }


                }


                switch (alt122) {
            	case 1 :
            	    // grammar/Java.g:1090:10: relationalOp shiftExpression
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression6101);
            	    relationalOp446=relationalOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalOp446.getTree());

            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression6103);
            	    shiftExpression447=shiftExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression447.getTree());

            	    }
            	    break;

            	default :
            	    break loop122;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 92, relationalExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "relationalExpression"


    public static class relationalOp_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "relationalOp"
    // grammar/Java.g:1094:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );
    public final JavaParser.relationalOp_return relationalOp() throws RecognitionException {
        JavaParser.relationalOp_return retval = new JavaParser.relationalOp_return();
        retval.start = input.LT(1);

        int relationalOp_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal448=null;
        Token char_literal449=null;
        Token char_literal450=null;
        Token char_literal451=null;
        Token char_literal452=null;
        Token char_literal453=null;

        CommonTree char_literal448_tree=null;
        CommonTree char_literal449_tree=null;
        CommonTree char_literal450_tree=null;
        CommonTree char_literal451_tree=null;
        CommonTree char_literal452_tree=null;
        CommonTree char_literal453_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 93) ) { return retval; }

            // grammar/Java.g:1095:5: ( '<' '=' | '>' '=' | '<' | '>' )
            int alt123=4;
            int LA123_0 = input.LA(1);

            if ( (LA123_0==LT) ) {
                int LA123_1 = input.LA(2);

                if ( (LA123_1==EQ) ) {
                    alt123=1;
                }
                else if ( (LA123_1==BANG||LA123_1==BOOLEAN||LA123_1==BYTE||(LA123_1 >= CHAR && LA123_1 <= CHARLITERAL)||(LA123_1 >= DOUBLE && LA123_1 <= DOUBLELITERAL)||LA123_1==FALSE||(LA123_1 >= FLOAT && LA123_1 <= FLOATLITERAL)||LA123_1==IDENTIFIER||LA123_1==INT||LA123_1==INTLITERAL||(LA123_1 >= LONG && LA123_1 <= LPAREN)||(LA123_1 >= NEW && LA123_1 <= NULL)||LA123_1==PLUS||LA123_1==PLUSPLUS||LA123_1==SHORT||(LA123_1 >= STRINGLITERAL && LA123_1 <= SUB)||(LA123_1 >= SUBSUB && LA123_1 <= SUPER)||LA123_1==THIS||LA123_1==TILDE||LA123_1==TRUE||LA123_1==VOID) ) {
                    alt123=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 123, 1, input);

                    throw nvae;

                }
            }
            else if ( (LA123_0==GT) ) {
                int LA123_2 = input.LA(2);

                if ( (LA123_2==EQ) ) {
                    alt123=2;
                }
                else if ( (LA123_2==BANG||LA123_2==BOOLEAN||LA123_2==BYTE||(LA123_2 >= CHAR && LA123_2 <= CHARLITERAL)||(LA123_2 >= DOUBLE && LA123_2 <= DOUBLELITERAL)||LA123_2==FALSE||(LA123_2 >= FLOAT && LA123_2 <= FLOATLITERAL)||LA123_2==IDENTIFIER||LA123_2==INT||LA123_2==INTLITERAL||(LA123_2 >= LONG && LA123_2 <= LPAREN)||(LA123_2 >= NEW && LA123_2 <= NULL)||LA123_2==PLUS||LA123_2==PLUSPLUS||LA123_2==SHORT||(LA123_2 >= STRINGLITERAL && LA123_2 <= SUB)||(LA123_2 >= SUBSUB && LA123_2 <= SUPER)||LA123_2==THIS||LA123_2==TILDE||LA123_2==TRUE||LA123_2==VOID) ) {
                    alt123=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 123, 2, input);

                    throw nvae;

                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 123, 0, input);

                throw nvae;

            }
            switch (alt123) {
                case 1 :
                    // grammar/Java.g:1095:10: '<' '='
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal448=(Token)match(input,LT,FOLLOW_LT_in_relationalOp6134); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal448_tree = 
                    (CommonTree)adaptor.create(char_literal448)
                    ;
                    adaptor.addChild(root_0, char_literal448_tree);
                    }

                    char_literal449=(Token)match(input,EQ,FOLLOW_EQ_in_relationalOp6136); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal449_tree = 
                    (CommonTree)adaptor.create(char_literal449)
                    ;
                    adaptor.addChild(root_0, char_literal449_tree);
                    }

                    }
                    break;
                case 2 :
                    // grammar/Java.g:1096:10: '>' '='
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal450=(Token)match(input,GT,FOLLOW_GT_in_relationalOp6147); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal450_tree = 
                    (CommonTree)adaptor.create(char_literal450)
                    ;
                    adaptor.addChild(root_0, char_literal450_tree);
                    }

                    char_literal451=(Token)match(input,EQ,FOLLOW_EQ_in_relationalOp6149); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal451_tree = 
                    (CommonTree)adaptor.create(char_literal451)
                    ;
                    adaptor.addChild(root_0, char_literal451_tree);
                    }

                    }
                    break;
                case 3 :
                    // grammar/Java.g:1097:9: '<'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal452=(Token)match(input,LT,FOLLOW_LT_in_relationalOp6159); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal452_tree = 
                    (CommonTree)adaptor.create(char_literal452)
                    ;
                    adaptor.addChild(root_0, char_literal452_tree);
                    }

                    }
                    break;
                case 4 :
                    // grammar/Java.g:1098:9: '>'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal453=(Token)match(input,GT,FOLLOW_GT_in_relationalOp6169); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal453_tree = 
                    (CommonTree)adaptor.create(char_literal453)
                    ;
                    adaptor.addChild(root_0, char_literal453_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 93, relationalOp_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "relationalOp"


    public static class shiftExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "shiftExpression"
    // grammar/Java.g:1101:1: shiftExpression : additiveExpression ( shiftOp additiveExpression )* ;
    public final JavaParser.shiftExpression_return shiftExpression() throws RecognitionException {
        JavaParser.shiftExpression_return retval = new JavaParser.shiftExpression_return();
        retval.start = input.LT(1);

        int shiftExpression_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.additiveExpression_return additiveExpression454 =null;

        JavaParser.shiftOp_return shiftOp455 =null;

        JavaParser.additiveExpression_return additiveExpression456 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 94) ) { return retval; }

            // grammar/Java.g:1102:5: ( additiveExpression ( shiftOp additiveExpression )* )
            // grammar/Java.g:1102:9: additiveExpression ( shiftOp additiveExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_additiveExpression_in_shiftExpression6188);
            additiveExpression454=additiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression454.getTree());

            // grammar/Java.g:1103:9: ( shiftOp additiveExpression )*
            loop124:
            do {
                int alt124=2;
                int LA124_0 = input.LA(1);

                if ( (LA124_0==LT) ) {
                    int LA124_1 = input.LA(2);

                    if ( (LA124_1==LT) ) {
                        int LA124_4 = input.LA(3);

                        if ( (LA124_4==BANG||LA124_4==BOOLEAN||LA124_4==BYTE||(LA124_4 >= CHAR && LA124_4 <= CHARLITERAL)||(LA124_4 >= DOUBLE && LA124_4 <= DOUBLELITERAL)||LA124_4==FALSE||(LA124_4 >= FLOAT && LA124_4 <= FLOATLITERAL)||LA124_4==IDENTIFIER||LA124_4==INT||LA124_4==INTLITERAL||(LA124_4 >= LONG && LA124_4 <= LPAREN)||(LA124_4 >= NEW && LA124_4 <= NULL)||LA124_4==PLUS||LA124_4==PLUSPLUS||LA124_4==SHORT||(LA124_4 >= STRINGLITERAL && LA124_4 <= SUB)||(LA124_4 >= SUBSUB && LA124_4 <= SUPER)||LA124_4==THIS||LA124_4==TILDE||LA124_4==TRUE||LA124_4==VOID) ) {
                            alt124=1;
                        }


                    }


                }
                else if ( (LA124_0==GT) ) {
                    int LA124_2 = input.LA(2);

                    if ( (LA124_2==GT) ) {
                        int LA124_5 = input.LA(3);

                        if ( (LA124_5==GT) ) {
                            int LA124_7 = input.LA(4);

                            if ( (LA124_7==BANG||LA124_7==BOOLEAN||LA124_7==BYTE||(LA124_7 >= CHAR && LA124_7 <= CHARLITERAL)||(LA124_7 >= DOUBLE && LA124_7 <= DOUBLELITERAL)||LA124_7==FALSE||(LA124_7 >= FLOAT && LA124_7 <= FLOATLITERAL)||LA124_7==IDENTIFIER||LA124_7==INT||LA124_7==INTLITERAL||(LA124_7 >= LONG && LA124_7 <= LPAREN)||(LA124_7 >= NEW && LA124_7 <= NULL)||LA124_7==PLUS||LA124_7==PLUSPLUS||LA124_7==SHORT||(LA124_7 >= STRINGLITERAL && LA124_7 <= SUB)||(LA124_7 >= SUBSUB && LA124_7 <= SUPER)||LA124_7==THIS||LA124_7==TILDE||LA124_7==TRUE||LA124_7==VOID) ) {
                                alt124=1;
                            }


                        }
                        else if ( (LA124_5==BANG||LA124_5==BOOLEAN||LA124_5==BYTE||(LA124_5 >= CHAR && LA124_5 <= CHARLITERAL)||(LA124_5 >= DOUBLE && LA124_5 <= DOUBLELITERAL)||LA124_5==FALSE||(LA124_5 >= FLOAT && LA124_5 <= FLOATLITERAL)||LA124_5==IDENTIFIER||LA124_5==INT||LA124_5==INTLITERAL||(LA124_5 >= LONG && LA124_5 <= LPAREN)||(LA124_5 >= NEW && LA124_5 <= NULL)||LA124_5==PLUS||LA124_5==PLUSPLUS||LA124_5==SHORT||(LA124_5 >= STRINGLITERAL && LA124_5 <= SUB)||(LA124_5 >= SUBSUB && LA124_5 <= SUPER)||LA124_5==THIS||LA124_5==TILDE||LA124_5==TRUE||LA124_5==VOID) ) {
                            alt124=1;
                        }


                    }


                }


                switch (alt124) {
            	case 1 :
            	    // grammar/Java.g:1103:10: shiftOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression6199);
            	    shiftOp455=shiftOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftOp455.getTree());

            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression6201);
            	    additiveExpression456=additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression456.getTree());

            	    }
            	    break;

            	default :
            	    break loop124;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 94, shiftExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "shiftExpression"


    public static class shiftOp_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "shiftOp"
    // grammar/Java.g:1108:1: shiftOp : ( '<' '<' | '>' '>' '>' | '>' '>' );
    public final JavaParser.shiftOp_return shiftOp() throws RecognitionException {
        JavaParser.shiftOp_return retval = new JavaParser.shiftOp_return();
        retval.start = input.LT(1);

        int shiftOp_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal457=null;
        Token char_literal458=null;
        Token char_literal459=null;
        Token char_literal460=null;
        Token char_literal461=null;
        Token char_literal462=null;
        Token char_literal463=null;

        CommonTree char_literal457_tree=null;
        CommonTree char_literal458_tree=null;
        CommonTree char_literal459_tree=null;
        CommonTree char_literal460_tree=null;
        CommonTree char_literal461_tree=null;
        CommonTree char_literal462_tree=null;
        CommonTree char_literal463_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 95) ) { return retval; }

            // grammar/Java.g:1109:5: ( '<' '<' | '>' '>' '>' | '>' '>' )
            int alt125=3;
            int LA125_0 = input.LA(1);

            if ( (LA125_0==LT) ) {
                alt125=1;
            }
            else if ( (LA125_0==GT) ) {
                int LA125_2 = input.LA(2);

                if ( (LA125_2==GT) ) {
                    int LA125_3 = input.LA(3);

                    if ( (LA125_3==GT) ) {
                        alt125=2;
                    }
                    else if ( (LA125_3==BANG||LA125_3==BOOLEAN||LA125_3==BYTE||(LA125_3 >= CHAR && LA125_3 <= CHARLITERAL)||(LA125_3 >= DOUBLE && LA125_3 <= DOUBLELITERAL)||LA125_3==FALSE||(LA125_3 >= FLOAT && LA125_3 <= FLOATLITERAL)||LA125_3==IDENTIFIER||LA125_3==INT||LA125_3==INTLITERAL||(LA125_3 >= LONG && LA125_3 <= LPAREN)||(LA125_3 >= NEW && LA125_3 <= NULL)||LA125_3==PLUS||LA125_3==PLUSPLUS||LA125_3==SHORT||(LA125_3 >= STRINGLITERAL && LA125_3 <= SUB)||(LA125_3 >= SUBSUB && LA125_3 <= SUPER)||LA125_3==THIS||LA125_3==TILDE||LA125_3==TRUE||LA125_3==VOID) ) {
                        alt125=3;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 125, 3, input);

                        throw nvae;

                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 125, 2, input);

                    throw nvae;

                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 125, 0, input);

                throw nvae;

            }
            switch (alt125) {
                case 1 :
                    // grammar/Java.g:1109:10: '<' '<'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal457=(Token)match(input,LT,FOLLOW_LT_in_shiftOp6233); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal457_tree = 
                    (CommonTree)adaptor.create(char_literal457)
                    ;
                    adaptor.addChild(root_0, char_literal457_tree);
                    }

                    char_literal458=(Token)match(input,LT,FOLLOW_LT_in_shiftOp6235); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal458_tree = 
                    (CommonTree)adaptor.create(char_literal458)
                    ;
                    adaptor.addChild(root_0, char_literal458_tree);
                    }

                    }
                    break;
                case 2 :
                    // grammar/Java.g:1110:10: '>' '>' '>'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal459=(Token)match(input,GT,FOLLOW_GT_in_shiftOp6246); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal459_tree = 
                    (CommonTree)adaptor.create(char_literal459)
                    ;
                    adaptor.addChild(root_0, char_literal459_tree);
                    }

                    char_literal460=(Token)match(input,GT,FOLLOW_GT_in_shiftOp6248); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal460_tree = 
                    (CommonTree)adaptor.create(char_literal460)
                    ;
                    adaptor.addChild(root_0, char_literal460_tree);
                    }

                    char_literal461=(Token)match(input,GT,FOLLOW_GT_in_shiftOp6250); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal461_tree = 
                    (CommonTree)adaptor.create(char_literal461)
                    ;
                    adaptor.addChild(root_0, char_literal461_tree);
                    }

                    }
                    break;
                case 3 :
                    // grammar/Java.g:1111:10: '>' '>'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal462=(Token)match(input,GT,FOLLOW_GT_in_shiftOp6261); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal462_tree = 
                    (CommonTree)adaptor.create(char_literal462)
                    ;
                    adaptor.addChild(root_0, char_literal462_tree);
                    }

                    char_literal463=(Token)match(input,GT,FOLLOW_GT_in_shiftOp6263); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal463_tree = 
                    (CommonTree)adaptor.create(char_literal463)
                    ;
                    adaptor.addChild(root_0, char_literal463_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 95, shiftOp_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "shiftOp"


    public static class additiveExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "additiveExpression"
    // grammar/Java.g:1115:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
    public final JavaParser.additiveExpression_return additiveExpression() throws RecognitionException {
        JavaParser.additiveExpression_return retval = new JavaParser.additiveExpression_return();
        retval.start = input.LT(1);

        int additiveExpression_StartIndex = input.index();

        CommonTree root_0 = null;

        Token set465=null;
        JavaParser.multiplicativeExpression_return multiplicativeExpression464 =null;

        JavaParser.multiplicativeExpression_return multiplicativeExpression466 =null;


        CommonTree set465_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 96) ) { return retval; }

            // grammar/Java.g:1116:5: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
            // grammar/Java.g:1116:9: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression6283);
            multiplicativeExpression464=multiplicativeExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression464.getTree());

            // grammar/Java.g:1117:9: ( ( '+' | '-' ) multiplicativeExpression )*
            loop126:
            do {
                int alt126=2;
                int LA126_0 = input.LA(1);

                if ( (LA126_0==PLUS||LA126_0==SUB) ) {
                    alt126=1;
                }


                switch (alt126) {
            	case 1 :
            	    // grammar/Java.g:1118:13: ( '+' | '-' ) multiplicativeExpression
            	    {
            	    set465=(Token)input.LT(1);

            	    if ( input.LA(1)==PLUS||input.LA(1)==SUB ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, 
            	        (CommonTree)adaptor.create(set465)
            	        );
            	        state.errorRecovery=false;
            	        state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression6357);
            	    multiplicativeExpression466=multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression466.getTree());

            	    }
            	    break;

            	default :
            	    break loop126;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 96, additiveExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "additiveExpression"


    public static class multiplicativeExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "multiplicativeExpression"
    // grammar/Java.g:1125:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
    public final JavaParser.multiplicativeExpression_return multiplicativeExpression() throws RecognitionException {
        JavaParser.multiplicativeExpression_return retval = new JavaParser.multiplicativeExpression_return();
        retval.start = input.LT(1);

        int multiplicativeExpression_StartIndex = input.index();

        CommonTree root_0 = null;

        Token set468=null;
        JavaParser.unaryExpression_return unaryExpression467 =null;

        JavaParser.unaryExpression_return unaryExpression469 =null;


        CommonTree set468_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 97) ) { return retval; }

            // grammar/Java.g:1126:5: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
            // grammar/Java.g:1127:9: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression6394);
            unaryExpression467=unaryExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression467.getTree());

            // grammar/Java.g:1128:9: ( ( '*' | '/' | '%' ) unaryExpression )*
            loop127:
            do {
                int alt127=2;
                int LA127_0 = input.LA(1);

                if ( (LA127_0==PERCENT||LA127_0==SLASH||LA127_0==STAR) ) {
                    alt127=1;
                }


                switch (alt127) {
            	case 1 :
            	    // grammar/Java.g:1129:13: ( '*' | '/' | '%' ) unaryExpression
            	    {
            	    set468=(Token)input.LT(1);

            	    if ( input.LA(1)==PERCENT||input.LA(1)==SLASH||input.LA(1)==STAR ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, 
            	        (CommonTree)adaptor.create(set468)
            	        );
            	        state.errorRecovery=false;
            	        state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression6486);
            	    unaryExpression469=unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression469.getTree());

            	    }
            	    break;

            	default :
            	    break loop127;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 97, multiplicativeExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "multiplicativeExpression"


    public static class unaryExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "unaryExpression"
    // grammar/Java.g:1141:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus );
    public final JavaParser.unaryExpression_return unaryExpression() throws RecognitionException {
        JavaParser.unaryExpression_return retval = new JavaParser.unaryExpression_return();
        retval.start = input.LT(1);

        int unaryExpression_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal470=null;
        Token char_literal472=null;
        Token string_literal474=null;
        Token string_literal476=null;
        JavaParser.unaryExpression_return unaryExpression471 =null;

        JavaParser.unaryExpression_return unaryExpression473 =null;

        JavaParser.unaryExpression_return unaryExpression475 =null;

        JavaParser.unaryExpression_return unaryExpression477 =null;

        JavaParser.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus478 =null;


        CommonTree char_literal470_tree=null;
        CommonTree char_literal472_tree=null;
        CommonTree string_literal474_tree=null;
        CommonTree string_literal476_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 98) ) { return retval; }

            // grammar/Java.g:1142:5: ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus )
            int alt128=5;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt128=1;
                }
                break;
            case SUB:
                {
                alt128=2;
                }
                break;
            case PLUSPLUS:
                {
                alt128=3;
                }
                break;
            case SUBSUB:
                {
                alt128=4;
                }
                break;
            case BANG:
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case CHARLITERAL:
            case DOUBLE:
            case DOUBLELITERAL:
            case FALSE:
            case FLOAT:
            case FLOATLITERAL:
            case IDENTIFIER:
            case INT:
            case INTLITERAL:
            case LONG:
            case LONGLITERAL:
            case LPAREN:
            case NEW:
            case NULL:
            case SHORT:
            case STRINGLITERAL:
            case SUPER:
            case THIS:
            case TILDE:
            case TRUE:
            case VOID:
                {
                alt128=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 128, 0, input);

                throw nvae;

            }

            switch (alt128) {
                case 1 :
                    // grammar/Java.g:1142:9: '+' unaryExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal470=(Token)match(input,PLUS,FOLLOW_PLUS_in_unaryExpression6518); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal470_tree = 
                    (CommonTree)adaptor.create(char_literal470)
                    ;
                    adaptor.addChild(root_0, char_literal470_tree);
                    }

                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression6521);
                    unaryExpression471=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression471.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:1143:9: '-' unaryExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal472=(Token)match(input,SUB,FOLLOW_SUB_in_unaryExpression6531); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal472_tree = 
                    (CommonTree)adaptor.create(char_literal472)
                    ;
                    adaptor.addChild(root_0, char_literal472_tree);
                    }

                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression6533);
                    unaryExpression473=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression473.getTree());

                    }
                    break;
                case 3 :
                    // grammar/Java.g:1144:9: '++' unaryExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal474=(Token)match(input,PLUSPLUS,FOLLOW_PLUSPLUS_in_unaryExpression6543); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal474_tree = 
                    (CommonTree)adaptor.create(string_literal474)
                    ;
                    adaptor.addChild(root_0, string_literal474_tree);
                    }

                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression6545);
                    unaryExpression475=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression475.getTree());

                    }
                    break;
                case 4 :
                    // grammar/Java.g:1145:9: '--' unaryExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal476=(Token)match(input,SUBSUB,FOLLOW_SUBSUB_in_unaryExpression6555); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal476_tree = 
                    (CommonTree)adaptor.create(string_literal476)
                    ;
                    adaptor.addChild(root_0, string_literal476_tree);
                    }

                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression6557);
                    unaryExpression477=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression477.getTree());

                    }
                    break;
                case 5 :
                    // grammar/Java.g:1146:9: unaryExpressionNotPlusMinus
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression6567);
                    unaryExpressionNotPlusMinus478=unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpressionNotPlusMinus478.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 98, unaryExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "unaryExpression"


    public static class unaryExpressionNotPlusMinus_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "unaryExpressionNotPlusMinus"
    // grammar/Java.g:1149:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );
    public final JavaParser.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus() throws RecognitionException {
        JavaParser.unaryExpressionNotPlusMinus_return retval = new JavaParser.unaryExpressionNotPlusMinus_return();
        retval.start = input.LT(1);

        int unaryExpressionNotPlusMinus_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal479=null;
        Token char_literal481=null;
        Token set486=null;
        JavaParser.unaryExpression_return unaryExpression480 =null;

        JavaParser.unaryExpression_return unaryExpression482 =null;

        JavaParser.castExpression_return castExpression483 =null;

        JavaParser.primary_return primary484 =null;

        JavaParser.selector_return selector485 =null;


        CommonTree char_literal479_tree=null;
        CommonTree char_literal481_tree=null;
        CommonTree set486_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 99) ) { return retval; }

            // grammar/Java.g:1150:5: ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? )
            int alt131=4;
            switch ( input.LA(1) ) {
            case TILDE:
                {
                alt131=1;
                }
                break;
            case BANG:
                {
                alt131=2;
                }
                break;
            case LPAREN:
                {
                int LA131_3 = input.LA(2);

                if ( (synpred203_Java()) ) {
                    alt131=3;
                }
                else if ( (true) ) {
                    alt131=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 131, 3, input);

                    throw nvae;

                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case CHARLITERAL:
            case DOUBLE:
            case DOUBLELITERAL:
            case FALSE:
            case FLOAT:
            case FLOATLITERAL:
            case IDENTIFIER:
            case INT:
            case INTLITERAL:
            case LONG:
            case LONGLITERAL:
            case NEW:
            case NULL:
            case SHORT:
            case STRINGLITERAL:
            case SUPER:
            case THIS:
            case TRUE:
            case VOID:
                {
                alt131=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 131, 0, input);

                throw nvae;

            }

            switch (alt131) {
                case 1 :
                    // grammar/Java.g:1150:9: '~' unaryExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal479=(Token)match(input,TILDE,FOLLOW_TILDE_in_unaryExpressionNotPlusMinus6586); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal479_tree = 
                    (CommonTree)adaptor.create(char_literal479)
                    ;
                    adaptor.addChild(root_0, char_literal479_tree);
                    }

                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6588);
                    unaryExpression480=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression480.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:1151:9: '!' unaryExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal481=(Token)match(input,BANG,FOLLOW_BANG_in_unaryExpressionNotPlusMinus6598); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal481_tree = 
                    (CommonTree)adaptor.create(char_literal481)
                    ;
                    adaptor.addChild(root_0, char_literal481_tree);
                    }

                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6600);
                    unaryExpression482=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression482.getTree());

                    }
                    break;
                case 3 :
                    // grammar/Java.g:1152:9: castExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus6610);
                    castExpression483=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression483.getTree());

                    }
                    break;
                case 4 :
                    // grammar/Java.g:1153:9: primary ( selector )* ( '++' | '--' )?
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus6620);
                    primary484=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primary484.getTree());

                    // grammar/Java.g:1154:9: ( selector )*
                    loop129:
                    do {
                        int alt129=2;
                        int LA129_0 = input.LA(1);

                        if ( (LA129_0==DOT||LA129_0==LBRACKET) ) {
                            alt129=1;
                        }


                        switch (alt129) {
                    	case 1 :
                    	    // grammar/Java.g:1154:10: selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus6631);
                    	    selector485=selector();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, selector485.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop129;
                        }
                    } while (true);


                    // grammar/Java.g:1156:9: ( '++' | '--' )?
                    int alt130=2;
                    int LA130_0 = input.LA(1);

                    if ( (LA130_0==PLUSPLUS||LA130_0==SUBSUB) ) {
                        alt130=1;
                    }
                    switch (alt130) {
                        case 1 :
                            // grammar/Java.g:
                            {
                            set486=(Token)input.LT(1);

                            if ( input.LA(1)==PLUSPLUS||input.LA(1)==SUBSUB ) {
                                input.consume();
                                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                                (CommonTree)adaptor.create(set486)
                                );
                                state.errorRecovery=false;
                                state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 99, unaryExpressionNotPlusMinus_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "unaryExpressionNotPlusMinus"


    public static class castExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "castExpression"
    // grammar/Java.g:1161:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' type ')' unaryExpressionNotPlusMinus );
    public final JavaParser.castExpression_return castExpression() throws RecognitionException {
        JavaParser.castExpression_return retval = new JavaParser.castExpression_return();
        retval.start = input.LT(1);

        int castExpression_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal487=null;
        Token char_literal489=null;
        Token char_literal491=null;
        Token char_literal493=null;
        JavaParser.primitiveType_return primitiveType488 =null;

        JavaParser.unaryExpression_return unaryExpression490 =null;

        JavaParser.type_return type492 =null;

        JavaParser.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus494 =null;


        CommonTree char_literal487_tree=null;
        CommonTree char_literal489_tree=null;
        CommonTree char_literal491_tree=null;
        CommonTree char_literal493_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 100) ) { return retval; }

            // grammar/Java.g:1162:5: ( '(' primitiveType ')' unaryExpression | '(' type ')' unaryExpressionNotPlusMinus )
            int alt132=2;
            int LA132_0 = input.LA(1);

            if ( (LA132_0==LPAREN) ) {
                int LA132_1 = input.LA(2);

                if ( (synpred207_Java()) ) {
                    alt132=1;
                }
                else if ( (true) ) {
                    alt132=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 132, 1, input);

                    throw nvae;

                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 132, 0, input);

                throw nvae;

            }
            switch (alt132) {
                case 1 :
                    // grammar/Java.g:1162:9: '(' primitiveType ')' unaryExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal487=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_castExpression6700); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal487_tree = 
                    (CommonTree)adaptor.create(char_literal487)
                    ;
                    adaptor.addChild(root_0, char_literal487_tree);
                    }

                    pushFollow(FOLLOW_primitiveType_in_castExpression6702);
                    primitiveType488=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType488.getTree());

                    char_literal489=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_castExpression6704); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal489_tree = 
                    (CommonTree)adaptor.create(char_literal489)
                    ;
                    adaptor.addChild(root_0, char_literal489_tree);
                    }

                    pushFollow(FOLLOW_unaryExpression_in_castExpression6706);
                    unaryExpression490=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression490.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:1163:9: '(' type ')' unaryExpressionNotPlusMinus
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal491=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_castExpression6716); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal491_tree = 
                    (CommonTree)adaptor.create(char_literal491)
                    ;
                    adaptor.addChild(root_0, char_literal491_tree);
                    }

                    pushFollow(FOLLOW_type_in_castExpression6718);
                    type492=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type492.getTree());

                    char_literal493=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_castExpression6720); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal493_tree = 
                    (CommonTree)adaptor.create(char_literal493)
                    ;
                    adaptor.addChild(root_0, char_literal493_tree);
                    }

                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression6722);
                    unaryExpressionNotPlusMinus494=unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpressionNotPlusMinus494.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 100, castExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "castExpression"


    public static class primary_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "primary"
    // grammar/Java.g:1169:1: primary : ( parExpression | objectSelector | superSelector | literal | creator | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );
    public final JavaParser.primary_return primary() throws RecognitionException {
        JavaParser.primary_return retval = new JavaParser.primary_return();
        retval.start = input.LT(1);

        int primary_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal501=null;
        Token char_literal502=null;
        Token char_literal503=null;
        Token string_literal504=null;
        Token string_literal505=null;
        Token char_literal506=null;
        Token string_literal507=null;
        JavaParser.parExpression_return parExpression495 =null;

        JavaParser.objectSelector_return objectSelector496 =null;

        JavaParser.superSelector_return superSelector497 =null;

        JavaParser.literal_return literal498 =null;

        JavaParser.creator_return creator499 =null;

        JavaParser.primitiveType_return primitiveType500 =null;


        CommonTree char_literal501_tree=null;
        CommonTree char_literal502_tree=null;
        CommonTree char_literal503_tree=null;
        CommonTree string_literal504_tree=null;
        CommonTree string_literal505_tree=null;
        CommonTree char_literal506_tree=null;
        CommonTree string_literal507_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 101) ) { return retval; }

            // grammar/Java.g:1170:5: ( parExpression | objectSelector | superSelector | literal | creator | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' )
            int alt134=7;
            switch ( input.LA(1) ) {
            case LPAREN:
                {
                alt134=1;
                }
                break;
            case IDENTIFIER:
            case THIS:
                {
                alt134=2;
                }
                break;
            case SUPER:
                {
                alt134=3;
                }
                break;
            case CHARLITERAL:
            case DOUBLELITERAL:
            case FALSE:
            case FLOATLITERAL:
            case INTLITERAL:
            case LONGLITERAL:
            case NULL:
            case STRINGLITERAL:
            case TRUE:
                {
                alt134=4;
                }
                break;
            case NEW:
                {
                alt134=5;
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
                alt134=6;
                }
                break;
            case VOID:
                {
                alt134=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 134, 0, input);

                throw nvae;

            }

            switch (alt134) {
                case 1 :
                    // grammar/Java.g:1170:9: parExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_parExpression_in_primary6743);
                    parExpression495=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression495.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:1171:9: objectSelector
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_objectSelector_in_primary6753);
                    objectSelector496=objectSelector();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, objectSelector496.getTree());

                    }
                    break;
                case 3 :
                    // grammar/Java.g:1172:9: superSelector
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_superSelector_in_primary6763);
                    superSelector497=superSelector();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, superSelector497.getTree());

                    }
                    break;
                case 4 :
                    // grammar/Java.g:1173:9: literal
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_literal_in_primary6773);
                    literal498=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal498.getTree());

                    }
                    break;
                case 5 :
                    // grammar/Java.g:1174:9: creator
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_creator_in_primary6783);
                    creator499=creator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, creator499.getTree());

                    }
                    break;
                case 6 :
                    // grammar/Java.g:1175:9: primitiveType ( '[' ']' )* '.' 'class'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_primitiveType_in_primary6793);
                    primitiveType500=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType500.getTree());

                    // grammar/Java.g:1176:9: ( '[' ']' )*
                    loop133:
                    do {
                        int alt133=2;
                        int LA133_0 = input.LA(1);

                        if ( (LA133_0==LBRACKET) ) {
                            alt133=1;
                        }


                        switch (alt133) {
                    	case 1 :
                    	    // grammar/Java.g:1176:10: '[' ']'
                    	    {
                    	    char_literal501=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_primary6804); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal501_tree = 
                    	    (CommonTree)adaptor.create(char_literal501)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal501_tree);
                    	    }

                    	    char_literal502=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_primary6806); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal502_tree = 
                    	    (CommonTree)adaptor.create(char_literal502)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal502_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop133;
                        }
                    } while (true);


                    char_literal503=(Token)match(input,DOT,FOLLOW_DOT_in_primary6827); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal503_tree = 
                    (CommonTree)adaptor.create(char_literal503)
                    ;
                    adaptor.addChild(root_0, char_literal503_tree);
                    }

                    string_literal504=(Token)match(input,CLASS,FOLLOW_CLASS_in_primary6829); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal504_tree = 
                    (CommonTree)adaptor.create(string_literal504)
                    ;
                    adaptor.addChild(root_0, string_literal504_tree);
                    }

                    }
                    break;
                case 7 :
                    // grammar/Java.g:1179:9: 'void' '.' 'class'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal505=(Token)match(input,VOID,FOLLOW_VOID_in_primary6839); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal505_tree = 
                    (CommonTree)adaptor.create(string_literal505)
                    ;
                    adaptor.addChild(root_0, string_literal505_tree);
                    }

                    char_literal506=(Token)match(input,DOT,FOLLOW_DOT_in_primary6841); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal506_tree = 
                    (CommonTree)adaptor.create(char_literal506)
                    ;
                    adaptor.addChild(root_0, char_literal506_tree);
                    }

                    string_literal507=(Token)match(input,CLASS,FOLLOW_CLASS_in_primary6843); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal507_tree = 
                    (CommonTree)adaptor.create(string_literal507)
                    ;
                    adaptor.addChild(root_0, string_literal507_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 101, primary_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "primary"


    public static class objectSelector_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "objectSelector"
    // grammar/Java.g:1182:1: objectSelector : objectSelectorInner ( identifierSuffix )? -> ^( VAR_NAME_T objectSelectorInner ( identifierSuffix )? ) ;
    public final JavaParser.objectSelector_return objectSelector() throws RecognitionException {
        JavaParser.objectSelector_return retval = new JavaParser.objectSelector_return();
        retval.start = input.LT(1);

        int objectSelector_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.objectSelectorInner_return objectSelectorInner508 =null;

        JavaParser.identifierSuffix_return identifierSuffix509 =null;


        RewriteRuleSubtreeStream stream_objectSelectorInner=new RewriteRuleSubtreeStream(adaptor,"rule objectSelectorInner");
        RewriteRuleSubtreeStream stream_identifierSuffix=new RewriteRuleSubtreeStream(adaptor,"rule identifierSuffix");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 102) ) { return retval; }

            // grammar/Java.g:1183:2: ( objectSelectorInner ( identifierSuffix )? -> ^( VAR_NAME_T objectSelectorInner ( identifierSuffix )? ) )
            // grammar/Java.g:1183:3: objectSelectorInner ( identifierSuffix )?
            {
            pushFollow(FOLLOW_objectSelectorInner_in_objectSelector6856);
            objectSelectorInner508=objectSelectorInner();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_objectSelectorInner.add(objectSelectorInner508.getTree());

            // grammar/Java.g:1183:23: ( identifierSuffix )?
            int alt135=2;
            switch ( input.LA(1) ) {
                case LBRACKET:
                    {
                    int LA135_1 = input.LA(2);

                    if ( (synpred215_Java()) ) {
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

                    if ( (synpred215_Java()) ) {
                        alt135=1;
                    }
                    }
                    break;
            }

            switch (alt135) {
                case 1 :
                    // grammar/Java.g:1183:24: identifierSuffix
                    {
                    pushFollow(FOLLOW_identifierSuffix_in_objectSelector6859);
                    identifierSuffix509=identifierSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_identifierSuffix.add(identifierSuffix509.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: identifierSuffix, objectSelectorInner
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 1183:43: -> ^( VAR_NAME_T objectSelectorInner ( identifierSuffix )? )
            {
                // grammar/Java.g:1183:46: ^( VAR_NAME_T objectSelectorInner ( identifierSuffix )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(VAR_NAME_T, "VAR_NAME_T")
                , root_1);

                adaptor.addChild(root_1, stream_objectSelectorInner.nextTree());

                // grammar/Java.g:1183:80: ( identifierSuffix )?
                if ( stream_identifierSuffix.hasNext() ) {
                    adaptor.addChild(root_1, stream_identifierSuffix.nextTree());

                }
                stream_identifierSuffix.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 102, objectSelector_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "objectSelector"


    public static class objectSelectorInner_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "objectSelectorInner"
    // grammar/Java.g:1186:1: objectSelectorInner : ( 'this' ( '.' IDENTIFIER )* | IDENTIFIER ( '.' IDENTIFIER )* );
    public final JavaParser.objectSelectorInner_return objectSelectorInner() throws RecognitionException {
        JavaParser.objectSelectorInner_return retval = new JavaParser.objectSelectorInner_return();
        retval.start = input.LT(1);

        int objectSelectorInner_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal510=null;
        Token char_literal511=null;
        Token IDENTIFIER512=null;
        Token IDENTIFIER513=null;
        Token char_literal514=null;
        Token IDENTIFIER515=null;

        CommonTree string_literal510_tree=null;
        CommonTree char_literal511_tree=null;
        CommonTree IDENTIFIER512_tree=null;
        CommonTree IDENTIFIER513_tree=null;
        CommonTree char_literal514_tree=null;
        CommonTree IDENTIFIER515_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 103) ) { return retval; }

            // grammar/Java.g:1187:2: ( 'this' ( '.' IDENTIFIER )* | IDENTIFIER ( '.' IDENTIFIER )* )
            int alt138=2;
            int LA138_0 = input.LA(1);

            if ( (LA138_0==THIS) ) {
                alt138=1;
            }
            else if ( (LA138_0==IDENTIFIER) ) {
                alt138=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 138, 0, input);

                throw nvae;

            }
            switch (alt138) {
                case 1 :
                    // grammar/Java.g:1187:3: 'this' ( '.' IDENTIFIER )*
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal510=(Token)match(input,THIS,FOLLOW_THIS_in_objectSelectorInner6885); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal510_tree = 
                    (CommonTree)adaptor.create(string_literal510)
                    ;
                    adaptor.addChild(root_0, string_literal510_tree);
                    }

                    // grammar/Java.g:1188:14: ( '.' IDENTIFIER )*
                    loop136:
                    do {
                        int alt136=2;
                        int LA136_0 = input.LA(1);

                        if ( (LA136_0==DOT) ) {
                            int LA136_2 = input.LA(2);

                            if ( (LA136_2==IDENTIFIER) ) {
                                int LA136_3 = input.LA(3);

                                if ( (synpred216_Java()) ) {
                                    alt136=1;
                                }


                            }


                        }


                        switch (alt136) {
                    	case 1 :
                    	    // grammar/Java.g:1188:15: '.' IDENTIFIER
                    	    {
                    	    char_literal511=(Token)match(input,DOT,FOLLOW_DOT_in_objectSelectorInner6901); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal511_tree = 
                    	    (CommonTree)adaptor.create(char_literal511)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal511_tree);
                    	    }

                    	    IDENTIFIER512=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_objectSelectorInner6903); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    IDENTIFIER512_tree = 
                    	    (CommonTree)adaptor.create(IDENTIFIER512)
                    	    ;
                    	    adaptor.addChild(root_0, IDENTIFIER512_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop136;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // grammar/Java.g:1190:7: IDENTIFIER ( '.' IDENTIFIER )*
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    IDENTIFIER513=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_objectSelectorInner6927); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER513_tree = 
                    (CommonTree)adaptor.create(IDENTIFIER513)
                    ;
                    adaptor.addChild(root_0, IDENTIFIER513_tree);
                    }

                    // grammar/Java.g:1191:15: ( '.' IDENTIFIER )*
                    loop137:
                    do {
                        int alt137=2;
                        int LA137_0 = input.LA(1);

                        if ( (LA137_0==DOT) ) {
                            int LA137_2 = input.LA(2);

                            if ( (LA137_2==IDENTIFIER) ) {
                                int LA137_3 = input.LA(3);

                                if ( (synpred218_Java()) ) {
                                    alt137=1;
                                }


                            }


                        }


                        switch (alt137) {
                    	case 1 :
                    	    // grammar/Java.g:1191:16: '.' IDENTIFIER
                    	    {
                    	    char_literal514=(Token)match(input,DOT,FOLLOW_DOT_in_objectSelectorInner6944); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal514_tree = 
                    	    (CommonTree)adaptor.create(char_literal514)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal514_tree);
                    	    }

                    	    IDENTIFIER515=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_objectSelectorInner6946); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    IDENTIFIER515_tree = 
                    	    (CommonTree)adaptor.create(IDENTIFIER515)
                    	    ;
                    	    adaptor.addChild(root_0, IDENTIFIER515_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop137;
                        }
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 103, objectSelectorInner_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "objectSelectorInner"


    public static class superSelector_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "superSelector"
    // grammar/Java.g:1195:1: superSelector : 'super' superSuffix -> ^( VAR_NAME_T 'super' superSuffix ) ;
    public final JavaParser.superSelector_return superSelector() throws RecognitionException {
        JavaParser.superSelector_return retval = new JavaParser.superSelector_return();
        retval.start = input.LT(1);

        int superSelector_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal516=null;
        JavaParser.superSuffix_return superSuffix517 =null;


        CommonTree string_literal516_tree=null;
        RewriteRuleTokenStream stream_SUPER=new RewriteRuleTokenStream(adaptor,"token SUPER");
        RewriteRuleSubtreeStream stream_superSuffix=new RewriteRuleSubtreeStream(adaptor,"rule superSuffix");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 104) ) { return retval; }

            // grammar/Java.g:1196:2: ( 'super' superSuffix -> ^( VAR_NAME_T 'super' superSuffix ) )
            // grammar/Java.g:1196:3: 'super' superSuffix
            {
            string_literal516=(Token)match(input,SUPER,FOLLOW_SUPER_in_superSelector6976); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SUPER.add(string_literal516);


            pushFollow(FOLLOW_superSuffix_in_superSelector6978);
            superSuffix517=superSuffix();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_superSuffix.add(superSuffix517.getTree());

            // AST REWRITE
            // elements: superSuffix, SUPER
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 1196:23: -> ^( VAR_NAME_T 'super' superSuffix )
            {
                // grammar/Java.g:1196:26: ^( VAR_NAME_T 'super' superSuffix )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(VAR_NAME_T, "VAR_NAME_T")
                , root_1);

                adaptor.addChild(root_1, 
                stream_SUPER.nextNode()
                );

                adaptor.addChild(root_1, stream_superSuffix.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 104, superSelector_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "superSelector"


    public static class superSuffix_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "superSuffix"
    // grammar/Java.g:1200:1: superSuffix : ( arguments | '.' ( typeArguments )? IDENTIFIER ( arguments )? );
    public final JavaParser.superSuffix_return superSuffix() throws RecognitionException {
        JavaParser.superSuffix_return retval = new JavaParser.superSuffix_return();
        retval.start = input.LT(1);

        int superSuffix_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal519=null;
        Token IDENTIFIER521=null;
        JavaParser.arguments_return arguments518 =null;

        JavaParser.typeArguments_return typeArguments520 =null;

        JavaParser.arguments_return arguments522 =null;


        CommonTree char_literal519_tree=null;
        CommonTree IDENTIFIER521_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 105) ) { return retval; }

            // grammar/Java.g:1201:5: ( arguments | '.' ( typeArguments )? IDENTIFIER ( arguments )? )
            int alt141=2;
            int LA141_0 = input.LA(1);

            if ( (LA141_0==LPAREN) ) {
                alt141=1;
            }
            else if ( (LA141_0==DOT) ) {
                alt141=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 141, 0, input);

                throw nvae;

            }
            switch (alt141) {
                case 1 :
                    // grammar/Java.g:1201:9: arguments
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_arguments_in_superSuffix7012);
                    arguments518=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments518.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:1202:9: '.' ( typeArguments )? IDENTIFIER ( arguments )?
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal519=(Token)match(input,DOT,FOLLOW_DOT_in_superSuffix7022); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal519_tree = 
                    (CommonTree)adaptor.create(char_literal519)
                    ;
                    adaptor.addChild(root_0, char_literal519_tree);
                    }

                    // grammar/Java.g:1202:13: ( typeArguments )?
                    int alt139=2;
                    int LA139_0 = input.LA(1);

                    if ( (LA139_0==LT) ) {
                        alt139=1;
                    }
                    switch (alt139) {
                        case 1 :
                            // grammar/Java.g:1202:14: typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_superSuffix7025);
                            typeArguments520=typeArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments520.getTree());

                            }
                            break;

                    }


                    IDENTIFIER521=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_superSuffix7046); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER521_tree = 
                    (CommonTree)adaptor.create(IDENTIFIER521)
                    ;
                    adaptor.addChild(root_0, IDENTIFIER521_tree);
                    }

                    // grammar/Java.g:1205:9: ( arguments )?
                    int alt140=2;
                    int LA140_0 = input.LA(1);

                    if ( (LA140_0==LPAREN) ) {
                        alt140=1;
                    }
                    switch (alt140) {
                        case 1 :
                            // grammar/Java.g:1205:10: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix7057);
                            arguments522=arguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments522.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 105, superSuffix_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "superSuffix"


    public static class identifierSuffix_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "identifierSuffix"
    // grammar/Java.g:1210:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' nonWildcardTypeArguments IDENTIFIER arguments | '.' 'this' | '.' 'super' arguments | innerCreator );
    public final JavaParser.identifierSuffix_return identifierSuffix() throws RecognitionException {
        JavaParser.identifierSuffix_return retval = new JavaParser.identifierSuffix_return();
        retval.start = input.LT(1);

        int identifierSuffix_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal523=null;
        Token char_literal524=null;
        Token char_literal525=null;
        Token string_literal526=null;
        Token char_literal527=null;
        Token char_literal529=null;
        Token char_literal531=null;
        Token string_literal532=null;
        Token char_literal533=null;
        Token IDENTIFIER535=null;
        Token char_literal537=null;
        Token string_literal538=null;
        Token char_literal539=null;
        Token string_literal540=null;
        JavaParser.expression_return expression528 =null;

        JavaParser.arguments_return arguments530 =null;

        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments534 =null;

        JavaParser.arguments_return arguments536 =null;

        JavaParser.arguments_return arguments541 =null;

        JavaParser.innerCreator_return innerCreator542 =null;


        CommonTree char_literal523_tree=null;
        CommonTree char_literal524_tree=null;
        CommonTree char_literal525_tree=null;
        CommonTree string_literal526_tree=null;
        CommonTree char_literal527_tree=null;
        CommonTree char_literal529_tree=null;
        CommonTree char_literal531_tree=null;
        CommonTree string_literal532_tree=null;
        CommonTree char_literal533_tree=null;
        CommonTree IDENTIFIER535_tree=null;
        CommonTree char_literal537_tree=null;
        CommonTree string_literal538_tree=null;
        CommonTree char_literal539_tree=null;
        CommonTree string_literal540_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 106) ) { return retval; }

            // grammar/Java.g:1211:5: ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' nonWildcardTypeArguments IDENTIFIER arguments | '.' 'this' | '.' 'super' arguments | innerCreator )
            int alt144=8;
            switch ( input.LA(1) ) {
            case LBRACKET:
                {
                int LA144_1 = input.LA(2);

                if ( (LA144_1==RBRACKET) ) {
                    alt144=1;
                }
                else if ( (LA144_1==BANG||LA144_1==BOOLEAN||LA144_1==BYTE||(LA144_1 >= CHAR && LA144_1 <= CHARLITERAL)||(LA144_1 >= DOUBLE && LA144_1 <= DOUBLELITERAL)||LA144_1==FALSE||(LA144_1 >= FLOAT && LA144_1 <= FLOATLITERAL)||LA144_1==IDENTIFIER||LA144_1==INT||LA144_1==INTLITERAL||(LA144_1 >= LONG && LA144_1 <= LPAREN)||(LA144_1 >= NEW && LA144_1 <= NULL)||LA144_1==PLUS||LA144_1==PLUSPLUS||LA144_1==SHORT||(LA144_1 >= STRINGLITERAL && LA144_1 <= SUB)||(LA144_1 >= SUBSUB && LA144_1 <= SUPER)||LA144_1==THIS||LA144_1==TILDE||LA144_1==TRUE||LA144_1==VOID) ) {
                    alt144=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 144, 1, input);

                    throw nvae;

                }
                }
                break;
            case LPAREN:
                {
                alt144=3;
                }
                break;
            case DOT:
                {
                switch ( input.LA(2) ) {
                case CLASS:
                    {
                    alt144=4;
                    }
                    break;
                case THIS:
                    {
                    alt144=6;
                    }
                    break;
                case SUPER:
                    {
                    alt144=7;
                    }
                    break;
                case NEW:
                    {
                    alt144=8;
                    }
                    break;
                case LT:
                    {
                    alt144=5;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 144, 3, input);

                    throw nvae;

                }

                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 144, 0, input);

                throw nvae;

            }

            switch (alt144) {
                case 1 :
                    // grammar/Java.g:1211:9: ( '[' ']' )+ '.' 'class'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    // grammar/Java.g:1211:9: ( '[' ']' )+
                    int cnt142=0;
                    loop142:
                    do {
                        int alt142=2;
                        int LA142_0 = input.LA(1);

                        if ( (LA142_0==LBRACKET) ) {
                            alt142=1;
                        }


                        switch (alt142) {
                    	case 1 :
                    	    // grammar/Java.g:1211:10: '[' ']'
                    	    {
                    	    char_literal523=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_identifierSuffix7089); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal523_tree = 
                    	    (CommonTree)adaptor.create(char_literal523)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal523_tree);
                    	    }

                    	    char_literal524=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_identifierSuffix7091); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal524_tree = 
                    	    (CommonTree)adaptor.create(char_literal524)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal524_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt142 >= 1 ) break loop142;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(142, input);
                                throw eee;
                        }
                        cnt142++;
                    } while (true);


                    char_literal525=(Token)match(input,DOT,FOLLOW_DOT_in_identifierSuffix7112); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal525_tree = 
                    (CommonTree)adaptor.create(char_literal525)
                    ;
                    adaptor.addChild(root_0, char_literal525_tree);
                    }

                    string_literal526=(Token)match(input,CLASS,FOLLOW_CLASS_in_identifierSuffix7114); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal526_tree = 
                    (CommonTree)adaptor.create(string_literal526)
                    ;
                    adaptor.addChild(root_0, string_literal526_tree);
                    }

                    }
                    break;
                case 2 :
                    // grammar/Java.g:1214:9: ( '[' expression ']' )+
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    // grammar/Java.g:1214:9: ( '[' expression ']' )+
                    int cnt143=0;
                    loop143:
                    do {
                        int alt143=2;
                        int LA143_0 = input.LA(1);

                        if ( (LA143_0==LBRACKET) ) {
                            int LA143_2 = input.LA(2);

                            if ( (synpred224_Java()) ) {
                                alt143=1;
                            }


                        }


                        switch (alt143) {
                    	case 1 :
                    	    // grammar/Java.g:1214:10: '[' expression ']'
                    	    {
                    	    char_literal527=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_identifierSuffix7125); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal527_tree = 
                    	    (CommonTree)adaptor.create(char_literal527)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal527_tree);
                    	    }

                    	    pushFollow(FOLLOW_expression_in_identifierSuffix7127);
                    	    expression528=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression528.getTree());

                    	    char_literal529=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_identifierSuffix7129); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal529_tree = 
                    	    (CommonTree)adaptor.create(char_literal529)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal529_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt143 >= 1 ) break loop143;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(143, input);
                                throw eee;
                        }
                        cnt143++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // grammar/Java.g:1216:9: arguments
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_arguments_in_identifierSuffix7150);
                    arguments530=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments530.getTree());

                    }
                    break;
                case 4 :
                    // grammar/Java.g:1217:9: '.' 'class'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal531=(Token)match(input,DOT,FOLLOW_DOT_in_identifierSuffix7160); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal531_tree = 
                    (CommonTree)adaptor.create(char_literal531)
                    ;
                    adaptor.addChild(root_0, char_literal531_tree);
                    }

                    string_literal532=(Token)match(input,CLASS,FOLLOW_CLASS_in_identifierSuffix7162); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal532_tree = 
                    (CommonTree)adaptor.create(string_literal532)
                    ;
                    adaptor.addChild(root_0, string_literal532_tree);
                    }

                    }
                    break;
                case 5 :
                    // grammar/Java.g:1218:9: '.' nonWildcardTypeArguments IDENTIFIER arguments
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal533=(Token)match(input,DOT,FOLLOW_DOT_in_identifierSuffix7174); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal533_tree = 
                    (CommonTree)adaptor.create(char_literal533)
                    ;
                    adaptor.addChild(root_0, char_literal533_tree);
                    }

                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_identifierSuffix7176);
                    nonWildcardTypeArguments534=nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments534.getTree());

                    IDENTIFIER535=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_identifierSuffix7178); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER535_tree = 
                    (CommonTree)adaptor.create(IDENTIFIER535)
                    ;
                    adaptor.addChild(root_0, IDENTIFIER535_tree);
                    }

                    pushFollow(FOLLOW_arguments_in_identifierSuffix7180);
                    arguments536=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments536.getTree());

                    }
                    break;
                case 6 :
                    // grammar/Java.g:1219:9: '.' 'this'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal537=(Token)match(input,DOT,FOLLOW_DOT_in_identifierSuffix7190); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal537_tree = 
                    (CommonTree)adaptor.create(char_literal537)
                    ;
                    adaptor.addChild(root_0, char_literal537_tree);
                    }

                    string_literal538=(Token)match(input,THIS,FOLLOW_THIS_in_identifierSuffix7192); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal538_tree = 
                    (CommonTree)adaptor.create(string_literal538)
                    ;
                    adaptor.addChild(root_0, string_literal538_tree);
                    }

                    }
                    break;
                case 7 :
                    // grammar/Java.g:1220:9: '.' 'super' arguments
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal539=(Token)match(input,DOT,FOLLOW_DOT_in_identifierSuffix7202); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal539_tree = 
                    (CommonTree)adaptor.create(char_literal539)
                    ;
                    adaptor.addChild(root_0, char_literal539_tree);
                    }

                    string_literal540=(Token)match(input,SUPER,FOLLOW_SUPER_in_identifierSuffix7204); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal540_tree = 
                    (CommonTree)adaptor.create(string_literal540)
                    ;
                    adaptor.addChild(root_0, string_literal540_tree);
                    }

                    pushFollow(FOLLOW_arguments_in_identifierSuffix7206);
                    arguments541=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments541.getTree());

                    }
                    break;
                case 8 :
                    // grammar/Java.g:1221:9: innerCreator
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_innerCreator_in_identifierSuffix7216);
                    innerCreator542=innerCreator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, innerCreator542.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 106, identifierSuffix_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "identifierSuffix"


    public static class selector_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "selector"
    // grammar/Java.g:1225:1: selector : ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | innerCreator | '[' expression ']' );
    public final JavaParser.selector_return selector() throws RecognitionException {
        JavaParser.selector_return retval = new JavaParser.selector_return();
        retval.start = input.LT(1);

        int selector_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal543=null;
        Token IDENTIFIER544=null;
        Token char_literal546=null;
        Token string_literal547=null;
        Token char_literal548=null;
        Token string_literal549=null;
        Token char_literal552=null;
        Token char_literal554=null;
        JavaParser.arguments_return arguments545 =null;

        JavaParser.superSuffix_return superSuffix550 =null;

        JavaParser.innerCreator_return innerCreator551 =null;

        JavaParser.expression_return expression553 =null;


        CommonTree char_literal543_tree=null;
        CommonTree IDENTIFIER544_tree=null;
        CommonTree char_literal546_tree=null;
        CommonTree string_literal547_tree=null;
        CommonTree char_literal548_tree=null;
        CommonTree string_literal549_tree=null;
        CommonTree char_literal552_tree=null;
        CommonTree char_literal554_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 107) ) { return retval; }

            // grammar/Java.g:1226:5: ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | innerCreator | '[' expression ']' )
            int alt146=5;
            int LA146_0 = input.LA(1);

            if ( (LA146_0==DOT) ) {
                switch ( input.LA(2) ) {
                case IDENTIFIER:
                    {
                    alt146=1;
                    }
                    break;
                case THIS:
                    {
                    alt146=2;
                    }
                    break;
                case SUPER:
                    {
                    alt146=3;
                    }
                    break;
                case NEW:
                    {
                    alt146=4;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 146, 1, input);

                    throw nvae;

                }

            }
            else if ( (LA146_0==LBRACKET) ) {
                alt146=5;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 146, 0, input);

                throw nvae;

            }
            switch (alt146) {
                case 1 :
                    // grammar/Java.g:1226:9: '.' IDENTIFIER ( arguments )?
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal543=(Token)match(input,DOT,FOLLOW_DOT_in_selector7242); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal543_tree = 
                    (CommonTree)adaptor.create(char_literal543)
                    ;
                    adaptor.addChild(root_0, char_literal543_tree);
                    }

                    IDENTIFIER544=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_selector7244); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER544_tree = 
                    (CommonTree)adaptor.create(IDENTIFIER544)
                    ;
                    adaptor.addChild(root_0, IDENTIFIER544_tree);
                    }

                    // grammar/Java.g:1227:9: ( arguments )?
                    int alt145=2;
                    int LA145_0 = input.LA(1);

                    if ( (LA145_0==LPAREN) ) {
                        alt145=1;
                    }
                    switch (alt145) {
                        case 1 :
                            // grammar/Java.g:1227:10: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector7255);
                            arguments545=arguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments545.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // grammar/Java.g:1229:9: '.' 'this'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal546=(Token)match(input,DOT,FOLLOW_DOT_in_selector7276); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal546_tree = 
                    (CommonTree)adaptor.create(char_literal546)
                    ;
                    adaptor.addChild(root_0, char_literal546_tree);
                    }

                    string_literal547=(Token)match(input,THIS,FOLLOW_THIS_in_selector7278); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal547_tree = 
                    (CommonTree)adaptor.create(string_literal547)
                    ;
                    adaptor.addChild(root_0, string_literal547_tree);
                    }

                    }
                    break;
                case 3 :
                    // grammar/Java.g:1230:9: '.' 'super' superSuffix
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal548=(Token)match(input,DOT,FOLLOW_DOT_in_selector7288); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal548_tree = 
                    (CommonTree)adaptor.create(char_literal548)
                    ;
                    adaptor.addChild(root_0, char_literal548_tree);
                    }

                    string_literal549=(Token)match(input,SUPER,FOLLOW_SUPER_in_selector7290); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal549_tree = 
                    (CommonTree)adaptor.create(string_literal549)
                    ;
                    adaptor.addChild(root_0, string_literal549_tree);
                    }

                    pushFollow(FOLLOW_superSuffix_in_selector7300);
                    superSuffix550=superSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, superSuffix550.getTree());

                    }
                    break;
                case 4 :
                    // grammar/Java.g:1232:9: innerCreator
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_innerCreator_in_selector7310);
                    innerCreator551=innerCreator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, innerCreator551.getTree());

                    }
                    break;
                case 5 :
                    // grammar/Java.g:1233:9: '[' expression ']'
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    char_literal552=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_selector7320); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal552_tree = 
                    (CommonTree)adaptor.create(char_literal552)
                    ;
                    adaptor.addChild(root_0, char_literal552_tree);
                    }

                    pushFollow(FOLLOW_expression_in_selector7322);
                    expression553=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression553.getTree());

                    char_literal554=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_selector7324); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal554_tree = 
                    (CommonTree)adaptor.create(char_literal554)
                    ;
                    adaptor.addChild(root_0, char_literal554_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 107, selector_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "selector"


    public static class creator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "creator"
    // grammar/Java.g:1236:1: creator : ( 'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest | 'new' classOrInterfaceType classCreatorRest | arrayCreator );
    public final JavaParser.creator_return creator() throws RecognitionException {
        JavaParser.creator_return retval = new JavaParser.creator_return();
        retval.start = input.LT(1);

        int creator_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal555=null;
        Token string_literal559=null;
        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments556 =null;

        JavaParser.classOrInterfaceType_return classOrInterfaceType557 =null;

        JavaParser.classCreatorRest_return classCreatorRest558 =null;

        JavaParser.classOrInterfaceType_return classOrInterfaceType560 =null;

        JavaParser.classCreatorRest_return classCreatorRest561 =null;

        JavaParser.arrayCreator_return arrayCreator562 =null;


        CommonTree string_literal555_tree=null;
        CommonTree string_literal559_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 108) ) { return retval; }

            // grammar/Java.g:1237:5: ( 'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest | 'new' classOrInterfaceType classCreatorRest | arrayCreator )
            int alt147=3;
            int LA147_0 = input.LA(1);

            if ( (LA147_0==NEW) ) {
                int LA147_1 = input.LA(2);

                if ( (synpred236_Java()) ) {
                    alt147=1;
                }
                else if ( (synpred237_Java()) ) {
                    alt147=2;
                }
                else if ( (true) ) {
                    alt147=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 147, 1, input);

                    throw nvae;

                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 147, 0, input);

                throw nvae;

            }
            switch (alt147) {
                case 1 :
                    // grammar/Java.g:1237:9: 'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal555=(Token)match(input,NEW,FOLLOW_NEW_in_creator7343); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal555_tree = 
                    (CommonTree)adaptor.create(string_literal555)
                    ;
                    adaptor.addChild(root_0, string_literal555_tree);
                    }

                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator7345);
                    nonWildcardTypeArguments556=nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments556.getTree());

                    pushFollow(FOLLOW_classOrInterfaceType_in_creator7347);
                    classOrInterfaceType557=classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceType557.getTree());

                    pushFollow(FOLLOW_classCreatorRest_in_creator7349);
                    classCreatorRest558=classCreatorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest558.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:1238:9: 'new' classOrInterfaceType classCreatorRest
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal559=(Token)match(input,NEW,FOLLOW_NEW_in_creator7359); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal559_tree = 
                    (CommonTree)adaptor.create(string_literal559)
                    ;
                    adaptor.addChild(root_0, string_literal559_tree);
                    }

                    pushFollow(FOLLOW_classOrInterfaceType_in_creator7361);
                    classOrInterfaceType560=classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceType560.getTree());

                    pushFollow(FOLLOW_classCreatorRest_in_creator7363);
                    classCreatorRest561=classCreatorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest561.getTree());

                    }
                    break;
                case 3 :
                    // grammar/Java.g:1239:9: arrayCreator
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_arrayCreator_in_creator7373);
                    arrayCreator562=arrayCreator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayCreator562.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 108, creator_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "creator"


    public static class arrayCreator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arrayCreator"
    // grammar/Java.g:1242:1: arrayCreator : ( 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer | 'new' createdName '[' expression ']' ( '[' expression ']' )* ( '[' ']' )* );
    public final JavaParser.arrayCreator_return arrayCreator() throws RecognitionException {
        JavaParser.arrayCreator_return retval = new JavaParser.arrayCreator_return();
        retval.start = input.LT(1);

        int arrayCreator_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal563=null;
        Token char_literal565=null;
        Token char_literal566=null;
        Token char_literal567=null;
        Token char_literal568=null;
        Token string_literal570=null;
        Token char_literal572=null;
        Token char_literal574=null;
        Token char_literal575=null;
        Token char_literal577=null;
        Token char_literal578=null;
        Token char_literal579=null;
        JavaParser.createdName_return createdName564 =null;

        JavaParser.arrayInitializer_return arrayInitializer569 =null;

        JavaParser.createdName_return createdName571 =null;

        JavaParser.expression_return expression573 =null;

        JavaParser.expression_return expression576 =null;


        CommonTree string_literal563_tree=null;
        CommonTree char_literal565_tree=null;
        CommonTree char_literal566_tree=null;
        CommonTree char_literal567_tree=null;
        CommonTree char_literal568_tree=null;
        CommonTree string_literal570_tree=null;
        CommonTree char_literal572_tree=null;
        CommonTree char_literal574_tree=null;
        CommonTree char_literal575_tree=null;
        CommonTree char_literal577_tree=null;
        CommonTree char_literal578_tree=null;
        CommonTree char_literal579_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 109) ) { return retval; }

            // grammar/Java.g:1243:5: ( 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer | 'new' createdName '[' expression ']' ( '[' expression ']' )* ( '[' ']' )* )
            int alt151=2;
            int LA151_0 = input.LA(1);

            if ( (LA151_0==NEW) ) {
                int LA151_1 = input.LA(2);

                if ( (synpred239_Java()) ) {
                    alt151=1;
                }
                else if ( (true) ) {
                    alt151=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 151, 1, input);

                    throw nvae;

                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 151, 0, input);

                throw nvae;

            }
            switch (alt151) {
                case 1 :
                    // grammar/Java.g:1243:9: 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal563=(Token)match(input,NEW,FOLLOW_NEW_in_arrayCreator7398); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal563_tree = 
                    (CommonTree)adaptor.create(string_literal563)
                    ;
                    adaptor.addChild(root_0, string_literal563_tree);
                    }

                    pushFollow(FOLLOW_createdName_in_arrayCreator7400);
                    createdName564=createdName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, createdName564.getTree());

                    char_literal565=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7410); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal565_tree = 
                    (CommonTree)adaptor.create(char_literal565)
                    ;
                    adaptor.addChild(root_0, char_literal565_tree);
                    }

                    char_literal566=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7412); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal566_tree = 
                    (CommonTree)adaptor.create(char_literal566)
                    ;
                    adaptor.addChild(root_0, char_literal566_tree);
                    }

                    // grammar/Java.g:1245:9: ( '[' ']' )*
                    loop148:
                    do {
                        int alt148=2;
                        int LA148_0 = input.LA(1);

                        if ( (LA148_0==LBRACKET) ) {
                            alt148=1;
                        }


                        switch (alt148) {
                    	case 1 :
                    	    // grammar/Java.g:1245:10: '[' ']'
                    	    {
                    	    char_literal567=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7423); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal567_tree = 
                    	    (CommonTree)adaptor.create(char_literal567)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal567_tree);
                    	    }

                    	    char_literal568=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7425); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal568_tree = 
                    	    (CommonTree)adaptor.create(char_literal568)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal568_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop148;
                        }
                    } while (true);


                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreator7446);
                    arrayInitializer569=arrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayInitializer569.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:1249:9: 'new' createdName '[' expression ']' ( '[' expression ']' )* ( '[' ']' )*
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    string_literal570=(Token)match(input,NEW,FOLLOW_NEW_in_arrayCreator7457); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal570_tree = 
                    (CommonTree)adaptor.create(string_literal570)
                    ;
                    adaptor.addChild(root_0, string_literal570_tree);
                    }

                    pushFollow(FOLLOW_createdName_in_arrayCreator7459);
                    createdName571=createdName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, createdName571.getTree());

                    char_literal572=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7469); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal572_tree = 
                    (CommonTree)adaptor.create(char_literal572)
                    ;
                    adaptor.addChild(root_0, char_literal572_tree);
                    }

                    pushFollow(FOLLOW_expression_in_arrayCreator7471);
                    expression573=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression573.getTree());

                    char_literal574=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7481); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal574_tree = 
                    (CommonTree)adaptor.create(char_literal574)
                    ;
                    adaptor.addChild(root_0, char_literal574_tree);
                    }

                    // grammar/Java.g:1252:9: ( '[' expression ']' )*
                    loop149:
                    do {
                        int alt149=2;
                        int LA149_0 = input.LA(1);

                        if ( (LA149_0==LBRACKET) ) {
                            int LA149_1 = input.LA(2);

                            if ( (synpred240_Java()) ) {
                                alt149=1;
                            }


                        }


                        switch (alt149) {
                    	case 1 :
                    	    // grammar/Java.g:1252:13: '[' expression ']'
                    	    {
                    	    char_literal575=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7495); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal575_tree = 
                    	    (CommonTree)adaptor.create(char_literal575)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal575_tree);
                    	    }

                    	    pushFollow(FOLLOW_expression_in_arrayCreator7497);
                    	    expression576=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression576.getTree());

                    	    char_literal577=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7511); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal577_tree = 
                    	    (CommonTree)adaptor.create(char_literal577)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal577_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop149;
                        }
                    } while (true);


                    // grammar/Java.g:1255:9: ( '[' ']' )*
                    loop150:
                    do {
                        int alt150=2;
                        int LA150_0 = input.LA(1);

                        if ( (LA150_0==LBRACKET) ) {
                            int LA150_2 = input.LA(2);

                            if ( (LA150_2==RBRACKET) ) {
                                alt150=1;
                            }


                        }


                        switch (alt150) {
                    	case 1 :
                    	    // grammar/Java.g:1255:10: '[' ']'
                    	    {
                    	    char_literal578=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7533); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal578_tree = 
                    	    (CommonTree)adaptor.create(char_literal578)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal578_tree);
                    	    }

                    	    char_literal579=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7535); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal579_tree = 
                    	    (CommonTree)adaptor.create(char_literal579)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal579_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop150;
                        }
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 109, arrayCreator_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "arrayCreator"


    public static class variableInitializer_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "variableInitializer"
    // grammar/Java.g:1259:1: variableInitializer : ( arrayInitializer | expression );
    public final JavaParser.variableInitializer_return variableInitializer() throws RecognitionException {
        JavaParser.variableInitializer_return retval = new JavaParser.variableInitializer_return();
        retval.start = input.LT(1);

        int variableInitializer_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.arrayInitializer_return arrayInitializer580 =null;

        JavaParser.expression_return expression581 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 110) ) { return retval; }

            // grammar/Java.g:1260:5: ( arrayInitializer | expression )
            int alt152=2;
            int LA152_0 = input.LA(1);

            if ( (LA152_0==LBRACE) ) {
                alt152=1;
            }
            else if ( (LA152_0==BANG||LA152_0==BOOLEAN||LA152_0==BYTE||(LA152_0 >= CHAR && LA152_0 <= CHARLITERAL)||(LA152_0 >= DOUBLE && LA152_0 <= DOUBLELITERAL)||LA152_0==FALSE||(LA152_0 >= FLOAT && LA152_0 <= FLOATLITERAL)||LA152_0==IDENTIFIER||LA152_0==INT||LA152_0==INTLITERAL||(LA152_0 >= LONG && LA152_0 <= LPAREN)||(LA152_0 >= NEW && LA152_0 <= NULL)||LA152_0==PLUS||LA152_0==PLUSPLUS||LA152_0==SHORT||(LA152_0 >= STRINGLITERAL && LA152_0 <= SUB)||(LA152_0 >= SUBSUB && LA152_0 <= SUPER)||LA152_0==THIS||LA152_0==TILDE||LA152_0==TRUE||LA152_0==VOID) ) {
                alt152=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 152, 0, input);

                throw nvae;

            }
            switch (alt152) {
                case 1 :
                    // grammar/Java.g:1260:9: arrayInitializer
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer7565);
                    arrayInitializer580=arrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayInitializer580.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:1261:9: expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_expression_in_variableInitializer7575);
                    expression581=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression581.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 110, variableInitializer_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "variableInitializer"


    public static class arrayInitializer_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arrayInitializer"
    // grammar/Java.g:1264:1: arrayInitializer : '{' ( variableInitializer ( ',' variableInitializer )* )? ( ',' )? '}' ;
    public final JavaParser.arrayInitializer_return arrayInitializer() throws RecognitionException {
        JavaParser.arrayInitializer_return retval = new JavaParser.arrayInitializer_return();
        retval.start = input.LT(1);

        int arrayInitializer_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal582=null;
        Token char_literal584=null;
        Token char_literal586=null;
        Token char_literal587=null;
        JavaParser.variableInitializer_return variableInitializer583 =null;

        JavaParser.variableInitializer_return variableInitializer585 =null;


        CommonTree char_literal582_tree=null;
        CommonTree char_literal584_tree=null;
        CommonTree char_literal586_tree=null;
        CommonTree char_literal587_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 111) ) { return retval; }

            // grammar/Java.g:1265:5: ( '{' ( variableInitializer ( ',' variableInitializer )* )? ( ',' )? '}' )
            // grammar/Java.g:1265:9: '{' ( variableInitializer ( ',' variableInitializer )* )? ( ',' )? '}'
            {
            root_0 = (CommonTree)adaptor.nil();


            char_literal582=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_arrayInitializer7594); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal582_tree = 
            (CommonTree)adaptor.create(char_literal582)
            ;
            adaptor.addChild(root_0, char_literal582_tree);
            }

            // grammar/Java.g:1266:13: ( variableInitializer ( ',' variableInitializer )* )?
            int alt154=2;
            int LA154_0 = input.LA(1);

            if ( (LA154_0==BANG||LA154_0==BOOLEAN||LA154_0==BYTE||(LA154_0 >= CHAR && LA154_0 <= CHARLITERAL)||(LA154_0 >= DOUBLE && LA154_0 <= DOUBLELITERAL)||LA154_0==FALSE||(LA154_0 >= FLOAT && LA154_0 <= FLOATLITERAL)||LA154_0==IDENTIFIER||LA154_0==INT||LA154_0==INTLITERAL||LA154_0==LBRACE||(LA154_0 >= LONG && LA154_0 <= LPAREN)||(LA154_0 >= NEW && LA154_0 <= NULL)||LA154_0==PLUS||LA154_0==PLUSPLUS||LA154_0==SHORT||(LA154_0 >= STRINGLITERAL && LA154_0 <= SUB)||(LA154_0 >= SUBSUB && LA154_0 <= SUPER)||LA154_0==THIS||LA154_0==TILDE||LA154_0==TRUE||LA154_0==VOID) ) {
                alt154=1;
            }
            switch (alt154) {
                case 1 :
                    // grammar/Java.g:1266:14: variableInitializer ( ',' variableInitializer )*
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer7609);
                    variableInitializer583=variableInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer583.getTree());

                    // grammar/Java.g:1267:17: ( ',' variableInitializer )*
                    loop153:
                    do {
                        int alt153=2;
                        int LA153_0 = input.LA(1);

                        if ( (LA153_0==COMMA) ) {
                            int LA153_1 = input.LA(2);

                            if ( (LA153_1==BANG||LA153_1==BOOLEAN||LA153_1==BYTE||(LA153_1 >= CHAR && LA153_1 <= CHARLITERAL)||(LA153_1 >= DOUBLE && LA153_1 <= DOUBLELITERAL)||LA153_1==FALSE||(LA153_1 >= FLOAT && LA153_1 <= FLOATLITERAL)||LA153_1==IDENTIFIER||LA153_1==INT||LA153_1==INTLITERAL||LA153_1==LBRACE||(LA153_1 >= LONG && LA153_1 <= LPAREN)||(LA153_1 >= NEW && LA153_1 <= NULL)||LA153_1==PLUS||LA153_1==PLUSPLUS||LA153_1==SHORT||(LA153_1 >= STRINGLITERAL && LA153_1 <= SUB)||(LA153_1 >= SUBSUB && LA153_1 <= SUPER)||LA153_1==THIS||LA153_1==TILDE||LA153_1==TRUE||LA153_1==VOID) ) {
                                alt153=1;
                            }


                        }


                        switch (alt153) {
                    	case 1 :
                    	    // grammar/Java.g:1267:18: ',' variableInitializer
                    	    {
                    	    char_literal584=(Token)match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer7628); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal584_tree = 
                    	    (CommonTree)adaptor.create(char_literal584)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal584_tree);
                    	    }

                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer7630);
                    	    variableInitializer585=variableInitializer();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer585.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop153;
                        }
                    } while (true);


                    }
                    break;

            }


            // grammar/Java.g:1270:13: ( ',' )?
            int alt155=2;
            int LA155_0 = input.LA(1);

            if ( (LA155_0==COMMA) ) {
                alt155=1;
            }
            switch (alt155) {
                case 1 :
                    // grammar/Java.g:1270:14: ','
                    {
                    char_literal586=(Token)match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer7679); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal586_tree = 
                    (CommonTree)adaptor.create(char_literal586)
                    ;
                    adaptor.addChild(root_0, char_literal586_tree);
                    }

                    }
                    break;

            }


            char_literal587=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_arrayInitializer7691); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal587_tree = 
            (CommonTree)adaptor.create(char_literal587)
            ;
            adaptor.addChild(root_0, char_literal587_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 111, arrayInitializer_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "arrayInitializer"


    public static class createdName_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "createdName"
    // grammar/Java.g:1275:1: createdName : ( classOrInterfaceType | primitiveType );
    public final JavaParser.createdName_return createdName() throws RecognitionException {
        JavaParser.createdName_return retval = new JavaParser.createdName_return();
        retval.start = input.LT(1);

        int createdName_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.classOrInterfaceType_return classOrInterfaceType588 =null;

        JavaParser.primitiveType_return primitiveType589 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 112) ) { return retval; }

            // grammar/Java.g:1276:5: ( classOrInterfaceType | primitiveType )
            int alt156=2;
            int LA156_0 = input.LA(1);

            if ( (LA156_0==IDENTIFIER) ) {
                alt156=1;
            }
            else if ( (LA156_0==BOOLEAN||LA156_0==BYTE||LA156_0==CHAR||LA156_0==DOUBLE||LA156_0==FLOAT||LA156_0==INT||LA156_0==LONG||LA156_0==SHORT) ) {
                alt156=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 156, 0, input);

                throw nvae;

            }
            switch (alt156) {
                case 1 :
                    // grammar/Java.g:1276:9: classOrInterfaceType
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_classOrInterfaceType_in_createdName7724);
                    classOrInterfaceType588=classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceType588.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:1277:9: primitiveType
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_primitiveType_in_createdName7734);
                    primitiveType589=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType589.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 112, createdName_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "createdName"


    public static class innerCreator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "innerCreator"
    // grammar/Java.g:1280:1: innerCreator : '.' 'new' ( nonWildcardTypeArguments )? IDENTIFIER ( typeArguments )? classCreatorRest ;
    public final JavaParser.innerCreator_return innerCreator() throws RecognitionException {
        JavaParser.innerCreator_return retval = new JavaParser.innerCreator_return();
        retval.start = input.LT(1);

        int innerCreator_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal590=null;
        Token string_literal591=null;
        Token IDENTIFIER593=null;
        JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments592 =null;

        JavaParser.typeArguments_return typeArguments594 =null;

        JavaParser.classCreatorRest_return classCreatorRest595 =null;


        CommonTree char_literal590_tree=null;
        CommonTree string_literal591_tree=null;
        CommonTree IDENTIFIER593_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 113) ) { return retval; }

            // grammar/Java.g:1281:5: ( '.' 'new' ( nonWildcardTypeArguments )? IDENTIFIER ( typeArguments )? classCreatorRest )
            // grammar/Java.g:1281:9: '.' 'new' ( nonWildcardTypeArguments )? IDENTIFIER ( typeArguments )? classCreatorRest
            {
            root_0 = (CommonTree)adaptor.nil();


            char_literal590=(Token)match(input,DOT,FOLLOW_DOT_in_innerCreator7753); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal590_tree = 
            (CommonTree)adaptor.create(char_literal590)
            ;
            adaptor.addChild(root_0, char_literal590_tree);
            }

            string_literal591=(Token)match(input,NEW,FOLLOW_NEW_in_innerCreator7755); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal591_tree = 
            (CommonTree)adaptor.create(string_literal591)
            ;
            adaptor.addChild(root_0, string_literal591_tree);
            }

            // grammar/Java.g:1282:9: ( nonWildcardTypeArguments )?
            int alt157=2;
            int LA157_0 = input.LA(1);

            if ( (LA157_0==LT) ) {
                alt157=1;
            }
            switch (alt157) {
                case 1 :
                    // grammar/Java.g:1282:10: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_innerCreator7766);
                    nonWildcardTypeArguments592=nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments592.getTree());

                    }
                    break;

            }


            IDENTIFIER593=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_innerCreator7787); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER593_tree = 
            (CommonTree)adaptor.create(IDENTIFIER593)
            ;
            adaptor.addChild(root_0, IDENTIFIER593_tree);
            }

            // grammar/Java.g:1285:9: ( typeArguments )?
            int alt158=2;
            int LA158_0 = input.LA(1);

            if ( (LA158_0==LT) ) {
                alt158=1;
            }
            switch (alt158) {
                case 1 :
                    // grammar/Java.g:1285:10: typeArguments
                    {
                    pushFollow(FOLLOW_typeArguments_in_innerCreator7803);
                    typeArguments594=typeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments594.getTree());

                    }
                    break;

            }


            pushFollow(FOLLOW_classCreatorRest_in_innerCreator7824);
            classCreatorRest595=classCreatorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest595.getTree());

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 113, innerCreator_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "innerCreator"


    public static class classCreatorRest_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "classCreatorRest"
    // grammar/Java.g:1291:1: classCreatorRest : arguments ( classBody )? ;
    public final JavaParser.classCreatorRest_return classCreatorRest() throws RecognitionException {
        JavaParser.classCreatorRest_return retval = new JavaParser.classCreatorRest_return();
        retval.start = input.LT(1);

        int classCreatorRest_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.arguments_return arguments596 =null;

        JavaParser.classBody_return classBody597 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 114) ) { return retval; }

            // grammar/Java.g:1292:5: ( arguments ( classBody )? )
            // grammar/Java.g:1292:9: arguments ( classBody )?
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_arguments_in_classCreatorRest7844);
            arguments596=arguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments596.getTree());

            // grammar/Java.g:1293:9: ( classBody )?
            int alt159=2;
            int LA159_0 = input.LA(1);

            if ( (LA159_0==LBRACE) ) {
                alt159=1;
            }
            switch (alt159) {
                case 1 :
                    // grammar/Java.g:1293:10: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_classCreatorRest7855);
                    classBody597=classBody();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classBody597.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 114, classCreatorRest_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "classCreatorRest"


    public static class nonWildcardTypeArguments_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "nonWildcardTypeArguments"
    // grammar/Java.g:1298:1: nonWildcardTypeArguments : '<' typeList '>' ;
    public final JavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments() throws RecognitionException {
        JavaParser.nonWildcardTypeArguments_return retval = new JavaParser.nonWildcardTypeArguments_return();
        retval.start = input.LT(1);

        int nonWildcardTypeArguments_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal598=null;
        Token char_literal600=null;
        JavaParser.typeList_return typeList599 =null;


        CommonTree char_literal598_tree=null;
        CommonTree char_literal600_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 115) ) { return retval; }

            // grammar/Java.g:1299:5: ( '<' typeList '>' )
            // grammar/Java.g:1299:9: '<' typeList '>'
            {
            root_0 = (CommonTree)adaptor.nil();


            char_literal598=(Token)match(input,LT,FOLLOW_LT_in_nonWildcardTypeArguments7886); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal598_tree = 
            (CommonTree)adaptor.create(char_literal598)
            ;
            adaptor.addChild(root_0, char_literal598_tree);
            }

            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments7888);
            typeList599=typeList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeList599.getTree());

            char_literal600=(Token)match(input,GT,FOLLOW_GT_in_nonWildcardTypeArguments7898); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal600_tree = 
            (CommonTree)adaptor.create(char_literal600)
            ;
            adaptor.addChild(root_0, char_literal600_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 115, nonWildcardTypeArguments_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "nonWildcardTypeArguments"


    public static class arguments_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arguments"
    // grammar/Java.g:1303:1: arguments : argumentsInner -> ^( ARG_T argumentsInner ) ;
    public final JavaParser.arguments_return arguments() throws RecognitionException {
        JavaParser.arguments_return retval = new JavaParser.arguments_return();
        retval.start = input.LT(1);

        int arguments_StartIndex = input.index();

        CommonTree root_0 = null;

        JavaParser.argumentsInner_return argumentsInner601 =null;


        RewriteRuleSubtreeStream stream_argumentsInner=new RewriteRuleSubtreeStream(adaptor,"rule argumentsInner");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 116) ) { return retval; }

            // grammar/Java.g:1304:2: ( argumentsInner -> ^( ARG_T argumentsInner ) )
            // grammar/Java.g:1304:3: argumentsInner
            {
            pushFollow(FOLLOW_argumentsInner_in_arguments7911);
            argumentsInner601=argumentsInner();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_argumentsInner.add(argumentsInner601.getTree());

            // AST REWRITE
            // elements: argumentsInner
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 1304:18: -> ^( ARG_T argumentsInner )
            {
                // grammar/Java.g:1304:21: ^( ARG_T argumentsInner )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ARG_T, "ARG_T")
                , root_1);

                adaptor.addChild(root_1, stream_argumentsInner.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 116, arguments_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "arguments"


    public static class argumentsInner_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "argumentsInner"
    // grammar/Java.g:1307:1: argumentsInner : '(' ( expressionList )? ')' ;
    public final JavaParser.argumentsInner_return argumentsInner() throws RecognitionException {
        JavaParser.argumentsInner_return retval = new JavaParser.argumentsInner_return();
        retval.start = input.LT(1);

        int argumentsInner_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal602=null;
        Token char_literal604=null;
        JavaParser.expressionList_return expressionList603 =null;


        CommonTree char_literal602_tree=null;
        CommonTree char_literal604_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 117) ) { return retval; }

            // grammar/Java.g:1308:5: ( '(' ( expressionList )? ')' )
            // grammar/Java.g:1308:9: '(' ( expressionList )? ')'
            {
            root_0 = (CommonTree)adaptor.nil();


            char_literal602=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_argumentsInner7935); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal602_tree = 
            (CommonTree)adaptor.create(char_literal602)
            ;
            adaptor.addChild(root_0, char_literal602_tree);
            }

            // grammar/Java.g:1308:13: ( expressionList )?
            int alt160=2;
            int LA160_0 = input.LA(1);

            if ( (LA160_0==BANG||LA160_0==BOOLEAN||LA160_0==BYTE||(LA160_0 >= CHAR && LA160_0 <= CHARLITERAL)||(LA160_0 >= DOUBLE && LA160_0 <= DOUBLELITERAL)||LA160_0==FALSE||(LA160_0 >= FLOAT && LA160_0 <= FLOATLITERAL)||LA160_0==IDENTIFIER||LA160_0==INT||LA160_0==INTLITERAL||(LA160_0 >= LONG && LA160_0 <= LPAREN)||(LA160_0 >= NEW && LA160_0 <= NULL)||LA160_0==PLUS||LA160_0==PLUSPLUS||LA160_0==SHORT||(LA160_0 >= STRINGLITERAL && LA160_0 <= SUB)||(LA160_0 >= SUBSUB && LA160_0 <= SUPER)||LA160_0==THIS||LA160_0==TILDE||LA160_0==TRUE||LA160_0==VOID) ) {
                alt160=1;
            }
            switch (alt160) {
                case 1 :
                    // grammar/Java.g:1308:14: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_argumentsInner7938);
                    expressionList603=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList603.getTree());

                    }
                    break;

            }


            char_literal604=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_argumentsInner7951); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal604_tree = 
            (CommonTree)adaptor.create(char_literal604)
            ;
            adaptor.addChild(root_0, char_literal604_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 117, argumentsInner_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "argumentsInner"


    public static class navigableIdentifier_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "navigableIdentifier"
    // grammar/Java.g:1312:1: navigableIdentifier : IDENTIFIER -> ^( VAR_NAME_T IDENTIFIER ) ;
    public final JavaParser.navigableIdentifier_return navigableIdentifier() throws RecognitionException {
        JavaParser.navigableIdentifier_return retval = new JavaParser.navigableIdentifier_return();
        retval.start = input.LT(1);

        int navigableIdentifier_StartIndex = input.index();

        CommonTree root_0 = null;

        Token IDENTIFIER605=null;

        CommonTree IDENTIFIER605_tree=null;
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 118) ) { return retval; }

            // grammar/Java.g:1313:2: ( IDENTIFIER -> ^( VAR_NAME_T IDENTIFIER ) )
            // grammar/Java.g:1313:4: IDENTIFIER
            {
            IDENTIFIER605=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_navigableIdentifier7965); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IDENTIFIER.add(IDENTIFIER605);


            // AST REWRITE
            // elements: IDENTIFIER
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 1313:15: -> ^( VAR_NAME_T IDENTIFIER )
            {
                // grammar/Java.g:1313:18: ^( VAR_NAME_T IDENTIFIER )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(VAR_NAME_T, "VAR_NAME_T")
                , root_1);

                adaptor.addChild(root_1, 
                stream_IDENTIFIER.nextNode()
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 118, navigableIdentifier_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "navigableIdentifier"


    public static class literal_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "literal"
    // grammar/Java.g:1317:1: literal : ( INTLITERAL | LONGLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | TRUE | FALSE | NULL );
    public final JavaParser.literal_return literal() throws RecognitionException {
        JavaParser.literal_return retval = new JavaParser.literal_return();
        retval.start = input.LT(1);

        int literal_StartIndex = input.index();

        CommonTree root_0 = null;

        Token set606=null;

        CommonTree set606_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 119) ) { return retval; }

            // grammar/Java.g:1318:5: ( INTLITERAL | LONGLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | TRUE | FALSE | NULL )
            // grammar/Java.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set606=(Token)input.LT(1);

            if ( input.LA(1)==CHARLITERAL||input.LA(1)==DOUBLELITERAL||input.LA(1)==FALSE||input.LA(1)==FLOATLITERAL||input.LA(1)==INTLITERAL||input.LA(1)==LONGLITERAL||input.LA(1)==NULL||input.LA(1)==STRINGLITERAL||input.LA(1)==TRUE ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set606)
                );
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 119, literal_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "literal"


    public static class classHeader_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "classHeader"
    // grammar/Java.g:1333:1: classHeader : modifiers 'class' IDENTIFIER ;
    public final JavaParser.classHeader_return classHeader() throws RecognitionException {
        JavaParser.classHeader_return retval = new JavaParser.classHeader_return();
        retval.start = input.LT(1);

        int classHeader_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal608=null;
        Token IDENTIFIER609=null;
        JavaParser.modifiers_return modifiers607 =null;


        CommonTree string_literal608_tree=null;
        CommonTree IDENTIFIER609_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 120) ) { return retval; }

            // grammar/Java.g:1334:5: ( modifiers 'class' IDENTIFIER )
            // grammar/Java.g:1334:9: modifiers 'class' IDENTIFIER
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_classHeader8093);
            modifiers607=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers607.getTree());

            string_literal608=(Token)match(input,CLASS,FOLLOW_CLASS_in_classHeader8095); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal608_tree = 
            (CommonTree)adaptor.create(string_literal608)
            ;
            adaptor.addChild(root_0, string_literal608_tree);
            }

            IDENTIFIER609=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classHeader8097); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER609_tree = 
            (CommonTree)adaptor.create(IDENTIFIER609)
            ;
            adaptor.addChild(root_0, IDENTIFIER609_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 120, classHeader_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "classHeader"


    public static class enumHeader_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "enumHeader"
    // grammar/Java.g:1337:1: enumHeader : modifiers ( 'enum' | IDENTIFIER ) IDENTIFIER ;
    public final JavaParser.enumHeader_return enumHeader() throws RecognitionException {
        JavaParser.enumHeader_return retval = new JavaParser.enumHeader_return();
        retval.start = input.LT(1);

        int enumHeader_StartIndex = input.index();

        CommonTree root_0 = null;

        Token set611=null;
        Token IDENTIFIER612=null;
        JavaParser.modifiers_return modifiers610 =null;


        CommonTree set611_tree=null;
        CommonTree IDENTIFIER612_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 121) ) { return retval; }

            // grammar/Java.g:1338:5: ( modifiers ( 'enum' | IDENTIFIER ) IDENTIFIER )
            // grammar/Java.g:1338:9: modifiers ( 'enum' | IDENTIFIER ) IDENTIFIER
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_enumHeader8116);
            modifiers610=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers610.getTree());

            set611=(Token)input.LT(1);

            if ( input.LA(1)==ENUM||input.LA(1)==IDENTIFIER ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set611)
                );
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            IDENTIFIER612=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_enumHeader8124); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER612_tree = 
            (CommonTree)adaptor.create(IDENTIFIER612)
            ;
            adaptor.addChild(root_0, IDENTIFIER612_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 121, enumHeader_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "enumHeader"


    public static class interfaceHeader_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "interfaceHeader"
    // grammar/Java.g:1341:1: interfaceHeader : modifiers 'interface' IDENTIFIER ;
    public final JavaParser.interfaceHeader_return interfaceHeader() throws RecognitionException {
        JavaParser.interfaceHeader_return retval = new JavaParser.interfaceHeader_return();
        retval.start = input.LT(1);

        int interfaceHeader_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal614=null;
        Token IDENTIFIER615=null;
        JavaParser.modifiers_return modifiers613 =null;


        CommonTree string_literal614_tree=null;
        CommonTree IDENTIFIER615_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 122) ) { return retval; }

            // grammar/Java.g:1342:5: ( modifiers 'interface' IDENTIFIER )
            // grammar/Java.g:1342:9: modifiers 'interface' IDENTIFIER
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_interfaceHeader8143);
            modifiers613=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers613.getTree());

            string_literal614=(Token)match(input,INTERFACE,FOLLOW_INTERFACE_in_interfaceHeader8145); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal614_tree = 
            (CommonTree)adaptor.create(string_literal614)
            ;
            adaptor.addChild(root_0, string_literal614_tree);
            }

            IDENTIFIER615=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_interfaceHeader8147); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER615_tree = 
            (CommonTree)adaptor.create(IDENTIFIER615)
            ;
            adaptor.addChild(root_0, IDENTIFIER615_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 122, interfaceHeader_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "interfaceHeader"


    public static class annotationHeader_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "annotationHeader"
    // grammar/Java.g:1345:1: annotationHeader : modifiers '@' 'interface' IDENTIFIER ;
    public final JavaParser.annotationHeader_return annotationHeader() throws RecognitionException {
        JavaParser.annotationHeader_return retval = new JavaParser.annotationHeader_return();
        retval.start = input.LT(1);

        int annotationHeader_StartIndex = input.index();

        CommonTree root_0 = null;

        Token char_literal617=null;
        Token string_literal618=null;
        Token IDENTIFIER619=null;
        JavaParser.modifiers_return modifiers616 =null;


        CommonTree char_literal617_tree=null;
        CommonTree string_literal618_tree=null;
        CommonTree IDENTIFIER619_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 123) ) { return retval; }

            // grammar/Java.g:1346:5: ( modifiers '@' 'interface' IDENTIFIER )
            // grammar/Java.g:1346:9: modifiers '@' 'interface' IDENTIFIER
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_annotationHeader8166);
            modifiers616=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers616.getTree());

            char_literal617=(Token)match(input,MONKEYS_AT,FOLLOW_MONKEYS_AT_in_annotationHeader8168); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal617_tree = 
            (CommonTree)adaptor.create(char_literal617)
            ;
            adaptor.addChild(root_0, char_literal617_tree);
            }

            string_literal618=(Token)match(input,INTERFACE,FOLLOW_INTERFACE_in_annotationHeader8170); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal618_tree = 
            (CommonTree)adaptor.create(string_literal618)
            ;
            adaptor.addChild(root_0, string_literal618_tree);
            }

            IDENTIFIER619=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_annotationHeader8172); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER619_tree = 
            (CommonTree)adaptor.create(IDENTIFIER619)
            ;
            adaptor.addChild(root_0, IDENTIFIER619_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 123, annotationHeader_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "annotationHeader"


    public static class typeHeader_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "typeHeader"
    // grammar/Java.g:1349:1: typeHeader : modifiers ( 'class' | 'enum' | ( ( '@' )? 'interface' ) ) IDENTIFIER ;
    public final JavaParser.typeHeader_return typeHeader() throws RecognitionException {
        JavaParser.typeHeader_return retval = new JavaParser.typeHeader_return();
        retval.start = input.LT(1);

        int typeHeader_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal621=null;
        Token string_literal622=null;
        Token char_literal623=null;
        Token string_literal624=null;
        Token IDENTIFIER625=null;
        JavaParser.modifiers_return modifiers620 =null;


        CommonTree string_literal621_tree=null;
        CommonTree string_literal622_tree=null;
        CommonTree char_literal623_tree=null;
        CommonTree string_literal624_tree=null;
        CommonTree IDENTIFIER625_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 124) ) { return retval; }

            // grammar/Java.g:1350:5: ( modifiers ( 'class' | 'enum' | ( ( '@' )? 'interface' ) ) IDENTIFIER )
            // grammar/Java.g:1350:9: modifiers ( 'class' | 'enum' | ( ( '@' )? 'interface' ) ) IDENTIFIER
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_typeHeader8191);
            modifiers620=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers620.getTree());

            // grammar/Java.g:1350:19: ( 'class' | 'enum' | ( ( '@' )? 'interface' ) )
            int alt162=3;
            switch ( input.LA(1) ) {
            case CLASS:
                {
                alt162=1;
                }
                break;
            case ENUM:
                {
                alt162=2;
                }
                break;
            case INTERFACE:
            case MONKEYS_AT:
                {
                alt162=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 162, 0, input);

                throw nvae;

            }

            switch (alt162) {
                case 1 :
                    // grammar/Java.g:1350:20: 'class'
                    {
                    string_literal621=(Token)match(input,CLASS,FOLLOW_CLASS_in_typeHeader8194); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal621_tree = 
                    (CommonTree)adaptor.create(string_literal621)
                    ;
                    adaptor.addChild(root_0, string_literal621_tree);
                    }

                    }
                    break;
                case 2 :
                    // grammar/Java.g:1350:28: 'enum'
                    {
                    string_literal622=(Token)match(input,ENUM,FOLLOW_ENUM_in_typeHeader8196); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal622_tree = 
                    (CommonTree)adaptor.create(string_literal622)
                    ;
                    adaptor.addChild(root_0, string_literal622_tree);
                    }

                    }
                    break;
                case 3 :
                    // grammar/Java.g:1350:35: ( ( '@' )? 'interface' )
                    {
                    // grammar/Java.g:1350:35: ( ( '@' )? 'interface' )
                    // grammar/Java.g:1350:36: ( '@' )? 'interface'
                    {
                    // grammar/Java.g:1350:36: ( '@' )?
                    int alt161=2;
                    int LA161_0 = input.LA(1);

                    if ( (LA161_0==MONKEYS_AT) ) {
                        alt161=1;
                    }
                    switch (alt161) {
                        case 1 :
                            // grammar/Java.g:1350:36: '@'
                            {
                            char_literal623=(Token)match(input,MONKEYS_AT,FOLLOW_MONKEYS_AT_in_typeHeader8199); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal623_tree = 
                            (CommonTree)adaptor.create(char_literal623)
                            ;
                            adaptor.addChild(root_0, char_literal623_tree);
                            }

                            }
                            break;

                    }


                    string_literal624=(Token)match(input,INTERFACE,FOLLOW_INTERFACE_in_typeHeader8203); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal624_tree = 
                    (CommonTree)adaptor.create(string_literal624)
                    ;
                    adaptor.addChild(root_0, string_literal624_tree);
                    }

                    }


                    }
                    break;

            }


            IDENTIFIER625=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_typeHeader8207); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER625_tree = 
            (CommonTree)adaptor.create(IDENTIFIER625)
            ;
            adaptor.addChild(root_0, IDENTIFIER625_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 124, typeHeader_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "typeHeader"


    public static class methodHeader_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "methodHeader"
    // grammar/Java.g:1353:1: methodHeader : modifiers ( typeParameters )? ( type | 'void' )? IDENTIFIER '(' ;
    public final JavaParser.methodHeader_return methodHeader() throws RecognitionException {
        JavaParser.methodHeader_return retval = new JavaParser.methodHeader_return();
        retval.start = input.LT(1);

        int methodHeader_StartIndex = input.index();

        CommonTree root_0 = null;

        Token string_literal629=null;
        Token IDENTIFIER630=null;
        Token char_literal631=null;
        JavaParser.modifiers_return modifiers626 =null;

        JavaParser.typeParameters_return typeParameters627 =null;

        JavaParser.type_return type628 =null;


        CommonTree string_literal629_tree=null;
        CommonTree IDENTIFIER630_tree=null;
        CommonTree char_literal631_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 125) ) { return retval; }

            // grammar/Java.g:1354:5: ( modifiers ( typeParameters )? ( type | 'void' )? IDENTIFIER '(' )
            // grammar/Java.g:1354:9: modifiers ( typeParameters )? ( type | 'void' )? IDENTIFIER '('
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_methodHeader8226);
            modifiers626=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers626.getTree());

            // grammar/Java.g:1354:19: ( typeParameters )?
            int alt163=2;
            int LA163_0 = input.LA(1);

            if ( (LA163_0==LT) ) {
                alt163=1;
            }
            switch (alt163) {
                case 1 :
                    // grammar/Java.g:1354:19: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_methodHeader8228);
                    typeParameters627=typeParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters627.getTree());

                    }
                    break;

            }


            // grammar/Java.g:1354:35: ( type | 'void' )?
            int alt164=3;
            switch ( input.LA(1) ) {
                case IDENTIFIER:
                    {
                    int LA164_1 = input.LA(2);

                    if ( (LA164_1==DOT||LA164_1==IDENTIFIER||LA164_1==LBRACKET||LA164_1==LT) ) {
                        alt164=1;
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
                    alt164=1;
                    }
                    break;
                case VOID:
                    {
                    alt164=2;
                    }
                    break;
            }

            switch (alt164) {
                case 1 :
                    // grammar/Java.g:1354:36: type
                    {
                    pushFollow(FOLLOW_type_in_methodHeader8232);
                    type628=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type628.getTree());

                    }
                    break;
                case 2 :
                    // grammar/Java.g:1354:41: 'void'
                    {
                    string_literal629=(Token)match(input,VOID,FOLLOW_VOID_in_methodHeader8234); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal629_tree = 
                    (CommonTree)adaptor.create(string_literal629)
                    ;
                    adaptor.addChild(root_0, string_literal629_tree);
                    }

                    }
                    break;

            }


            IDENTIFIER630=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodHeader8238); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER630_tree = 
            (CommonTree)adaptor.create(IDENTIFIER630)
            ;
            adaptor.addChild(root_0, IDENTIFIER630_tree);
            }

            char_literal631=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_methodHeader8240); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal631_tree = 
            (CommonTree)adaptor.create(char_literal631)
            ;
            adaptor.addChild(root_0, char_literal631_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 125, methodHeader_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "methodHeader"


    public static class fieldHeader_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "fieldHeader"
    // grammar/Java.g:1357:1: fieldHeader : modifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' ) ;
    public final JavaParser.fieldHeader_return fieldHeader() throws RecognitionException {
        JavaParser.fieldHeader_return retval = new JavaParser.fieldHeader_return();
        retval.start = input.LT(1);

        int fieldHeader_StartIndex = input.index();

        CommonTree root_0 = null;

        Token IDENTIFIER634=null;
        Token char_literal635=null;
        Token char_literal636=null;
        Token set637=null;
        JavaParser.modifiers_return modifiers632 =null;

        JavaParser.type_return type633 =null;


        CommonTree IDENTIFIER634_tree=null;
        CommonTree char_literal635_tree=null;
        CommonTree char_literal636_tree=null;
        CommonTree set637_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 126) ) { return retval; }

            // grammar/Java.g:1358:5: ( modifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' ) )
            // grammar/Java.g:1358:9: modifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' )
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_fieldHeader8259);
            modifiers632=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers632.getTree());

            pushFollow(FOLLOW_type_in_fieldHeader8261);
            type633=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type633.getTree());

            IDENTIFIER634=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_fieldHeader8263); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER634_tree = 
            (CommonTree)adaptor.create(IDENTIFIER634)
            ;
            adaptor.addChild(root_0, IDENTIFIER634_tree);
            }

            // grammar/Java.g:1358:35: ( '[' ']' )*
            loop165:
            do {
                int alt165=2;
                int LA165_0 = input.LA(1);

                if ( (LA165_0==LBRACKET) ) {
                    alt165=1;
                }


                switch (alt165) {
            	case 1 :
            	    // grammar/Java.g:1358:36: '[' ']'
            	    {
            	    char_literal635=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_fieldHeader8266); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal635_tree = 
            	    (CommonTree)adaptor.create(char_literal635)
            	    ;
            	    adaptor.addChild(root_0, char_literal635_tree);
            	    }

            	    char_literal636=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_fieldHeader8267); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal636_tree = 
            	    (CommonTree)adaptor.create(char_literal636)
            	    ;
            	    adaptor.addChild(root_0, char_literal636_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop165;
                }
            } while (true);


            set637=(Token)input.LT(1);

            if ( input.LA(1)==COMMA||input.LA(1)==EQ||input.LA(1)==SEMI ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set637)
                );
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 126, fieldHeader_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "fieldHeader"


    public static class localVariableHeader_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "localVariableHeader"
    // grammar/Java.g:1361:1: localVariableHeader : variableModifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' ) ;
    public final JavaParser.localVariableHeader_return localVariableHeader() throws RecognitionException {
        JavaParser.localVariableHeader_return retval = new JavaParser.localVariableHeader_return();
        retval.start = input.LT(1);

        int localVariableHeader_StartIndex = input.index();

        CommonTree root_0 = null;

        Token IDENTIFIER640=null;
        Token char_literal641=null;
        Token char_literal642=null;
        Token set643=null;
        JavaParser.variableModifiers_return variableModifiers638 =null;

        JavaParser.type_return type639 =null;


        CommonTree IDENTIFIER640_tree=null;
        CommonTree char_literal641_tree=null;
        CommonTree char_literal642_tree=null;
        CommonTree set643_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 127) ) { return retval; }

            // grammar/Java.g:1362:5: ( variableModifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' ) )
            // grammar/Java.g:1362:9: variableModifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' )
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_variableModifiers_in_localVariableHeader8296);
            variableModifiers638=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers638.getTree());

            pushFollow(FOLLOW_type_in_localVariableHeader8298);
            type639=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type639.getTree());

            IDENTIFIER640=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_localVariableHeader8300); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER640_tree = 
            (CommonTree)adaptor.create(IDENTIFIER640)
            ;
            adaptor.addChild(root_0, IDENTIFIER640_tree);
            }

            // grammar/Java.g:1362:43: ( '[' ']' )*
            loop166:
            do {
                int alt166=2;
                int LA166_0 = input.LA(1);

                if ( (LA166_0==LBRACKET) ) {
                    alt166=1;
                }


                switch (alt166) {
            	case 1 :
            	    // grammar/Java.g:1362:44: '[' ']'
            	    {
            	    char_literal641=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_localVariableHeader8303); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal641_tree = 
            	    (CommonTree)adaptor.create(char_literal641)
            	    ;
            	    adaptor.addChild(root_0, char_literal641_tree);
            	    }

            	    char_literal642=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_localVariableHeader8304); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal642_tree = 
            	    (CommonTree)adaptor.create(char_literal642)
            	    ;
            	    adaptor.addChild(root_0, char_literal642_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop166;
                }
            } while (true);


            set643=(Token)input.LT(1);

            if ( input.LA(1)==COMMA||input.LA(1)==EQ||input.LA(1)==SEMI ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set643)
                );
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 127, localVariableHeader_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "localVariableHeader"

    // $ANTLR start synpred2_Java
    public final void synpred2_Java_fragment() throws RecognitionException {
        // grammar/Java.g:307:13: ( ( annotations )? packageDeclaration )
        // grammar/Java.g:307:13: ( annotations )? packageDeclaration
        {
        // grammar/Java.g:307:13: ( annotations )?
        int alt167=2;
        int LA167_0 = input.LA(1);

        if ( (LA167_0==MONKEYS_AT) ) {
            alt167=1;
        }
        switch (alt167) {
            case 1 :
                // grammar/Java.g:307:14: annotations
                {
                pushFollow(FOLLOW_annotations_in_synpred2_Java98);
                annotations();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }


        pushFollow(FOLLOW_packageDeclaration_in_synpred2_Java127);
        packageDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred2_Java

    // $ANTLR start synpred12_Java
    public final void synpred12_Java_fragment() throws RecognitionException {
        // grammar/Java.g:359:10: ( classDeclaration )
        // grammar/Java.g:359:10: classDeclaration
        {
        pushFollow(FOLLOW_classDeclaration_in_synpred12_Java499);
        classDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred12_Java

    // $ANTLR start synpred27_Java
    public final void synpred27_Java_fragment() throws RecognitionException {
        // grammar/Java.g:390:9: ( normalClassDeclaration )
        // grammar/Java.g:390:9: normalClassDeclaration
        {
        pushFollow(FOLLOW_normalClassDeclaration_in_synpred27_Java722);
        normalClassDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred27_Java

    // $ANTLR start synpred35_Java
    public final void synpred35_Java_fragment() throws RecognitionException {
        // grammar/Java.g:454:10: ( enumConstants )
        // grammar/Java.g:454:10: enumConstants
        {
        pushFollow(FOLLOW_enumConstants_in_synpred35_Java1209);
        enumConstants();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred35_Java

    // $ANTLR start synpred36_Java
    public final void synpred36_Java_fragment() throws RecognitionException {
        // grammar/Java.g:456:10: ( enumBodyDeclarations )
        // grammar/Java.g:456:10: enumBodyDeclarations
        {
        pushFollow(FOLLOW_enumBodyDeclarations_in_synpred36_Java1231);
        enumBodyDeclarations();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred36_Java

    // $ANTLR start synpred42_Java
    public final void synpred42_Java_fragment() throws RecognitionException {
        // grammar/Java.g:490:9: ( normalInterfaceDeclaration )
        // grammar/Java.g:490:9: normalInterfaceDeclaration
        {
        pushFollow(FOLLOW_normalInterfaceDeclaration_in_synpred42_Java1462);
        normalInterfaceDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred42_Java

    // $ANTLR start synpred51_Java
    public final void synpred51_Java_fragment() throws RecognitionException {
        // grammar/Java.g:532:10: ( fieldDeclaration )
        // grammar/Java.g:532:10: fieldDeclaration
        {
        pushFollow(FOLLOW_fieldDeclaration_in_synpred51_Java1777);
        fieldDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred51_Java

    // $ANTLR start synpred52_Java
    public final void synpred52_Java_fragment() throws RecognitionException {
        // grammar/Java.g:533:10: ( methodDeclaration )
        // grammar/Java.g:533:10: methodDeclaration
        {
        pushFollow(FOLLOW_methodDeclaration_in_synpred52_Java1788);
        methodDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred52_Java

    // $ANTLR start synpred53_Java
    public final void synpred53_Java_fragment() throws RecognitionException {
        // grammar/Java.g:534:10: ( classDeclaration )
        // grammar/Java.g:534:10: classDeclaration
        {
        pushFollow(FOLLOW_classDeclaration_in_synpred53_Java1799);
        classDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred53_Java

    // $ANTLR start synpred56_Java
    public final void synpred56_Java_fragment() throws RecognitionException {
        // grammar/Java.g:554:10: ( explicitConstructorInvocation )
        // grammar/Java.g:554:10: explicitConstructorInvocation
        {
        pushFollow(FOLLOW_explicitConstructorInvocation_in_synpred56_Java1952);
        explicitConstructorInvocation();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred56_Java

    // $ANTLR start synpred58_Java
    public final void synpred58_Java_fragment() throws RecognitionException {
        // grammar/Java.g:546:10: ( modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' )
        // grammar/Java.g:546:10: modifiers ( typeParameters )? IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}'
        {
        pushFollow(FOLLOW_modifiers_in_synpred58_Java1865);
        modifiers();

        state._fsp--;
        if (state.failed) return ;

        // grammar/Java.g:547:9: ( typeParameters )?
        int alt170=2;
        int LA170_0 = input.LA(1);

        if ( (LA170_0==LT) ) {
            alt170=1;
        }
        switch (alt170) {
            case 1 :
                // grammar/Java.g:547:10: typeParameters
                {
                pushFollow(FOLLOW_typeParameters_in_synpred58_Java1876);
                typeParameters();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }


        match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred58_Java1897); if (state.failed) return ;

        pushFollow(FOLLOW_formalParameters_in_synpred58_Java1907);
        formalParameters();

        state._fsp--;
        if (state.failed) return ;

        // grammar/Java.g:551:9: ( 'throws' qualifiedNameList )?
        int alt171=2;
        int LA171_0 = input.LA(1);

        if ( (LA171_0==THROWS) ) {
            alt171=1;
        }
        switch (alt171) {
            case 1 :
                // grammar/Java.g:551:10: 'throws' qualifiedNameList
                {
                match(input,THROWS,FOLLOW_THROWS_in_synpred58_Java1918); if (state.failed) return ;

                pushFollow(FOLLOW_qualifiedNameList_in_synpred58_Java1920);
                qualifiedNameList();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }


        match(input,LBRACE,FOLLOW_LBRACE_in_synpred58_Java1941); if (state.failed) return ;

        // grammar/Java.g:554:9: ( explicitConstructorInvocation )?
        int alt172=2;
        switch ( input.LA(1) ) {
            case LT:
                {
                alt172=1;
                }
                break;
            case THIS:
                {
                int LA172_2 = input.LA(2);

                if ( (synpred56_Java()) ) {
                    alt172=1;
                }
                }
                break;
            case LPAREN:
                {
                int LA172_3 = input.LA(2);

                if ( (synpred56_Java()) ) {
                    alt172=1;
                }
                }
                break;
            case SUPER:
                {
                int LA172_4 = input.LA(2);

                if ( (synpred56_Java()) ) {
                    alt172=1;
                }
                }
                break;
            case IDENTIFIER:
                {
                int LA172_5 = input.LA(2);

                if ( (synpred56_Java()) ) {
                    alt172=1;
                }
                }
                break;
            case CHARLITERAL:
            case DOUBLELITERAL:
            case FALSE:
            case FLOATLITERAL:
            case INTLITERAL:
            case LONGLITERAL:
            case NULL:
            case STRINGLITERAL:
            case TRUE:
                {
                int LA172_6 = input.LA(2);

                if ( (synpred56_Java()) ) {
                    alt172=1;
                }
                }
                break;
            case NEW:
                {
                int LA172_7 = input.LA(2);

                if ( (synpred56_Java()) ) {
                    alt172=1;
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
                int LA172_8 = input.LA(2);

                if ( (synpred56_Java()) ) {
                    alt172=1;
                }
                }
                break;
            case VOID:
                {
                int LA172_9 = input.LA(2);

                if ( (synpred56_Java()) ) {
                    alt172=1;
                }
                }
                break;
        }

        switch (alt172) {
            case 1 :
                // grammar/Java.g:554:10: explicitConstructorInvocation
                {
                pushFollow(FOLLOW_explicitConstructorInvocation_in_synpred58_Java1952);
                explicitConstructorInvocation();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }


        // grammar/Java.g:556:9: ( blockStatement )*
        loop173:
        do {
            int alt173=2;
            int LA173_0 = input.LA(1);

            if ( (LA173_0==EOF||LA173_0==ABSTRACT||(LA173_0 >= ASSERT && LA173_0 <= BANG)||(LA173_0 >= BOOLEAN && LA173_0 <= BYTE)||(LA173_0 >= CHAR && LA173_0 <= CLASS)||LA173_0==CONTINUE||LA173_0==DO||(LA173_0 >= DOUBLE && LA173_0 <= DOUBLELITERAL)||LA173_0==ENUM||(LA173_0 >= FALSE && LA173_0 <= FINAL)||(LA173_0 >= FLOAT && LA173_0 <= FOR)||(LA173_0 >= IDENTIFIER && LA173_0 <= IF)||(LA173_0 >= INT && LA173_0 <= INTLITERAL)||LA173_0==LBRACE||(LA173_0 >= LONG && LA173_0 <= LT)||(LA173_0 >= MONKEYS_AT && LA173_0 <= NULL)||LA173_0==PLUS||(LA173_0 >= PLUSPLUS && LA173_0 <= PUBLIC)||LA173_0==RETURN||(LA173_0 >= SEMI && LA173_0 <= SHORT)||(LA173_0 >= STATIC && LA173_0 <= SUB)||(LA173_0 >= SUBSUB && LA173_0 <= SYNCHRONIZED)||(LA173_0 >= THIS && LA173_0 <= THROW)||(LA173_0 >= TILDE && LA173_0 <= TRY)||(LA173_0 >= VOID && LA173_0 <= WHILE)) ) {
                alt173=1;
            }


            switch (alt173) {
        	case 1 :
        	    // grammar/Java.g:556:10: blockStatement
        	    {
        	    pushFollow(FOLLOW_blockStatement_in_synpred58_Java1974);
        	    blockStatement();

        	    state._fsp--;
        	    if (state.failed) return ;

        	    }
        	    break;

        	default :
        	    break loop173;
            }
        } while (true);


        match(input,RBRACE,FOLLOW_RBRACE_in_synpred58_Java1995); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred58_Java

    // $ANTLR start synpred68_Java
    public final void synpred68_Java_fragment() throws RecognitionException {
        // grammar/Java.g:614:9: ( interfaceFieldDeclaration )
        // grammar/Java.g:614:9: interfaceFieldDeclaration
        {
        pushFollow(FOLLOW_interfaceFieldDeclaration_in_synpred68_Java2388);
        interfaceFieldDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred68_Java

    // $ANTLR start synpred69_Java
    public final void synpred69_Java_fragment() throws RecognitionException {
        // grammar/Java.g:615:9: ( interfaceMethodDeclaration )
        // grammar/Java.g:615:9: interfaceMethodDeclaration
        {
        pushFollow(FOLLOW_interfaceMethodDeclaration_in_synpred69_Java2398);
        interfaceMethodDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred69_Java

    // $ANTLR start synpred70_Java
    public final void synpred70_Java_fragment() throws RecognitionException {
        // grammar/Java.g:616:9: ( interfaceDeclaration )
        // grammar/Java.g:616:9: interfaceDeclaration
        {
        pushFollow(FOLLOW_interfaceDeclaration_in_synpred70_Java2408);
        interfaceDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred70_Java

    // $ANTLR start synpred71_Java
    public final void synpred71_Java_fragment() throws RecognitionException {
        // grammar/Java.g:617:9: ( classDeclaration )
        // grammar/Java.g:617:9: classDeclaration
        {
        pushFollow(FOLLOW_classDeclaration_in_synpred71_Java2418);
        classDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred71_Java

    // $ANTLR start synpred96_Java
    public final void synpred96_Java_fragment() throws RecognitionException {
        // grammar/Java.g:724:9: ( ellipsisParameterDecl )
        // grammar/Java.g:724:9: ellipsisParameterDecl
        {
        pushFollow(FOLLOW_ellipsisParameterDecl_in_synpred96_Java3212);
        ellipsisParameterDecl();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred96_Java

    // $ANTLR start synpred98_Java
    public final void synpred98_Java_fragment() throws RecognitionException {
        // grammar/Java.g:725:9: ( normalParameterDecl ( ',' normalParameterDecl )* )
        // grammar/Java.g:725:9: normalParameterDecl ( ',' normalParameterDecl )*
        {
        pushFollow(FOLLOW_normalParameterDecl_in_synpred98_Java3222);
        normalParameterDecl();

        state._fsp--;
        if (state.failed) return ;

        // grammar/Java.g:726:9: ( ',' normalParameterDecl )*
        loop176:
        do {
            int alt176=2;
            int LA176_0 = input.LA(1);

            if ( (LA176_0==COMMA) ) {
                alt176=1;
            }


            switch (alt176) {
        	case 1 :
        	    // grammar/Java.g:726:10: ',' normalParameterDecl
        	    {
        	    match(input,COMMA,FOLLOW_COMMA_in_synpred98_Java3233); if (state.failed) return ;

        	    pushFollow(FOLLOW_normalParameterDecl_in_synpred98_Java3235);
        	    normalParameterDecl();

        	    state._fsp--;
        	    if (state.failed) return ;

        	    }
        	    break;

        	default :
        	    break loop176;
            }
        } while (true);


        }

    }
    // $ANTLR end synpred98_Java

    // $ANTLR start synpred99_Java
    public final void synpred99_Java_fragment() throws RecognitionException {
        // grammar/Java.g:728:10: ( normalParameterDecl ',' )
        // grammar/Java.g:728:10: normalParameterDecl ','
        {
        pushFollow(FOLLOW_normalParameterDecl_in_synpred99_Java3257);
        normalParameterDecl();

        state._fsp--;
        if (state.failed) return ;

        match(input,COMMA,FOLLOW_COMMA_in_synpred99_Java3267); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred99_Java

    // $ANTLR start synpred100_Java
    public final void synpred100_Java_fragment() throws RecognitionException {
        // grammar/Java.g:735:9: ( variableModifiers )
        // grammar/Java.g:735:9: variableModifiers
        {
        pushFollow(FOLLOW_variableModifiers_in_synpred100_Java3307);
        variableModifiers();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred100_Java

    // $ANTLR start synpred104_Java
    public final void synpred104_Java_fragment() throws RecognitionException {
        // grammar/Java.g:750:9: ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' )
        // grammar/Java.g:750:9: ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';'
        {
        // grammar/Java.g:750:9: ( nonWildcardTypeArguments )?
        int alt177=2;
        int LA177_0 = input.LA(1);

        if ( (LA177_0==LT) ) {
            alt177=1;
        }
        switch (alt177) {
            case 1 :
                // grammar/Java.g:750:10: nonWildcardTypeArguments
                {
                pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred104_Java3497);
                nonWildcardTypeArguments();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }


        if ( input.LA(1)==SUPER||input.LA(1)==THIS ) {
            input.consume();
            state.errorRecovery=false;
            state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }


        pushFollow(FOLLOW_arguments_in_synpred104_Java3555);
        arguments();

        state._fsp--;
        if (state.failed) return ;

        match(input,SEMI,FOLLOW_SEMI_in_synpred104_Java3557); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred104_Java

    // $ANTLR start synpred118_Java
    public final void synpred118_Java_fragment() throws RecognitionException {
        // grammar/Java.g:838:9: ( annotationMethodDeclaration )
        // grammar/Java.g:838:9: annotationMethodDeclaration
        {
        pushFollow(FOLLOW_annotationMethodDeclaration_in_synpred118_Java4140);
        annotationMethodDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred118_Java

    // $ANTLR start synpred119_Java
    public final void synpred119_Java_fragment() throws RecognitionException {
        // grammar/Java.g:839:9: ( interfaceFieldDeclaration )
        // grammar/Java.g:839:9: interfaceFieldDeclaration
        {
        pushFollow(FOLLOW_interfaceFieldDeclaration_in_synpred119_Java4150);
        interfaceFieldDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred119_Java

    // $ANTLR start synpred120_Java
    public final void synpred120_Java_fragment() throws RecognitionException {
        // grammar/Java.g:840:9: ( normalClassDeclaration )
        // grammar/Java.g:840:9: normalClassDeclaration
        {
        pushFollow(FOLLOW_normalClassDeclaration_in_synpred120_Java4160);
        normalClassDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred120_Java

    // $ANTLR start synpred121_Java
    public final void synpred121_Java_fragment() throws RecognitionException {
        // grammar/Java.g:841:9: ( normalInterfaceDeclaration )
        // grammar/Java.g:841:9: normalInterfaceDeclaration
        {
        pushFollow(FOLLOW_normalInterfaceDeclaration_in_synpred121_Java4170);
        normalInterfaceDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred121_Java

    // $ANTLR start synpred122_Java
    public final void synpred122_Java_fragment() throws RecognitionException {
        // grammar/Java.g:842:9: ( enumDeclaration )
        // grammar/Java.g:842:9: enumDeclaration
        {
        pushFollow(FOLLOW_enumDeclaration_in_synpred122_Java4180);
        enumDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred122_Java

    // $ANTLR start synpred123_Java
    public final void synpred123_Java_fragment() throws RecognitionException {
        // grammar/Java.g:843:9: ( annotationTypeDeclaration )
        // grammar/Java.g:843:9: annotationTypeDeclaration
        {
        pushFollow(FOLLOW_annotationTypeDeclaration_in_synpred123_Java4190);
        annotationTypeDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred123_Java

    // $ANTLR start synpred126_Java
    public final void synpred126_Java_fragment() throws RecognitionException {
        // grammar/Java.g:893:9: ( localVariableDeclarationStatement )
        // grammar/Java.g:893:9: localVariableDeclarationStatement
        {
        pushFollow(FOLLOW_localVariableDeclarationStatement_in_synpred126_Java4378);
        localVariableDeclarationStatement();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred126_Java

    // $ANTLR start synpred127_Java
    public final void synpred127_Java_fragment() throws RecognitionException {
        // grammar/Java.g:894:9: ( classOrInterfaceDeclaration )
        // grammar/Java.g:894:9: classOrInterfaceDeclaration
        {
        pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred127_Java4388);
        classOrInterfaceDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred127_Java

    // $ANTLR start synpred131_Java
    public final void synpred131_Java_fragment() throws RecognitionException {
        // grammar/Java.g:914:9: ( ( 'assert' ) expression ( ':' expression )? ';' )
        // grammar/Java.g:914:9: ( 'assert' ) expression ( ':' expression )? ';'
        {
        // grammar/Java.g:914:9: ( 'assert' )
        // grammar/Java.g:914:10: 'assert'
        {
        match(input,ASSERT,FOLLOW_ASSERT_in_synpred131_Java4524); if (state.failed) return ;

        }


        pushFollow(FOLLOW_expression_in_synpred131_Java4544);
        expression();

        state._fsp--;
        if (state.failed) return ;

        // grammar/Java.g:916:20: ( ':' expression )?
        int alt180=2;
        int LA180_0 = input.LA(1);

        if ( (LA180_0==COLON) ) {
            alt180=1;
        }
        switch (alt180) {
            case 1 :
                // grammar/Java.g:916:21: ':' expression
                {
                match(input,COLON,FOLLOW_COLON_in_synpred131_Java4547); if (state.failed) return ;

                pushFollow(FOLLOW_expression_in_synpred131_Java4549);
                expression();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }


        match(input,SEMI,FOLLOW_SEMI_in_synpred131_Java4553); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred131_Java

    // $ANTLR start synpred133_Java
    public final void synpred133_Java_fragment() throws RecognitionException {
        // grammar/Java.g:917:9: ( 'assert' expression ( ':' expression )? ';' )
        // grammar/Java.g:917:9: 'assert' expression ( ':' expression )? ';'
        {
        match(input,ASSERT,FOLLOW_ASSERT_in_synpred133_Java4563); if (state.failed) return ;

        pushFollow(FOLLOW_expression_in_synpred133_Java4566);
        expression();

        state._fsp--;
        if (state.failed) return ;

        // grammar/Java.g:917:30: ( ':' expression )?
        int alt181=2;
        int LA181_0 = input.LA(1);

        if ( (LA181_0==COLON) ) {
            alt181=1;
        }
        switch (alt181) {
            case 1 :
                // grammar/Java.g:917:31: ':' expression
                {
                match(input,COLON,FOLLOW_COLON_in_synpred133_Java4569); if (state.failed) return ;

                pushFollow(FOLLOW_expression_in_synpred133_Java4571);
                expression();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }


        match(input,SEMI,FOLLOW_SEMI_in_synpred133_Java4575); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred133_Java

    // $ANTLR start synpred134_Java
    public final void synpred134_Java_fragment() throws RecognitionException {
        // grammar/Java.g:918:39: ( 'else' statement )
        // grammar/Java.g:918:39: 'else' statement
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred134_Java4592); if (state.failed) return ;

        pushFollow(FOLLOW_statement_in_synpred134_Java4594);
        statement();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred134_Java

    // $ANTLR start synpred149_Java
    public final void synpred149_Java_fragment() throws RecognitionException {
        // grammar/Java.g:933:9: ( expression ';' )
        // grammar/Java.g:933:9: expression ';'
        {
        pushFollow(FOLLOW_expression_in_synpred149_Java4806);
        expression();

        state._fsp--;
        if (state.failed) return ;

        match(input,SEMI,FOLLOW_SEMI_in_synpred149_Java4809); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred149_Java

    // $ANTLR start synpred150_Java
    public final void synpred150_Java_fragment() throws RecognitionException {
        // grammar/Java.g:934:9: ( IDENTIFIER ':' statement )
        // grammar/Java.g:934:9: IDENTIFIER ':' statement
        {
        match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred150_Java4819); if (state.failed) return ;

        match(input,COLON,FOLLOW_COLON_in_synpred150_Java4821); if (state.failed) return ;

        pushFollow(FOLLOW_statement_in_synpred150_Java4823);
        statement();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred150_Java

    // $ANTLR start synpred154_Java
    public final void synpred154_Java_fragment() throws RecognitionException {
        // grammar/Java.g:958:13: ( catches 'finally' block )
        // grammar/Java.g:958:13: catches 'finally' block
        {
        pushFollow(FOLLOW_catches_in_synpred154_Java4978);
        catches();

        state._fsp--;
        if (state.failed) return ;

        match(input,FINALLY,FOLLOW_FINALLY_in_synpred154_Java4980); if (state.failed) return ;

        pushFollow(FOLLOW_block_in_synpred154_Java4982);
        block();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred154_Java

    // $ANTLR start synpred155_Java
    public final void synpred155_Java_fragment() throws RecognitionException {
        // grammar/Java.g:959:13: ( catches )
        // grammar/Java.g:959:13: catches
        {
        pushFollow(FOLLOW_catches_in_synpred155_Java4996);
        catches();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred155_Java

    // $ANTLR start synpred158_Java
    public final void synpred158_Java_fragment() throws RecognitionException {
        // grammar/Java.g:984:9: ( 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement )
        // grammar/Java.g:984:9: 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement
        {
        match(input,FOR,FOLLOW_FOR_in_synpred158_Java5183); if (state.failed) return ;

        match(input,LPAREN,FOLLOW_LPAREN_in_synpred158_Java5185); if (state.failed) return ;

        pushFollow(FOLLOW_variableModifiers_in_synpred158_Java5187);
        variableModifiers();

        state._fsp--;
        if (state.failed) return ;

        pushFollow(FOLLOW_type_in_synpred158_Java5189);
        type();

        state._fsp--;
        if (state.failed) return ;

        match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred158_Java5191); if (state.failed) return ;

        match(input,COLON,FOLLOW_COLON_in_synpred158_Java5193); if (state.failed) return ;

        pushFollow(FOLLOW_expression_in_synpred158_Java5209);
        expression();

        state._fsp--;
        if (state.failed) return ;

        match(input,RPAREN,FOLLOW_RPAREN_in_synpred158_Java5211); if (state.failed) return ;

        pushFollow(FOLLOW_statement_in_synpred158_Java5213);
        statement();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred158_Java

    // $ANTLR start synpred162_Java
    public final void synpred162_Java_fragment() throws RecognitionException {
        // grammar/Java.g:998:9: ( localVariableDeclaration )
        // grammar/Java.g:998:9: localVariableDeclaration
        {
        pushFollow(FOLLOW_localVariableDeclaration_in_synpred162_Java5376);
        localVariableDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred162_Java

    // $ANTLR start synpred203_Java
    public final void synpred203_Java_fragment() throws RecognitionException {
        // grammar/Java.g:1152:9: ( castExpression )
        // grammar/Java.g:1152:9: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred203_Java6610);
        castExpression();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred203_Java

    // $ANTLR start synpred207_Java
    public final void synpred207_Java_fragment() throws RecognitionException {
        // grammar/Java.g:1162:9: ( '(' primitiveType ')' unaryExpression )
        // grammar/Java.g:1162:9: '(' primitiveType ')' unaryExpression
        {
        match(input,LPAREN,FOLLOW_LPAREN_in_synpred207_Java6700); if (state.failed) return ;

        pushFollow(FOLLOW_primitiveType_in_synpred207_Java6702);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        match(input,RPAREN,FOLLOW_RPAREN_in_synpred207_Java6704); if (state.failed) return ;

        pushFollow(FOLLOW_unaryExpression_in_synpred207_Java6706);
        unaryExpression();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred207_Java

    // $ANTLR start synpred215_Java
    public final void synpred215_Java_fragment() throws RecognitionException {
        // grammar/Java.g:1183:24: ( identifierSuffix )
        // grammar/Java.g:1183:24: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred215_Java6859);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred215_Java

    // $ANTLR start synpred216_Java
    public final void synpred216_Java_fragment() throws RecognitionException {
        // grammar/Java.g:1188:15: ( '.' IDENTIFIER )
        // grammar/Java.g:1188:15: '.' IDENTIFIER
        {
        match(input,DOT,FOLLOW_DOT_in_synpred216_Java6901); if (state.failed) return ;

        match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred216_Java6903); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred216_Java

    // $ANTLR start synpred218_Java
    public final void synpred218_Java_fragment() throws RecognitionException {
        // grammar/Java.g:1191:16: ( '.' IDENTIFIER )
        // grammar/Java.g:1191:16: '.' IDENTIFIER
        {
        match(input,DOT,FOLLOW_DOT_in_synpred218_Java6944); if (state.failed) return ;

        match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred218_Java6946); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred218_Java

    // $ANTLR start synpred224_Java
    public final void synpred224_Java_fragment() throws RecognitionException {
        // grammar/Java.g:1214:10: ( '[' expression ']' )
        // grammar/Java.g:1214:10: '[' expression ']'
        {
        match(input,LBRACKET,FOLLOW_LBRACKET_in_synpred224_Java7125); if (state.failed) return ;

        pushFollow(FOLLOW_expression_in_synpred224_Java7127);
        expression();

        state._fsp--;
        if (state.failed) return ;

        match(input,RBRACKET,FOLLOW_RBRACKET_in_synpred224_Java7129); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred224_Java

    // $ANTLR start synpred236_Java
    public final void synpred236_Java_fragment() throws RecognitionException {
        // grammar/Java.g:1237:9: ( 'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest )
        // grammar/Java.g:1237:9: 'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest
        {
        match(input,NEW,FOLLOW_NEW_in_synpred236_Java7343); if (state.failed) return ;

        pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred236_Java7345);
        nonWildcardTypeArguments();

        state._fsp--;
        if (state.failed) return ;

        pushFollow(FOLLOW_classOrInterfaceType_in_synpred236_Java7347);
        classOrInterfaceType();

        state._fsp--;
        if (state.failed) return ;

        pushFollow(FOLLOW_classCreatorRest_in_synpred236_Java7349);
        classCreatorRest();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred236_Java

    // $ANTLR start synpred237_Java
    public final void synpred237_Java_fragment() throws RecognitionException {
        // grammar/Java.g:1238:9: ( 'new' classOrInterfaceType classCreatorRest )
        // grammar/Java.g:1238:9: 'new' classOrInterfaceType classCreatorRest
        {
        match(input,NEW,FOLLOW_NEW_in_synpred237_Java7359); if (state.failed) return ;

        pushFollow(FOLLOW_classOrInterfaceType_in_synpred237_Java7361);
        classOrInterfaceType();

        state._fsp--;
        if (state.failed) return ;

        pushFollow(FOLLOW_classCreatorRest_in_synpred237_Java7363);
        classCreatorRest();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred237_Java

    // $ANTLR start synpred239_Java
    public final void synpred239_Java_fragment() throws RecognitionException {
        // grammar/Java.g:1243:9: ( 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer )
        // grammar/Java.g:1243:9: 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer
        {
        match(input,NEW,FOLLOW_NEW_in_synpred239_Java7398); if (state.failed) return ;

        pushFollow(FOLLOW_createdName_in_synpred239_Java7400);
        createdName();

        state._fsp--;
        if (state.failed) return ;

        match(input,LBRACKET,FOLLOW_LBRACKET_in_synpred239_Java7410); if (state.failed) return ;

        match(input,RBRACKET,FOLLOW_RBRACKET_in_synpred239_Java7412); if (state.failed) return ;

        // grammar/Java.g:1245:9: ( '[' ']' )*
        loop191:
        do {
            int alt191=2;
            int LA191_0 = input.LA(1);

            if ( (LA191_0==LBRACKET) ) {
                alt191=1;
            }


            switch (alt191) {
        	case 1 :
        	    // grammar/Java.g:1245:10: '[' ']'
        	    {
        	    match(input,LBRACKET,FOLLOW_LBRACKET_in_synpred239_Java7423); if (state.failed) return ;

        	    match(input,RBRACKET,FOLLOW_RBRACKET_in_synpred239_Java7425); if (state.failed) return ;

        	    }
        	    break;

        	default :
        	    break loop191;
            }
        } while (true);


        pushFollow(FOLLOW_arrayInitializer_in_synpred239_Java7446);
        arrayInitializer();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred239_Java

    // $ANTLR start synpred240_Java
    public final void synpred240_Java_fragment() throws RecognitionException {
        // grammar/Java.g:1252:13: ( '[' expression ']' )
        // grammar/Java.g:1252:13: '[' expression ']'
        {
        match(input,LBRACKET,FOLLOW_LBRACKET_in_synpred240_Java7495); if (state.failed) return ;

        pushFollow(FOLLOW_expression_in_synpred240_Java7497);
        expression();

        state._fsp--;
        if (state.failed) return ;

        match(input,RBRACKET,FOLLOW_RBRACKET_in_synpred240_Java7511); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred240_Java

    // Delegated rules

    public final boolean synpred56_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred56_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred98_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred98_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred224_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred224_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred207_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred207_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred121_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred121_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred239_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred239_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred69_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred69_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred154_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred154_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred71_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred71_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred133_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred133_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred104_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred104_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred119_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred119_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred215_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred215_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred218_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred218_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred162_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred162_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred126_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred126_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred35_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred35_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred42_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred42_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred51_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred51_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred155_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred155_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred100_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred100_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred68_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred68_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred134_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred134_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred53_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred53_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred216_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred216_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred158_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred158_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred52_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred52_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred236_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred236_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred123_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred123_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred12_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred12_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred36_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred36_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred149_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred149_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred120_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred120_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred122_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred122_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred240_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred240_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred150_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred150_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred27_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred27_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred70_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred70_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred127_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred127_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred58_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred58_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred96_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred96_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred203_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred203_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred99_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred99_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred131_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred131_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred237_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred237_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred118_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred118_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


 

    public static final BitSet FOLLOW_annotations_in_compilationUnit98 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_packageDeclaration_in_compilationUnit127 = new BitSet(new long[]{0x4001010002000012L,0x04103041C00C0004L,0x0000000000000002L});
    public static final BitSet FOLLOW_importDeclaration_in_compilationUnit149 = new BitSet(new long[]{0x4001010002000012L,0x04103041C00C0004L,0x0000000000000002L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit171 = new BitSet(new long[]{0x0001010002000012L,0x04103041C00C0004L,0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_packageDeclaration202 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_qualifiedName_in_packageDeclaration204 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_packageDeclaration206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_importDeclarationInner_in_importDeclaration233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_importDeclarationInner260 = new BitSet(new long[]{0x0800000000000000L,0x0000100000000000L});
    public static final BitSet FOLLOW_STATIC_in_importDeclarationInner271 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_importDeclarationInner292 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_DOT_in_importDeclarationInner294 = new BitSet(new long[]{0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_STAR_in_importDeclarationInner296 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_importDeclarationInner306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_importDeclarationInner316 = new BitSet(new long[]{0x0800000000000000L,0x0000100000000000L});
    public static final BitSet FOLLOW_STATIC_in_importDeclarationInner327 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_importDeclarationInner348 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_DOT_in_importDeclarationInner359 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_importDeclarationInner361 = new BitSet(new long[]{0x0000000400000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_DOT_in_importDeclarationInner383 = new BitSet(new long[]{0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_STAR_in_importDeclarationInner385 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_importDeclarationInner406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedImportName426 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_DOT_in_qualifiedImportName437 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedImportName439 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_typeDeclaration479 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_classOrInterfaceDeclaration499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_modifiers536 = new BitSet(new long[]{0x0001000000000012L,0x04103001C00C0000L,0x0000000000000002L});
    public static final BitSet FOLLOW_PUBLIC_in_modifiers546 = new BitSet(new long[]{0x0001000000000012L,0x04103001C00C0000L,0x0000000000000002L});
    public static final BitSet FOLLOW_PROTECTED_in_modifiers556 = new BitSet(new long[]{0x0001000000000012L,0x04103001C00C0000L,0x0000000000000002L});
    public static final BitSet FOLLOW_PRIVATE_in_modifiers566 = new BitSet(new long[]{0x0001000000000012L,0x04103001C00C0000L,0x0000000000000002L});
    public static final BitSet FOLLOW_STATIC_in_modifiers576 = new BitSet(new long[]{0x0001000000000012L,0x04103001C00C0000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ABSTRACT_in_modifiers586 = new BitSet(new long[]{0x0001000000000012L,0x04103001C00C0000L,0x0000000000000002L});
    public static final BitSet FOLLOW_FINAL_in_modifiers596 = new BitSet(new long[]{0x0001000000000012L,0x04103001C00C0000L,0x0000000000000002L});
    public static final BitSet FOLLOW_NATIVE_in_modifiers606 = new BitSet(new long[]{0x0001000000000012L,0x04103001C00C0000L,0x0000000000000002L});
    public static final BitSet FOLLOW_SYNCHRONIZED_in_modifiers616 = new BitSet(new long[]{0x0001000000000012L,0x04103001C00C0000L,0x0000000000000002L});
    public static final BitSet FOLLOW_TRANSIENT_in_modifiers626 = new BitSet(new long[]{0x0001000000000012L,0x04103001C00C0000L,0x0000000000000002L});
    public static final BitSet FOLLOW_VOLATILE_in_modifiers636 = new BitSet(new long[]{0x0001000000000012L,0x04103001C00C0000L,0x0000000000000002L});
    public static final BitSet FOLLOW_STRICTFP_in_modifiers646 = new BitSet(new long[]{0x0001000000000012L,0x04103001C00C0000L,0x0000000000000002L});
    public static final BitSet FOLLOW_FINAL_in_variableModifiers677 = new BitSet(new long[]{0x0001000000000002L,0x0000000000040000L});
    public static final BitSet FOLLOW_annotation_in_variableModifiers691 = new BitSet(new long[]{0x0001000000000002L,0x0000000000040000L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_classDeclaration722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_classDeclaration732 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalClassDeclarationInner_in_normalClassDeclaration751 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_normalClassDeclarationInner781 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_CLASS_in_normalClassDeclarationInner784 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_normalClassDeclarationInner792 = new BitSet(new long[]{0x2000100000000000L,0x0000000000004080L});
    public static final BitSet FOLLOW_typeParameters_in_normalClassDeclarationInner803 = new BitSet(new long[]{0x2000100000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_extendPart_in_normalClassDeclarationInner825 = new BitSet(new long[]{0x2000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_implementsPart_in_normalClassDeclarationInner847 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_classBody_in_normalClassDeclarationInner868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXTENDS_in_extendPart885 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_type_in_extendPart887 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPLEMENTS_in_implementsPart914 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_typeList_in_implementsPart916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_typeParameters947 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters962 = new BitSet(new long[]{0x0100000010000000L});
    public static final BitSet FOLLOW_COMMA_in_typeParameters977 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters979 = new BitSet(new long[]{0x0100000010000000L});
    public static final BitSet FOLLOW_GT_in_typeParameters1004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_typeParameter1023 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_EXTENDS_in_typeParameter1034 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_typeBound_in_typeParameter1036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeBound1067 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_AMP_in_typeBound1078 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_type_in_typeBound1080 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_modifiers_in_enumDeclaration1111 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_ENUM_in_enumDeclaration1122 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_enumDeclaration1143 = new BitSet(new long[]{0x2000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_IMPLEMENTS_in_enumDeclaration1154 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_typeList_in_enumDeclaration1156 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_enumBody_in_enumDeclaration1177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_enumBody1198 = new BitSet(new long[]{0x0805010802850010L,0x041030C5C00C4886L,0x0000000000000003L});
    public static final BitSet FOLLOW_enumConstants_in_enumBody1209 = new BitSet(new long[]{0x0805010802850010L,0x041030C5C00C4886L,0x0000000000000003L});
    public static final BitSet FOLLOW_enumBodyDeclarations_in_enumBody1231 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_RBRACE_in_enumBody1252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants1272 = new BitSet(new long[]{0x0000000010000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_COMMA_in_enumConstants1283 = new BitSet(new long[]{0x0800000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants1285 = new BitSet(new long[]{0x0000000010000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_enumConstants1298 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotations_in_enumConstant1337 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_enumConstant1358 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002080L});
    public static final BitSet FOLLOW_arguments_in_enumConstant1369 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_classBody_in_enumConstant1391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classBodyDeclaration_in_enumBodyDeclarations1432 = new BitSet(new long[]{0x0805010802850012L,0x041030C1C00C4886L,0x0000000000000003L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration1462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration1472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_normalInterfaceDeclaration1491 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_INTERFACE_in_normalInterfaceDeclaration1493 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_normalInterfaceDeclaration1495 = new BitSet(new long[]{0x0000100000000000L,0x0000000000004080L});
    public static final BitSet FOLLOW_typeParameters_in_normalInterfaceDeclaration1506 = new BitSet(new long[]{0x0000100000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_EXTENDS_in_normalInterfaceDeclaration1528 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_typeList_in_normalInterfaceDeclaration1530 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_interfaceBody_in_normalInterfaceDeclaration1551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList1570 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_COMMA_in_typeList1581 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_type_in_typeList1583 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_LBRACE_in_classBody1613 = new BitSet(new long[]{0x0805010802850010L,0x041030C5C00C4886L,0x0000000000000003L});
    public static final BitSet FOLLOW_classBodyDeclaration_in_classBody1624 = new BitSet(new long[]{0x0805010802850010L,0x041030C5C00C4886L,0x0000000000000003L});
    public static final BitSet FOLLOW_RBRACE_in_classBody1645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_interfaceBody1664 = new BitSet(new long[]{0x0805010802850010L,0x041030C5C00C4806L,0x0000000000000003L});
    public static final BitSet FOLLOW_interfaceBodyDeclaration_in_interfaceBody1675 = new BitSet(new long[]{0x0805010802850010L,0x041030C5C00C4806L,0x0000000000000003L});
    public static final BitSet FOLLOW_RBRACE_in_interfaceBody1696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_classBodyDeclaration1715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STATIC_in_classBodyDeclaration1726 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_block_in_classBodyDeclaration1747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_memberDecl_in_classBodyDeclaration1757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldDeclaration_in_memberDecl1777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodDeclaration_in_memberDecl1788 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_memberDecl1799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_memberDecl1810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodDeclarationInner_in_methodDeclaration1824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_methodDeclarationInner1865 = new BitSet(new long[]{0x0800000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_typeParameters_in_methodDeclarationInner1876 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodDeclarationInner1897 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_formalParameters_in_methodDeclarationInner1907 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000080L});
    public static final BitSet FOLLOW_THROWS_in_methodDeclarationInner1918 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_qualifiedNameList_in_methodDeclarationInner1920 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_LBRACE_in_methodDeclarationInner1941 = new BitSet(new long[]{0x181D811A83870610L,0x1EDEF0D5E83C788EL,0x0000000000000007L});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_methodDeclarationInner1952 = new BitSet(new long[]{0x181D811A83870610L,0x1EDEF0D5E83C388EL,0x0000000000000007L});
    public static final BitSet FOLLOW_blockStatement_in_methodDeclarationInner1974 = new BitSet(new long[]{0x181D811A83870610L,0x1EDEF0D5E83C388EL,0x0000000000000007L});
    public static final BitSet FOLLOW_RBRACE_in_methodDeclarationInner1995 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_methodDeclarationInner2005 = new BitSet(new long[]{0x0804000800850000L,0x0000008000004802L,0x0000000000000001L});
    public static final BitSet FOLLOW_typeParameters_in_methodDeclarationInner2016 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L,0x0000000000000001L});
    public static final BitSet FOLLOW_methodRetValue_in_methodDeclarationInner2037 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodDeclarationInner2047 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_formalParameters_in_methodDeclarationInner2057 = new BitSet(new long[]{0x0000000000000000L,0x0100004000000180L});
    public static final BitSet FOLLOW_LBRACKET_in_methodDeclarationInner2068 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_methodDeclarationInner2070 = new BitSet(new long[]{0x0000000000000000L,0x0100004000000180L});
    public static final BitSet FOLLOW_THROWS_in_methodDeclarationInner2092 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_qualifiedNameList_in_methodDeclarationInner2094 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000080L});
    public static final BitSet FOLLOW_block_in_methodDeclarationInner2129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_methodDeclarationInner2143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodRetValueInner_in_methodRetValue2170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_methodRetValueInner2192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VOID_in_methodRetValueInner2200 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldDeclarationInner_in_fieldDeclaration2212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_fieldDeclarationInner2240 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_type_in_fieldDeclarationInner2250 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_variableDeclarator_in_fieldDeclarationInner2260 = new BitSet(new long[]{0x0000000010000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_COMMA_in_fieldDeclarationInner2271 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_variableDeclarator_in_fieldDeclarationInner2273 = new BitSet(new long[]{0x0000000010000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_fieldDeclarationInner2294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_navigableIdentifier_in_variableDeclarator2313 = new BitSet(new long[]{0x0000020000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_LBRACKET_in_variableDeclarator2324 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_variableDeclarator2326 = new BitSet(new long[]{0x0000020000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_EQ_in_variableDeclarator2348 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830388AL,0x0000000000000001L});
    public static final BitSet FOLLOW_variableInitializer_in_variableDeclarator2350 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceFieldDeclaration_in_interfaceBodyDeclaration2388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodDeclaration_in_interfaceBodyDeclaration2398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_interfaceBodyDeclaration2408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_interfaceBodyDeclaration2418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_interfaceBodyDeclaration2428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_interfaceMethodDeclaration2447 = new BitSet(new long[]{0x0804000800850000L,0x0000008000004802L,0x0000000000000001L});
    public static final BitSet FOLLOW_typeParameters_in_interfaceMethodDeclaration2458 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L,0x0000000000000001L});
    public static final BitSet FOLLOW_type_in_interfaceMethodDeclaration2480 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_VOID_in_interfaceMethodDeclaration2491 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_interfaceMethodDeclaration2511 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_formalParameters_in_interfaceMethodDeclaration2521 = new BitSet(new long[]{0x0000000000000000L,0x0100004000000100L});
    public static final BitSet FOLLOW_LBRACKET_in_interfaceMethodDeclaration2532 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_interfaceMethodDeclaration2534 = new BitSet(new long[]{0x0000000000000000L,0x0100004000000100L});
    public static final BitSet FOLLOW_THROWS_in_interfaceMethodDeclaration2556 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_qualifiedNameList_in_interfaceMethodDeclaration2558 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_interfaceMethodDeclaration2571 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_interfaceFieldDeclaration2592 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_type_in_interfaceFieldDeclaration2594 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_variableDeclarator_in_interfaceFieldDeclaration2596 = new BitSet(new long[]{0x0000000010000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_COMMA_in_interfaceFieldDeclaration2607 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_variableDeclarator_in_interfaceFieldDeclaration2609 = new BitSet(new long[]{0x0000000010000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_interfaceFieldDeclaration2630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_type2650 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_LBRACKET_in_type2661 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_type2663 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_primitiveType_in_type2684 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_LBRACKET_in_type2695 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_type2697 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_classOrInterfaceTypeInner_in_classOrInterfaceType2724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_classOrInterfaceTypeInner2752 = new BitSet(new long[]{0x0000000400000002L,0x0000000000004000L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceTypeInner2763 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_DOT_in_classOrInterfaceTypeInner2785 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_classOrInterfaceTypeInner2787 = new BitSet(new long[]{0x0000000400000002L,0x0000000000004000L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceTypeInner2802 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_primitiveTypeInner_in_primitiveType2842 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_typeArguments2955 = new BitSet(new long[]{0x0804000800850000L,0x0000008200000802L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments2957 = new BitSet(new long[]{0x0100000010000000L});
    public static final BitSet FOLLOW_COMMA_in_typeArguments2968 = new BitSet(new long[]{0x0804000800850000L,0x0000008200000802L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments2970 = new BitSet(new long[]{0x0100000010000000L});
    public static final BitSet FOLLOW_GT_in_typeArguments2991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument3010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUES_in_typeArgument3020 = new BitSet(new long[]{0x0000100000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_set_in_typeArgument3044 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_type_in_typeArgument3088 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList3118 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_COMMA_in_qualifiedNameList3129 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList3131 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_LPAREN_in_formalParameters3161 = new BitSet(new long[]{0x0805000800850000L,0x000000A000040802L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameters3172 = new BitSet(new long[]{0x0000000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_RPAREN_in_formalParameters3193 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ellipsisParameterDecl_in_formalParameterDecls3212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalParameterDecl_in_formalParameterDecls3222 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_COMMA_in_formalParameterDecls3233 = new BitSet(new long[]{0x0805000800850000L,0x0000008000040802L});
    public static final BitSet FOLLOW_normalParameterDecl_in_formalParameterDecls3235 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_normalParameterDecl_in_formalParameterDecls3257 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COMMA_in_formalParameterDecls3267 = new BitSet(new long[]{0x0805000800850000L,0x0000008000040802L});
    public static final BitSet FOLLOW_ellipsisParameterDecl_in_formalParameterDecls3288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_normalParameterDecl3307 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_type_in_normalParameterDecl3310 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_normalParameterDecl3312 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_LBRACKET_in_normalParameterDecl3323 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_normalParameterDecl3325 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_variableModifiers_in_ellipsisParameterDecl3453 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_type_in_ellipsisParameterDecl3463 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_ELLIPSIS_in_ellipsisParameterDecl3466 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_ellipsisParameterDecl3476 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3497 = new BitSet(new long[]{0x0000000000000000L,0x0044000000000000L});
    public static final BitSet FOLLOW_set_in_explicitConstructorInvocation3523 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation3555 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_explicitConstructorInvocation3557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_explicitConstructorInvocation3568 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_DOT_in_explicitConstructorInvocation3578 = new BitSet(new long[]{0x0000000000000000L,0x0004000000004000L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3589 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_SUPER_in_explicitConstructorInvocation3610 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation3620 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_explicitConstructorInvocation3622 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedName3642 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_DOT_in_qualifiedName3653 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedName3655 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_annotation_in_annotations3686 = new BitSet(new long[]{0x0000000000000002L,0x0000000000040000L});
    public static final BitSet FOLLOW_MONKEYS_AT_in_annotation3718 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_qualifiedName_in_annotation3720 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_LPAREN_in_annotation3734 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0A02834388AL,0x0000000000000001L});
    public static final BitSet FOLLOW_elementValuePairs_in_annotation3758 = new BitSet(new long[]{0x0000000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_elementValue_in_annotation3782 = new BitSet(new long[]{0x0000000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_RPAREN_in_annotation3817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs3847 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_COMMA_in_elementValuePairs3858 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs3860 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_navigableIdentifier_in_elementValuePair3890 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_EQ_in_elementValuePair3892 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802834388AL,0x0000000000000001L});
    public static final BitSet FOLLOW_elementValue_in_elementValuePair3894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_elementValue3913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_elementValue3923 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elementValueArrayInitializer_in_elementValue3933 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_elementValueArrayInitializer3952 = new BitSet(new long[]{0x080C801811850400L,0x0A46C0842834388AL,0x0000000000000001L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer3963 = new BitSet(new long[]{0x0000000010000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_COMMA_in_elementValueArrayInitializer3978 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802834388AL,0x0000000000000001L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer3980 = new BitSet(new long[]{0x0000000010000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_COMMA_in_elementValueArrayInitializer4009 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_RBRACE_in_elementValueArrayInitializer4013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_annotationTypeDeclaration4035 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_MONKEYS_AT_in_annotationTypeDeclaration4037 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_INTERFACE_in_annotationTypeDeclaration4047 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_annotationTypeDeclaration4057 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_annotationTypeBody_in_annotationTypeDeclaration4067 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_annotationTypeBody4087 = new BitSet(new long[]{0x0805010802850010L,0x041030C5C00C0806L,0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody4098 = new BitSet(new long[]{0x0805010802850010L,0x041030C5C00C0806L,0x0000000000000002L});
    public static final BitSet FOLLOW_RBRACE_in_annotationTypeBody4119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationMethodDeclaration_in_annotationTypeElementDeclaration4140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceFieldDeclaration_in_annotationTypeElementDeclaration4150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_annotationTypeElementDeclaration4160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementDeclaration4170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_annotationTypeElementDeclaration4180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_annotationTypeElementDeclaration4190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_annotationTypeElementDeclaration4200 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_annotationMethodDeclaration4219 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_type_in_annotationMethodDeclaration4221 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_annotationMethodDeclaration4223 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_LPAREN_in_annotationMethodDeclaration4233 = new BitSet(new long[]{0x0000000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_RPAREN_in_annotationMethodDeclaration4235 = new BitSet(new long[]{0x0000000100000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_DEFAULT_in_annotationMethodDeclaration4238 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802834388AL,0x0000000000000001L});
    public static final BitSet FOLLOW_elementValue_in_annotationMethodDeclaration4240 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_annotationMethodDeclaration4269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_block4287 = new BitSet(new long[]{0x181D811A83870610L,0x1EDEF0D5E83C388EL,0x0000000000000007L});
    public static final BitSet FOLLOW_blockInner_in_block4289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_blockStatement_in_blockInner4305 = new BitSet(new long[]{0x181D811A83870610L,0x1EDEF0D5E83C388EL,0x0000000000000007L});
    public static final BitSet FOLLOW_RBRACE_in_blockInner4326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_blockStatementInner_in_blockStatement4354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclarationStatement_in_blockStatementInner4378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_blockStatementInner4388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statement_in_blockStatementInner4398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement4418 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_localVariableDeclarationStatement4428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_localVariableDeclaration4457 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_type_in_localVariableDeclaration4459 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_variableDeclarator_in_localVariableDeclaration4469 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_COMMA_in_localVariableDeclaration4480 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_variableDeclarator_in_localVariableDeclaration4482 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_block_in_statement4512 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSERT_in_statement4524 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_statement4544 = new BitSet(new long[]{0x0000000008000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_COLON_in_statement4547 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_statement4549 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_statement4553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSERT_in_statement4563 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_statement4566 = new BitSet(new long[]{0x0000000008000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_COLON_in_statement4569 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_statement4571 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_statement4575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_statement4585 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_parExpression_in_statement4587 = new BitSet(new long[]{0x181C801A81870600L,0x1ADEC0D02830388AL,0x0000000000000005L});
    public static final BitSet FOLLOW_statement_in_statement4589 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_ELSE_in_statement4592 = new BitSet(new long[]{0x181C801A81870600L,0x1ADEC0D02830388AL,0x0000000000000005L});
    public static final BitSet FOLLOW_statement_in_statement4594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forstatement_in_statement4606 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_statement4616 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_parExpression_in_statement4618 = new BitSet(new long[]{0x181C801A81870600L,0x1ADEC0D02830388AL,0x0000000000000005L});
    public static final BitSet FOLLOW_statement_in_statement4620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_statement4630 = new BitSet(new long[]{0x181C801A81870600L,0x1ADEC0D02830388AL,0x0000000000000005L});
    public static final BitSet FOLLOW_statement_in_statement4632 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_WHILE_in_statement4634 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_parExpression_in_statement4636 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_statement4638 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_trystatement_in_statement4648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SWITCH_in_statement4658 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_parExpression_in_statement4660 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_LBRACE_in_statement4662 = new BitSet(new long[]{0x0000000100200000L,0x0000000400000000L});
    public static final BitSet FOLLOW_switchBlockStatementGroups_in_statement4664 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_RBRACE_in_statement4666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYNCHRONIZED_in_statement4676 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_parExpression_in_statement4678 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_block_in_statement4680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURN_in_statement4690 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0C02830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_statement4693 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_statement4698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THROW_in_statement4708 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_statement4710 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_statement4712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BREAK_in_statement4722 = new BitSet(new long[]{0x0800000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_statement4737 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_statement4754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONTINUE_in_statement4764 = new BitSet(new long[]{0x0800000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_statement4779 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_statement4796 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_statement4806 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_statement4809 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_statement4819 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_COLON_in_statement4821 = new BitSet(new long[]{0x181C801A81870600L,0x1ADEC0D02830388AL,0x0000000000000005L});
    public static final BitSet FOLLOW_statement_in_statement4823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_statement4836 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups4857 = new BitSet(new long[]{0x0000000100200002L});
    public static final BitSet FOLLOW_switchLabel_in_switchBlockStatementGroup4885 = new BitSet(new long[]{0x181D811A83870612L,0x1EDEF0D1E83C388EL,0x0000000000000007L});
    public static final BitSet FOLLOW_blockStatement_in_switchBlockStatementGroup4896 = new BitSet(new long[]{0x181D811A83870612L,0x1EDEF0D1E83C388EL,0x0000000000000007L});
    public static final BitSet FOLLOW_CASE_in_switchLabel4926 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_switchLabel4928 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_COLON_in_switchLabel4930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DEFAULT_in_switchLabel4940 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_COLON_in_switchLabel4942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_trystatement4962 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_block_in_trystatement4964 = new BitSet(new long[]{0x0002000000400000L});
    public static final BitSet FOLLOW_catches_in_trystatement4978 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_FINALLY_in_trystatement4980 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_block_in_trystatement4982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_trystatement4996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FINALLY_in_trystatement5010 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_block_in_trystatement5012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catchClause_in_catches5042 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_catchClause_in_catches5053 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_CATCH_in_catchClause5083 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_LPAREN_in_catchClause5085 = new BitSet(new long[]{0x0805000800850000L,0x0000008000040802L});
    public static final BitSet FOLLOW_formalParameter_in_catchClause5087 = new BitSet(new long[]{0x0000000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_RPAREN_in_catchClause5097 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_block_in_catchClause5099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_formalParameter5118 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_type_in_formalParameter5120 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_formalParameter5122 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_LBRACKET_in_formalParameter5136 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_formalParameter5138 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_FOR_in_forstatement5183 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_LPAREN_in_forstatement5185 = new BitSet(new long[]{0x0805000800850000L,0x0000008000040802L});
    public static final BitSet FOLLOW_variableModifiers_in_forstatement5187 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_type_in_forstatement5189 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_forstatement5191 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_COLON_in_forstatement5193 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_forstatement5209 = new BitSet(new long[]{0x0000000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_RPAREN_in_forstatement5211 = new BitSet(new long[]{0x181C801A81870600L,0x1ADEC0D02830388AL,0x0000000000000005L});
    public static final BitSet FOLLOW_statement_in_forstatement5213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_forstatement5233 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_LPAREN_in_forstatement5235 = new BitSet(new long[]{0x080D801801850400L,0x0A46C0C02834380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_forInit_in_forstatement5254 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_forstatement5275 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0C02830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_forstatement5294 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_forstatement5315 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0A02830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expressionList_in_forstatement5334 = new BitSet(new long[]{0x0000000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_RPAREN_in_forstatement5355 = new BitSet(new long[]{0x181C801A81870600L,0x1ADEC0D02830388AL,0x0000000000000005L});
    public static final BitSet FOLLOW_statement_in_forstatement5357 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_forInit5376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forInit5386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_parExpression5405 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_parExpression5407 = new BitSet(new long[]{0x0000000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_RPAREN_in_parExpression5409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList5428 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_COMMA_in_expressionList5439 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_expressionList5441 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression5472 = new BitSet(new long[]{0x0100020000104082L,0x00010A0014004000L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression5483 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_expression5485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQ_in_assignmentOperator5534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSEQ_in_assignmentOperator5544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUBEQ_in_assignmentOperator5554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAREQ_in_assignmentOperator5564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SLASHEQ_in_assignmentOperator5574 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AMPEQ_in_assignmentOperator5584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BAREQ_in_assignmentOperator5594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARETEQ_in_assignmentOperator5604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PERCENTEQ_in_assignmentOperator5614 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_assignmentOperator5625 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_LT_in_assignmentOperator5627 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_EQ_in_assignmentOperator5629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_assignmentOperator5640 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_GT_in_assignmentOperator5642 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_GT_in_assignmentOperator5644 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_EQ_in_assignmentOperator5646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_assignmentOperator5657 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_GT_in_assignmentOperator5659 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_EQ_in_assignmentOperator5661 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression5681 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_QUES_in_conditionalExpression5692 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression5694 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_COLON_in_conditionalExpression5696 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalExpression5698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression5728 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_BARBAR_in_conditionalOrExpression5739 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression5741 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5771 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_AMPAMP_in_conditionalAndExpression5782 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5784 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5814 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_BAR_in_inclusiveOrExpression5825 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5827 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression5857 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_CARET_in_exclusiveOrExpression5868 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression5870 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression5900 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_AMP_in_andExpression5911 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression5913 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression5943 = new BitSet(new long[]{0x0000040000000802L});
    public static final BitSet FOLLOW_set_in_equalityExpression5967 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression6017 = new BitSet(new long[]{0x0000040000000802L});
    public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression6047 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_INSTANCEOF_in_instanceOfExpression6058 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression6060 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression6090 = new BitSet(new long[]{0x0100000000000002L,0x0000000000004000L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression6101 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression6103 = new BitSet(new long[]{0x0100000000000002L,0x0000000000004000L});
    public static final BitSet FOLLOW_LT_in_relationalOp6134 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_EQ_in_relationalOp6136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_relationalOp6147 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_EQ_in_relationalOp6149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_relationalOp6159 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_relationalOp6169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression6188 = new BitSet(new long[]{0x0100000000000002L,0x0000000000004000L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression6199 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression6201 = new BitSet(new long[]{0x0100000000000002L,0x0000000000004000L});
    public static final BitSet FOLLOW_LT_in_shiftOp6233 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_LT_in_shiftOp6235 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_shiftOp6246 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_GT_in_shiftOp6248 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_GT_in_shiftOp6250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_shiftOp6261 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_GT_in_shiftOp6263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression6283 = new BitSet(new long[]{0x0000000000000002L,0x0000800008000000L});
    public static final BitSet FOLLOW_set_in_additiveExpression6307 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression6357 = new BitSet(new long[]{0x0000000000000002L,0x0000800008000000L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression6394 = new BitSet(new long[]{0x0000000000000002L,0x0000050002000000L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression6418 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression6486 = new BitSet(new long[]{0x0000000000000002L,0x0000050002000000L});
    public static final BitSet FOLLOW_PLUS_in_unaryExpression6518 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6521 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUB_in_unaryExpression6531 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSPLUS_in_unaryExpression6543 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUBSUB_in_unaryExpression6555 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression6567 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unaryExpressionNotPlusMinus6586 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_unaryExpressionNotPlusMinus6598 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6600 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus6610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus6620 = new BitSet(new long[]{0x0000000400000002L,0x0002000020000100L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus6631 = new BitSet(new long[]{0x0000000400000002L,0x0002000020000100L});
    public static final BitSet FOLLOW_LPAREN_in_castExpression6700 = new BitSet(new long[]{0x0004000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression6702 = new BitSet(new long[]{0x0000000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_RPAREN_in_castExpression6704 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression6706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_castExpression6716 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_type_in_castExpression6718 = new BitSet(new long[]{0x0000000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_RPAREN_in_castExpression6720 = new BitSet(new long[]{0x080C801801850400L,0x0A4440800030380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression6722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary6743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_objectSelector_in_primary6753 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_superSelector_in_primary6763 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary6773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_creator_in_primary6783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary6793 = new BitSet(new long[]{0x0000000400000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LBRACKET_in_primary6804 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_primary6806 = new BitSet(new long[]{0x0000000400000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_DOT_in_primary6827 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_CLASS_in_primary6829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VOID_in_primary6839 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_DOT_in_primary6841 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_CLASS_in_primary6843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_objectSelectorInner_in_objectSelector6856 = new BitSet(new long[]{0x0000000400000002L,0x0000000000002100L});
    public static final BitSet FOLLOW_identifierSuffix_in_objectSelector6859 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THIS_in_objectSelectorInner6885 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_DOT_in_objectSelectorInner6901 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_objectSelectorInner6903 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_objectSelectorInner6927 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_DOT_in_objectSelectorInner6944 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_objectSelectorInner6946 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_SUPER_in_superSelector6976 = new BitSet(new long[]{0x0000000400000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_superSuffix_in_superSelector6978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix7012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_superSuffix7022 = new BitSet(new long[]{0x0800000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_typeArguments_in_superSuffix7025 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_superSuffix7046 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_arguments_in_superSuffix7057 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_identifierSuffix7089 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_identifierSuffix7091 = new BitSet(new long[]{0x0000000400000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix7112 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_CLASS_in_identifierSuffix7114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_identifierSuffix7125 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix7127 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_identifierSuffix7129 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix7150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix7160 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_CLASS_in_identifierSuffix7162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix7174 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_identifierSuffix7176 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_identifierSuffix7178 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix7180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix7190 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_THIS_in_identifierSuffix7192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix7202 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_SUPER_in_identifierSuffix7204 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix7206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_innerCreator_in_identifierSuffix7216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector7242 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_selector7244 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_arguments_in_selector7255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector7276 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_THIS_in_selector7278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector7288 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_SUPER_in_selector7290 = new BitSet(new long[]{0x0000000400000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_superSuffix_in_selector7300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_innerCreator_in_selector7310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_selector7320 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_selector7322 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_selector7324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_creator7343 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator7345 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_creator7347 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator7349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_creator7359 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_creator7361 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator7363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arrayCreator_in_creator7373 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_arrayCreator7398 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_createdName_in_arrayCreator7400 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7410 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7412 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000180L});
    public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7423 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7425 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000180L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreator7446 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_arrayCreator7457 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_createdName_in_arrayCreator7459 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7469 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_arrayCreator7471 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7481 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7495 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_arrayCreator7497 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7511 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7533 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7535 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer7565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer7575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_arrayInitializer7594 = new BitSet(new long[]{0x080C801811850400L,0x0A46C0842830388AL,0x0000000000000001L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer7609 = new BitSet(new long[]{0x0000000010000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer7628 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830388AL,0x0000000000000001L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer7630 = new BitSet(new long[]{0x0000000010000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer7679 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_RBRACE_in_arrayInitializer7691 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_createdName7724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName7734 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_innerCreator7753 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_NEW_in_innerCreator7755 = new BitSet(new long[]{0x0800000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_innerCreator7766 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_innerCreator7787 = new BitSet(new long[]{0x0000000000000000L,0x0000000000006000L});
    public static final BitSet FOLLOW_typeArguments_in_innerCreator7803 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator7824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest7844 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_classBody_in_classCreatorRest7855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_nonWildcardTypeArguments7886 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments7888 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_GT_in_nonWildcardTypeArguments7898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_argumentsInner_in_arguments7911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_argumentsInner7935 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0A02830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expressionList_in_argumentsInner7938 = new BitSet(new long[]{0x0000000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_RPAREN_in_argumentsInner7951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_navigableIdentifier7965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_classHeader8093 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_CLASS_in_classHeader8095 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_classHeader8097 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_enumHeader8116 = new BitSet(new long[]{0x0800010000000000L});
    public static final BitSet FOLLOW_set_in_enumHeader8118 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_enumHeader8124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_interfaceHeader8143 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_INTERFACE_in_interfaceHeader8145 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_interfaceHeader8147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_annotationHeader8166 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_MONKEYS_AT_in_annotationHeader8168 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_INTERFACE_in_annotationHeader8170 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_annotationHeader8172 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_typeHeader8191 = new BitSet(new long[]{0x0000010002000000L,0x0000000000040004L});
    public static final BitSet FOLLOW_CLASS_in_typeHeader8194 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_ENUM_in_typeHeader8196 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_MONKEYS_AT_in_typeHeader8199 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_INTERFACE_in_typeHeader8203 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_typeHeader8207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_methodHeader8226 = new BitSet(new long[]{0x0804000800850000L,0x0000008000004802L,0x0000000000000001L});
    public static final BitSet FOLLOW_typeParameters_in_methodHeader8228 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L,0x0000000000000001L});
    public static final BitSet FOLLOW_type_in_methodHeader8232 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_VOID_in_methodHeader8234 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodHeader8238 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_LPAREN_in_methodHeader8240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_fieldHeader8259 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_type_in_fieldHeader8261 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_fieldHeader8263 = new BitSet(new long[]{0x0000020010000000L,0x0000004000000100L});
    public static final BitSet FOLLOW_LBRACKET_in_fieldHeader8266 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_fieldHeader8267 = new BitSet(new long[]{0x0000020010000000L,0x0000004000000100L});
    public static final BitSet FOLLOW_set_in_fieldHeader8271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_localVariableHeader8296 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_type_in_localVariableHeader8298 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_localVariableHeader8300 = new BitSet(new long[]{0x0000020010000000L,0x0000004000000100L});
    public static final BitSet FOLLOW_LBRACKET_in_localVariableHeader8303 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_localVariableHeader8304 = new BitSet(new long[]{0x0000020010000000L,0x0000004000000100L});
    public static final BitSet FOLLOW_set_in_localVariableHeader8308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotations_in_synpred2_Java98 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_packageDeclaration_in_synpred2_Java127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_synpred12_Java499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_synpred27_Java722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumConstants_in_synpred35_Java1209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumBodyDeclarations_in_synpred36_Java1231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_synpred42_Java1462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldDeclaration_in_synpred51_Java1777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodDeclaration_in_synpred52_Java1788 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_synpred53_Java1799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_synpred56_Java1952 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_synpred58_Java1865 = new BitSet(new long[]{0x0800000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_typeParameters_in_synpred58_Java1876 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_synpred58_Java1897 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_formalParameters_in_synpred58_Java1907 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000080L});
    public static final BitSet FOLLOW_THROWS_in_synpred58_Java1918 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_qualifiedNameList_in_synpred58_Java1920 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_LBRACE_in_synpred58_Java1941 = new BitSet(new long[]{0x181D811A83870610L,0x1EDEF0D5E83C788EL,0x0000000000000007L});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_synpred58_Java1952 = new BitSet(new long[]{0x181D811A83870610L,0x1EDEF0D5E83C388EL,0x0000000000000007L});
    public static final BitSet FOLLOW_blockStatement_in_synpred58_Java1974 = new BitSet(new long[]{0x181D811A83870610L,0x1EDEF0D5E83C388EL,0x0000000000000007L});
    public static final BitSet FOLLOW_RBRACE_in_synpred58_Java1995 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceFieldDeclaration_in_synpred68_Java2388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodDeclaration_in_synpred69_Java2398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_synpred70_Java2408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_synpred71_Java2418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ellipsisParameterDecl_in_synpred96_Java3212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalParameterDecl_in_synpred98_Java3222 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_COMMA_in_synpred98_Java3233 = new BitSet(new long[]{0x0805000800850000L,0x0000008000040802L});
    public static final BitSet FOLLOW_normalParameterDecl_in_synpred98_Java3235 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_normalParameterDecl_in_synpred99_Java3257 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COMMA_in_synpred99_Java3267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_synpred100_Java3307 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred104_Java3497 = new BitSet(new long[]{0x0000000000000000L,0x0044000000000000L});
    public static final BitSet FOLLOW_set_in_synpred104_Java3523 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_arguments_in_synpred104_Java3555 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_synpred104_Java3557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationMethodDeclaration_in_synpred118_Java4140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceFieldDeclaration_in_synpred119_Java4150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_synpred120_Java4160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_synpred121_Java4170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_synpred122_Java4180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_synpred123_Java4190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclarationStatement_in_synpred126_Java4378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred127_Java4388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSERT_in_synpred131_Java4524 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_synpred131_Java4544 = new BitSet(new long[]{0x0000000008000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_COLON_in_synpred131_Java4547 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_synpred131_Java4549 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_synpred131_Java4553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSERT_in_synpred133_Java4563 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_synpred133_Java4566 = new BitSet(new long[]{0x0000000008000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_COLON_in_synpred133_Java4569 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_synpred133_Java4571 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_synpred133_Java4575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred134_Java4592 = new BitSet(new long[]{0x181C801A81870600L,0x1ADEC0D02830388AL,0x0000000000000005L});
    public static final BitSet FOLLOW_statement_in_synpred134_Java4594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_synpred149_Java4806 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_SEMI_in_synpred149_Java4809 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_synpred150_Java4819 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_COLON_in_synpred150_Java4821 = new BitSet(new long[]{0x181C801A81870600L,0x1ADEC0D02830388AL,0x0000000000000005L});
    public static final BitSet FOLLOW_statement_in_synpred150_Java4823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred154_Java4978 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_FINALLY_in_synpred154_Java4980 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_block_in_synpred154_Java4982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred155_Java4996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_synpred158_Java5183 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_LPAREN_in_synpred158_Java5185 = new BitSet(new long[]{0x0805000800850000L,0x0000008000040802L});
    public static final BitSet FOLLOW_variableModifiers_in_synpred158_Java5187 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_type_in_synpred158_Java5189 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_synpred158_Java5191 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_COLON_in_synpred158_Java5193 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_synpred158_Java5209 = new BitSet(new long[]{0x0000000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_RPAREN_in_synpred158_Java5211 = new BitSet(new long[]{0x181C801A81870600L,0x1ADEC0D02830388AL,0x0000000000000005L});
    public static final BitSet FOLLOW_statement_in_synpred158_Java5213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_synpred162_Java5376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred203_Java6610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_synpred207_Java6700 = new BitSet(new long[]{0x0004000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_primitiveType_in_synpred207_Java6702 = new BitSet(new long[]{0x0000000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_RPAREN_in_synpred207_Java6704 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_unaryExpression_in_synpred207_Java6706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred215_Java6859 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred216_Java6901 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_synpred216_Java6903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred218_Java6944 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_synpred218_Java6946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_synpred224_Java7125 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_synpred224_Java7127 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_synpred224_Java7129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_synpred236_Java7343 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred236_Java7345 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_synpred236_Java7347 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_classCreatorRest_in_synpred236_Java7349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_synpred237_Java7359 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_synpred237_Java7361 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_classCreatorRest_in_synpred237_Java7363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_synpred239_Java7398 = new BitSet(new long[]{0x0804000800850000L,0x0000008000000802L});
    public static final BitSet FOLLOW_createdName_in_synpred239_Java7400 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LBRACKET_in_synpred239_Java7410 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_synpred239_Java7412 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000180L});
    public static final BitSet FOLLOW_LBRACKET_in_synpred239_Java7423 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_synpred239_Java7425 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000180L});
    public static final BitSet FOLLOW_arrayInitializer_in_synpred239_Java7446 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_synpred240_Java7495 = new BitSet(new long[]{0x080C801801850400L,0x0A46C0802830380AL,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_in_synpred240_Java7497 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACKET_in_synpred240_Java7511 = new BitSet(new long[]{0x0000000000000002L});

}