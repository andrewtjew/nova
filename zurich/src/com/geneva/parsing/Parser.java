package com.geneva.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.geneva.debug.Debug;
import com.geneva.debug.Output;
import com.geneva.lexing3.GenevaLexer;
import com.geneva.lexing3.Lexeme;
import com.geneva.lexing3.LexerResult;
import com.geneva.lexing3.Token;
import com.geneva.parsing.Node.NodeType;
import com.geneva.parsing.member.ClassMember;
import com.geneva.parsing.member.EnumMember;
import com.geneva.parsing.member.FieldMember;
import com.geneva.parsing.member.FunctionMember;
import com.geneva.parsing.member.Member;
import com.geneva.parsing.member.NamespaceMember;
import com.geneva.parsing.member.UsingMember;
import com.geneva.parsing.statement.BreakStatement;
import com.geneva.parsing.statement.CatchStatement;
import com.geneva.parsing.statement.ContinueStatement;
import com.geneva.parsing.statement.ElseIfStatement;
import com.geneva.parsing.statement.ElseStatement;
import com.geneva.parsing.statement.ExpressionStatement;
import com.geneva.parsing.statement.FinallyStatement;
import com.geneva.parsing.statement.GotoStatement;
import com.geneva.parsing.statement.IfStatement;
import com.geneva.parsing.statement.LabelStatement;
import com.geneva.parsing.statement.ReturnStatement;
import com.geneva.parsing.statement.Statement;
import com.geneva.parsing.statement.TryStatement;
import com.geneva.parsing.statement.UsingStatement;
import com.geneva.parsing.statement.WhileStatement;
import com.geneva.parsing.type.AlternativeType;
import com.geneva.parsing.type.FunctionType;
import com.geneva.parsing.type.RecordType;
import com.geneva.parsing.type.SimpleType;
import com.geneva.parsing.type.Type;
import com.geneva.util.ParseException;
import com.geneva.util.Source;

public class Parser
{
	final private HashMap<String,Integer> precedenceLevels;
	private final int lexemeCount;
	private final Lexeme[] lexemes;
	private int lexemeIndex;
	private int[] marks=new int[1000];
	private int markIndex=0;
	private Source source;

	static public Parser newParser(Source source)
	{
		LexerResult result=GenevaLexer.lex(source,source.getText().length());
		return new Parser(source,result.getLexemes(),result.getCount());
	}	
	
	public Parser(Source source,Lexeme[] lexemes,int lexemeCount)
	{
		this.source=source;
		this.lexemeCount=lexemeCount;
		this.lexemes=lexemes;

		//Initialize precedence levels
		this.precedenceLevels=new HashMap<>();
		
		int level=0;
		
		this.precedenceLevels.put(",", level);
		level++;
		
//		this.precedenceLevels.put("=", level);
//		level++;

		this.precedenceLevels.put("=>", level);
		level++;
		
		this.precedenceLevels.put("?", level);
		this.precedenceLevels.put(":", level);
		level++;

		this.precedenceLevels.put("||", level);
		level++;

		this.precedenceLevels.put("&&", level);
		level++;

		this.precedenceLevels.put("|", level);
		level++;

		this.precedenceLevels.put("^", level);
		level++;

		this.precedenceLevels.put("&", level);
		level++;
		
		this.precedenceLevels.put("==", level);
		this.precedenceLevels.put("!=", level);
		level++;

		this.precedenceLevels.put("<", level);
		this.precedenceLevels.put(">", level);
		this.precedenceLevels.put("<=", level);
		this.precedenceLevels.put(">=", level);
		level++;

		this.precedenceLevels.put("<<", level);
		this.precedenceLevels.put(">>", level);
		level++;

		this.precedenceLevels.put("+", level);
		this.precedenceLevels.put("-", level);
		level++;

		this.precedenceLevels.put("*", level);
		this.precedenceLevels.put("/", level);
		this.precedenceLevels.put("%", level);
		level++;

		this.precedenceLevels.put("!", level);
		level++;
		
		this.precedenceLevels.put(".", level);
		this.precedenceLevels.put("new", level);
		this.precedenceLevels.put("typeof", level);
//		this.precedenceLevels.put("mutable", level);
//		this.precedenceLevels.put("public", level);
		level++;

		//Unused. Just to make some room.
		this.precedenceLevels.put("", level); 
	}
	private int getPrecedenceLevel(String operator,int level)
	{
		return this.precedenceLevels.get(operator)+level*this.precedenceLevels.size();
	}

	private int getMaximumNormalPrecedenceLevel()
	{
		return this.precedenceLevels.size()-1;
	}

	private void mark()
	{
		marks[markIndex++]=lexemeIndex;
	}
	private void revert()
	{
		this.lexemeIndex=marks[--markIndex];
	}
	private void consume()
	{
		markIndex--;
	}
	
	private Lexeme next()
	{
		if (lexemeIndex>=lexemeCount)
		{
			if (Debug.DEBUG)
			{
				if (Debug.printNext)
				{
					System.out.println("Next:"+Token.END);
				}
			}
			return new Lexeme(Token.END,null,0,0,null);
		}
		Lexeme lexeme=this.lexemes[lexemeIndex++];
		if (Debug.DEBUG)
		{
			if (Debug.printNext)
			{
				System.out.println("Next:"+lexeme.getToken()+"="+lexeme.getValue());
			}
		}
		return lexeme;
	}
	private void back()
	{
		if (Debug.DEBUG)
		{
			if (Debug.printNext)
			{
				System.out.println("BackOne");
			}
		}
		lexemeIndex--;
	}
	private void back(int amount)
	{
		if (Debug.DEBUG)
		{
			if (Debug.printNext)
			{
				System.out.println("Back:"+amount);
			}
		}
		lexemeIndex-=amount;
	}
	private Lexeme nextToken(Token token) throws ParseException
	{
		Lexeme lexeme=next();
		if (lexeme.isToken(token)==false)
		{
			throw new ParseException(lexeme);
		}
		return lexeme;
	}
	
	private void nextTerminator() throws ParseException
	{
		nextToken(Token.TERMINATOR);
	}
	private Lexeme nextIdentifier() throws ParseException
	{
		Lexeme lexeme=next();
		if (lexeme.isToken(Token.IDENTIFIER)==false)
		{
			throw new ParseException(lexeme);
		}
		return lexeme;
	}
	private Lexeme nextOperator(String operator) throws ParseException
	{
		Lexeme lexeme=next();
		if (lexeme.isToken(Token.OPERATOR)==false)
		{
			throw new ParseException(lexeme);
		}
		if (lexeme.getValue().equals(operator)==false)
		{
			throw new ParseException(lexeme);
		}
		return lexeme;
	}
	private Lexeme current()
	{
		return this.lexemes[lexemeIndex];
	}
	private int getLexemeCount()
	{
		return this.lexemeCount;
	}
	private int getLexemeIndex()
	{
		return this.lexemeIndex;
	}
	
	public Node debugParseExpression() throws ParseException
	{
		return parseExpression();
	}

	public Unit parse() throws ParseException
	{
		return new Unit(parseMembers(Token.END));
	}

	private List<MutableNameType> parseMutableNameTypes() throws ParseException
	{
		ArrayList<MutableNameType> mutableNameTypes=new ArrayList<MutableNameType>();
		boolean needComma=false;
		for (Lexeme lexeme=next();lexeme.isToken(Token.CLOSE_ROUND)==false;lexeme=next())
		{
			if (needComma)
			{
				if (lexeme.isOperator(",")==false)
				{
					throw new ParseException(lexeme);
				}
				needComma=false;
				continue;
			}
			needComma=true;
			back();
			mutableNameTypes.add(parseMutableNameType());
		}
		return mutableNameTypes;
	}

	private List<NameTypeExpression> parseAttributes() throws ParseException
	{
		ArrayList<NameTypeExpression> attributes=new ArrayList<NameTypeExpression>();
		boolean needComma=false;
		for (Lexeme lexeme=next();lexeme.isToken(Token.CLOSE_SQUARE)==false;lexeme=next())
		{
			if (needComma)
			{
				if (lexeme.isOperator(",")==false)
				{
					throw new ParseException(lexeme);
				}
				needComma=false;
				continue;
			}
			needComma=true;
			back();
			attributes.add(parseNameTypeExpression());
		}
		return attributes;
	}

	private List<Member> parseMembers(Token stopToken) throws ParseException
	{
		ArrayList<Member> members=new ArrayList<Member>();
		for (Lexeme lexeme=next();lexeme.isToken(stopToken)==false;lexeme=next())
		{
			List<NameTypeExpression> attributes=null;
			if (lexeme.isToken(Token.OPEN_SQUARE))
			{
				//attributes
				attributes=parseAttributes();
				lexeme=next();
			}
			
			Lexeme public_=null;
			for (;;lexeme=next())
			{
				//modifiers
				
    			if (lexeme.isIdentifier("public"))
    			{
        			if (public_!=null)
        			{
                		throw new ParseException(lexeme);
        			}
        			public_=lexeme;
    			}
    			else
    			{
    				break;
    			}
			}
			if (lexeme.isToken(Token.IDENTIFIER))
			{
    			if (lexeme.isIdentifier("class"))
    			{
    				members.add(parseClassMember(public_!=null));
    			}
    			else if (lexeme.isIdentifier("enum"))
    			{
    				members.add(parseEnumMember(public_!=null));
    			}
    			else if (lexeme.isIdentifier("using"))
    			{
        			if (public_!=null)
        			{
                		throw new ParseException(public_);
        			}
    				members.add(parseUsingMember());
    			}
    			else if (lexeme.isIdentifier("namespace"))
    			{
        			if (public_!=null)
        			{
                		throw new ParseException(public_);
        			}
    				members.add(parseNamespaceMember());
    			}
    			else
    			{
    				lexeme=next();
    				if (lexeme.isToken(Token.OPEN_ROUND))
    				{
    					back(2);
        				members.add(parseFunctionMember(attributes,public_!=null));
    				}
    				else
    				{
    					back(2);
        				members.add(parseFieldMember(attributes,new Modifiers(public_!=null)));
    				}
    			}
			}
			/*
			else if (lexeme.isToken(Token.OPERATOR))
			{
				System.out.println("***");
			}
			*/
			else
			{
				throw new ParseException(lexeme);
			}
			
		}		
		return members;
	}

	private EnumMember parseEnumMember(boolean public_) throws ParseException
	{
		Lexeme name=nextIdentifier();
		Lexeme lexeme=next();
		Node type=null;
		if (lexeme.isOperator(":"))
		{
			type=parseExpression();
		}
		else
		{
			back();
		}
		nextToken(Token.OPEN_CURLY);
		EnumMember enumMember=new EnumMember(public_,name,type,parseExpression());
		next();
		return enumMember;
	}

	private ClassMember parseClassMember(boolean public_) throws ParseException
	{
		Lexeme name=nextIdentifier();
		Lexeme lexeme=next();
		Namespace superClass=null;
		if (lexeme.isIdentifier("extends"))
		{
			superClass=parseNamespace();
		}
		else
		{
			back();
		}
		nextToken(Token.OPEN_CURLY);
		ClassMember classMember=new ClassMember(public_,name,superClass,parseMembers(Token.CLOSE_CURLY));
		return classMember;
	}
	
	private Namespace parseNamespace() throws ParseException
	{
		Lexeme lexeme=next();
		if (lexeme.isToken(Token.IDENTIFIER)==false)
		{
			throw new ParseException(lexeme);
		}
		ArrayList<Lexeme> lexemes=new ArrayList<Lexeme>();
		StringBuilder sb=new StringBuilder();
		sb.append(lexeme.getValue());
		lexemes.add(lexeme);
		lexeme=next();
		for (;lexeme.isOperator(".");lexeme=next())
		{
			lexeme=nextIdentifier();
			lexemes.add(lexeme);
			sb.append(".");
			sb.append(lexeme.getValue());
		}
		back();
		return new Namespace(sb.toString(),lexemes);
		
	}
	
	private NamespaceMember parseNamespaceMember() throws ParseException
	{
		Namespace namespace=parseNamespace();
		nextToken(Token.OPEN_CURLY);
		NamespaceMember namespaceMember=new NamespaceMember(namespace,parseMembers(Token.CLOSE_CURLY));
		return namespaceMember;
	}
	
	private UsingMember parseUsingMember() throws ParseException
	{
		Lexeme alias=next();
		Lexeme lexeme=next();
		if (lexeme.isToken(Token.ASSIGNMENT)==false)
		{
			back(2);
			alias=null;
		}
		Namespace namespace=parseNamespace();
		nextTerminator();
		return new UsingMember(alias, namespace);
	}

	private NameTypeExpression parseNameTypeExpression() throws ParseException
	{
		NameType nameType=parseNameType();
		Node expression=null;
		Lexeme lexeme=next();
		if (lexeme.isToken(Token.ASSIGNMENT))
		{
			expression=parseExpression();
		}
		return new NameTypeExpression(nameType,expression);
	}
	private MutableNameTypeExpression parseMutableNameTypeExpression() throws ParseException
	{
		MutableNameType nameType=parseMutableNameType();
		Node expression=null;
		Lexeme lexeme=next();
		if (lexeme.isToken(Token.ASSIGNMENT))
		{
			expression=parseExpression();
		}
		else
		{
			back();
		}
		return new MutableNameTypeExpression(nameType,expression);
	}
	
	private FieldMember parseFieldMember(List<NameTypeExpression> attributes,Modifiers modifiers) throws ParseException
	{
		MutableNameTypeExpression expression=parseMutableNameTypeExpression();
		nextTerminator();
		return new FieldMember(attributes,modifiers,expression);
	}

	private NameType parseNameType() throws ParseException
	{
		Lexeme name=nextIdentifier();
		Type type=null;
		Lexeme lexeme=next();
		if (lexeme.isOperator(":"))
		{
			type=parseType();
		}
		else
		{
			back();
		}
		return new NameType(name,type);
	}

	private MutableNameType parseMutableNameType() throws ParseException
	{
		boolean mutable=false;
		Lexeme lexeme=next();
		if (lexeme.isIdentifier("mutable"))
		{
			mutable=true;
		}
		else
		{
			back();
		}
		Lexeme name=nextIdentifier();
		Type type=null;
		lexeme=next();
		if (lexeme.isOperator(":"))
		{
			type=parseType();
		}
		else
		{
			back();
		}
		return new MutableNameType(mutable,name,type);
	}
	
	private RecordType parseRecordType() throws ParseException
	{
		ArrayList<NameType> nameTypes=new ArrayList<NameType>();
		boolean needComma=false;
		for (Lexeme lexeme=next();lexeme.isToken(Token.CLOSE_CURLY)==false;lexeme=next())
		{
			if (needComma)
			{
				if (lexeme.isOperator(",")==false)
				{
					throw new ParseException(lexeme);
				}
				needComma=false;
				continue;
			}
			needComma=true;
			back();
			nameTypes.add(parseNameType());
		}
		return new RecordType(nameTypes);
	}
	
	private Type parseSimpleType() throws ParseException
	{
		Namespace namespace=parseNamespace();
		return new SimpleType(namespace);
	}
	
	private FunctionType parseFunctionType() throws ParseException
	{
		boolean needComma=false;
		ArrayList<MutableNameType> inputTypes=new ArrayList<MutableNameType>();
		Type outputType=null;
		for (Lexeme lexeme=next();lexeme.isToken(Token.CLOSE_ROUND)==false;lexeme=next())
		{
			if (needComma)
			{
				if (lexeme.isOperator(",")==false)
				{
					throw new ParseException(lexeme);
				}
				needComma=false;
				continue;
			}
			needComma=true;
			back();
			inputTypes.add(parseMutableNameType());
		}
		Lexeme lexeme=next();
		if (lexeme.isOperator("=>"))
		{
			outputType=parseType();
		}
		else
		{
			back();
		}
		return new FunctionType(inputTypes,outputType);
	}
	
	private Type parseType() throws ParseException
	{
		Lexeme lexeme=next();
		Type type=null;
		if (lexeme.isToken(Token.OPEN_CURLY))
		{
			type=parseRecordType();
		}
		else if (lexeme.isToken(Token.OPEN_ROUND))
		{
			type=parseFunctionType();
		}
		else
		{
			back();
			type=parseSimpleType();
		}
		lexeme=next();
		if (lexeme.isOperator("|")==false)
		{
			back();
			return type;
		}
		ArrayList<Type> alternatives=new ArrayList<Type>();
		alternatives.add(type);
		alternatives.add(parseType());
		for (lexeme=next();lexeme.isOperator("|")==true;lexeme=next())
		{
			alternatives.add(parseType());
		}
		back();
		return new AlternativeType(alternatives);
	}

	private List<MutableNameType> parseInputParameters() throws ParseException
	{
		nextToken(Token.OPEN_ROUND);
		ArrayList<MutableNameType> parameters=new ArrayList<MutableNameType>();
		boolean expectComma=false;
		for (Lexeme lexeme=next();lexeme.isToken(Token.CLOSE_ROUND)==false;lexeme=next())
		{
			if (expectComma)
			{
				if (lexeme.isOperator(",")==false)
    			{
    				throw new ParseException(lexeme);
    			}
    			expectComma=false;
    			continue;
			}
			expectComma=true;
			back();
			parameters.add(parseMutableNameType());
		}
		return parameters;
	}
	private List<NameType> parseOutputParameters() throws ParseException
	{
		nextToken(Token.OPEN_ROUND);
		ArrayList<NameType> parameters=new ArrayList<NameType>();
		boolean expectComma=false;
		for (Lexeme lexeme=next();lexeme.isToken(Token.CLOSE_ROUND)==false;lexeme=next())
		{
			if (expectComma)
			{
				if (lexeme.isOperator(",")==false)
    			{
    				throw new ParseException(lexeme);
    			}
    			expectComma=false;
    			continue;
 			}
			expectComma=true;
			back();
			parameters.add(parseNameType());
		}
		return parameters;
	}	
	
	private FunctionMember parseFunctionMember(List<NameTypeExpression> attributes,boolean public_) throws ParseException
	{
		Lexeme name=nextIdentifier();
		List<MutableNameType> inputParameters=parseInputParameters();
//		List<NameType> outputParameters=null;
		Type outputType=null;
		Lexeme lexeme=next();
		if (lexeme.isOperator("=>"))
		{
			outputType=parseType();
			//
//			outputParameters=parseOutputParameters();
//			nextToken(Token.CLOSE_ROUND);
			lexeme=next();
		}
		List<Statement> statements=null;
		if (lexeme.isToken(Token.TERMINATOR)==false)
		{
			back();
			statements=parseStatements();
		}
		return new FunctionMember(attributes, new Modifiers(public_), name, inputParameters,outputType, statements);
	}
	
	private List<Statement> parseStatements() throws ParseException
	{
		ArrayList<Statement> statements=new ArrayList<Statement>();
		Lexeme lexeme=next();
		if (lexeme.isToken(Token.OPEN_CURLY)==false)
		{
			back();
			statements.add(parseExpressionStatement());
			return statements;
		}
		for (lexeme=next();lexeme.isToken(Token.END)==false;lexeme=next())
		{
			if (lexeme.isToken(Token.CLOSE_CURLY))
			{
				return statements;
			}
			else if (lexeme.isIdentifier("if"))
			{
				nextToken(Token.OPEN_ROUND);
				Node expression=parseExpression();
				nextToken(Token.CLOSE_ROUND);
				statements.add(new IfStatement(expression, parseStatements()));
			}
			else if (lexeme.isIdentifier("else"))
			{
				lexeme=next();
				if (lexeme.isIdentifier("if"))
				{
    				nextToken(Token.OPEN_ROUND);
    				Node expression=parseExpression();
    				nextToken(Token.CLOSE_ROUND);
    				statements.add(new ElseIfStatement(expression, parseStatements()));
				}
				else
				{
					back();
    				statements.add(new ElseStatement(parseStatements()));
				}
			}
			else if (lexeme.isIdentifier("while"))
			{
				nextToken(Token.OPEN_ROUND);
				Node expression=parseExpression();
				nextToken(Token.CLOSE_ROUND);
				statements.add(new WhileStatement(expression, parseStatements()));
			}
			else if (lexeme.isIdentifier("label"))
			{
				statements.add(new LabelStatement(nextIdentifier()));
				nextTerminator();
			}
			else if (lexeme.isIdentifier("goto"))
			{
				statements.add(new GotoStatement(nextIdentifier()));
				nextTerminator();
			}
			else if (lexeme.isIdentifier("break"))
			{
				statements.add(new BreakStatement());
				nextTerminator();
			}
			else if (lexeme.isIdentifier("continue"))
			{
				statements.add(new ContinueStatement());
				nextTerminator();
			}
			else if (lexeme.isIdentifier("using"))
			{
				statements.add(parseUsingStatement());
			}
			else if (lexeme.isIdentifier("return"))
			{
				Node expression=parseExpression();
				nextTerminator();
				statements.add(new ReturnStatement(expression));
			}
			else if (lexeme.isIdentifier("try"))
			{
				statements.add(new TryStatement(parseStatements()));
			}
			else if (lexeme.isIdentifier("catch"))
			{
				lexeme=next();
				if (lexeme.isToken(Token.OPEN_ROUND))
				{
    				List<MutableNameType> mutableNameTypes=parseMutableNameTypes();
    				if (mutableNameTypes.size()==0)
    				{
    					throw new ParseException(lexeme);
    				}
    				statements.add(new CatchStatement(mutableNameTypes,parseStatements()));
				}
				else
				{
    				statements.add(new CatchStatement(null,parseStatements()));
				}
			}
			else if (lexeme.isIdentifier("finally"))
			{
				statements.add(new FinallyStatement(parseStatements()));
			}
			else if (lexeme.isToken(Token.TERMINATOR))
			{
				continue;
			}
			else
			{
				back();
				statements.add(parseExpressionStatement());
			}
		}
		throw new ParseException(current());
	}

	private UsingStatement parseUsingStatement() throws ParseException
	{
		nextToken(Token.OPEN_ROUND);
		ArrayList<NameTypeExpression> objects=new ArrayList<NameTypeExpression>();
		boolean needComma=false;
		for (Lexeme lexeme=next();lexeme.isToken(Token.CLOSE_ROUND)==false;lexeme=next())
		{
			if (needComma)
			{
				if (lexeme.isOperator(",")==false)
				{
					throw new ParseException(lexeme);
				}
				needComma=false;
				continue;
			}
			needComma=true;
			back();
			objects.add(parseNameTypeExpression());
		}
		return new UsingStatement(objects,parseStatements());
		
	}
	
	private ExpressionStatement parseExpressionStatement() throws ParseException
	{
		MutableNameTypeExpression expression=parseMutableNameTypeExpression();
		nextTerminator();
		return new ExpressionStatement(expression);
	}
	
	
	private Node parseExpression() throws ParseException
	{
		if (Debug.DEBUG)
		{
			if (Debug.printActiveSubExpression)
			{
				System.out.println(source.getText());
			}
		}
		int level=0;
		int squareLevel=0;
		
		ArrayList<Element> elements=new ArrayList<Element>(128);
		for (Lexeme lexeme=next();lexeme.isToken(Token.END)==false;lexeme=next())
		{
			if (Debug.DEBUG)
			{
				if (Debug.printParsePrecedence)
				{
					Output.print(lexeme);
				}
			}
			switch (lexeme.getToken())
			{
			/*
			case COMMA:
				if (level!=0)
				{
					elements.add(new Element(lexeme));
				}
				else
				{
					back();
					return parseExpression(0,elements.size(),elements);
				}
				break;
			 	*/
			case OPERATOR:
			case IDENTIFIER:
			case CHARACTER:
			case NUMBER:
			case STRING:
				elements.add(new Element(lexeme));
				break;

			case OPEN_SQUARE:
				squareLevel++;
				elements.add(new Element(lexeme));
				break;
				
			case CLOSE_SQUARE:
				squareLevel--;
				if (squareLevel<0)
				{
					if (level==0)
					{
    					back();
    					return parseExpression(0,elements.size(),elements);
					}
					throw new ParseException(lexeme);
				}
				elements.add(new Element(lexeme));
				break;

			case OPEN_ROUND:
				level++;
				elements.add(new Element(lexeme));
				break;
				
			case CLOSE_ROUND:
				level--;
				if (level<0)
				{
					if (squareLevel==0)
					{
    					back();
    					return parseExpression(0,elements.size(),elements);
					}
					throw new ParseException(lexeme);
				}
				elements.add(new Element(lexeme));
				break;
			
			case TERMINATOR:
				if (level!=0)
				{
					throw new ParseException(current());
				}
				back();
				return parseExpression(0,elements.size(),elements);

			case OPEN_CURLY:
				back();
				elements.add(new Element(parseStatements()));
				break;
				
				
			case CLOSE_CURLY:
				if (level!=0)
				{
					throw new ParseException(current());
				}
				if (squareLevel!=0)
				{
					throw new ParseException(current());
				}
				back();
				return parseExpression(0,elements.size(),elements);

			case ASSIGNMENT:
			case ERROR:
			case END:
				throw new ParseException(current());
			}
		}
		throw new ParseException(current());
	}

	private Node parseExpression(int start,int end,List<Element> elements) throws ParseException
	{
		if (end-start<=1)
		{
			// zero or one lexemes left
			if (start==end)
			{
				return null;
			}
			
			//one
			Element startElement=elements.get(start);
			if (startElement.getStatements()!=null)
			{
				return new Node(NodeType.STATEMENT_BLOCK,startElement);
			}
			return new Node(NodeType.TERMINAL,startElement);
		}
		if (Debug.DEBUG)
		{
			if (Debug.printActiveSubExpression)
			{
    			Lexeme first=elements.get(start).getLexeme();
    			for (int i=0;i<first.getStart();i++)
    			{
    				System.out.print(" ");
    			}
    			Lexeme last=elements.get(end-1).getLexeme();
    			for (int i=first.getStart();i<=last.getStart();i++)
    			{
    				System.out.print("^");
    			}
    			System.out.println();
    			for (int i=start;i<end;i++)
    			{
    				System.out.print(elements.get(i).getLexeme().getValue());
    				
    			}
    			System.out.println();
			}
		}

		int lowestPrecedence=Integer.MAX_VALUE;
		int lowestPrecedenceIndex=start;
		int level=0;

		for (int i=end-1;i>=start;i--)
		{
			Element element=elements.get(i);
			if (element.getStatements()!=null)
			{
				continue;
			}
			Lexeme lexeme=element.getLexeme();
			Token token=lexeme.getToken();
			if (Debug.DEBUG)
			{
				if (Debug.printParseExpression)
				{
					System.out.print("start="+start+", end="+end+", index="+i+":");
					Output.print(lexeme);
				}
			}
			
			
			switch (token)
			{
			case IDENTIFIER:
			case CHARACTER:
			case NUMBER:
			case STRING:
				break;

			case OPERATOR:
				//We insist that left and right of . operator must be text
				if (lexeme.isOperator("."))
				{
					if (i==end-1)
					{
						throw new ParseException(lexeme);
					}
					Lexeme next=elements.get(i+1).getLexeme();
					if (next==null)
					{
						throw new ParseException(lexeme);
					}
					
					if (next.isToken(Token.IDENTIFIER)==false)
					{
						throw new ParseException(lexeme);
					}
					if (i==start)
					{
						throw new ParseException(lexeme);
					}
					Lexeme previous=elements.get(i-1).getLexeme();
					if (previous==null)
					{
						throw new ParseException(lexeme);
					}
					if (previous.isToken(Token.IDENTIFIER)==false)
					{
						throw new ParseException(lexeme);
					}
				}
					
				while (i>start)
				{
					//To handle unary operators
					//We want to find left most operator as unary operators associate strictly right to left
					Element before=elements.get(i-1);
					if (before.isToken(Token.OPERATOR))
					{
						element=before;
						i--;
					}
					else
					{
						break;
					}
				}
				int precedence=getPrecedenceLevel(lexeme.getValue(),level);
				if (precedence<lowestPrecedence)
				{
					lowestPrecedence=precedence;
					lowestPrecedenceIndex=i;
				}
				break;

			case CLOSE_ROUND:
				level++;
				break;
			case OPEN_ROUND:
				if (level==0)
				{
					throw new ParseException(lexeme);
				}
				level--;
				break;
			case CLOSE_SQUARE:
				level++;
				break;
			case OPEN_SQUARE:
				if (level==0)
				{
					throw new ParseException(lexeme);
				}
				level--;
				break;
			
			case CLOSE_CURLY:
			case OPEN_CURLY:
			case ERROR:
			case TERMINATOR:
			case ASSIGNMENT:
				throw new ParseException(lexeme);
			}
		}
		Element startElement=elements.get(start);
		if (lowestPrecedence>getMaximumNormalPrecedenceLevel())
		{
    		if (startElement.isToken(Token.OPEN_ROUND))
    		{
    			int l=0;
    			for (int i=start+1;i<end;i++)
    			{
    				 Lexeme lexeme=elements.get(i).getLexeme();
    				 if (lexeme!=null)
    				 {
    					 if (lexeme.isToken(Token.OPEN_ROUND))
    					 {
    						 l++;
    					 }
    					 else if (lexeme.isToken(Token.CLOSE_ROUND))
    					 {
    						 if (l==0)
    						 {
    							 if (i==end-1)
    							 {
    								 return parseExpression(start+1,end-1,elements);
    							 }
    	    	        		return new Node(NodeType.PARAMETERS,null,parseExpression(start+1,i,elements),parseExpression(i+1, end,elements));
    						 }
    						 l--;
    					 }
    				 }
    			}
    			throw new ParseException(startElement);
    		}
    		else if (startElement.isToken(Token.OPEN_SQUARE))
    		{
    			int l=0;
    			for (int i=start+1;i<end;i++)
    			{
    				 Lexeme lexeme=elements.get(i).getLexeme();
    				 if (lexeme!=null)
    				 {
    					 if (lexeme.isToken(Token.OPEN_SQUARE))
    					 {
    						 l++;
    					 }
    					 else if (lexeme.isToken(Token.CLOSE_SQUARE))
    					 {
    						 if (l==0)
    						 {
    							 if (i==end-1)
    							 {
    								 return parseExpression(start+1,end-1,elements);
    							 }
    	    	        		return new Node(NodeType.ATTRIBUTES,null,parseExpression(start+1,i,elements),parseExpression(i+1, end,elements));
    						 }
    						 l--;
    					 }
    				 }
    			}
    			throw new ParseException(startElement);
    		}
    		else
    		{
    			Element lastElement=elements.get(end-1);
        		if (lastElement.isToken(Token.CLOSE_SQUARE))
        		{
        			int l=0;
        			for (int i=end-2;i>=start;i--)
        			{
        				 Lexeme lexeme=elements.get(i).getLexeme();
        				 if (lexeme!=null)
        				 {
        					 if (lexeme.isToken(Token.CLOSE_SQUARE))
        					 {
        						 l++;
        					 }
        					 else if (lexeme.isToken(Token.OPEN_SQUARE))
        					 {
        						 if (l==0)
        						 {
        	    	        		return new Node(NodeType.LOOKUP,null,parseExpression(start,i,elements),parseExpression(i+1, end-1,elements));
        						 }
        						 l--;
        					 }
        				 }
        			}
        			throw new ParseException(startElement);
        		}
        		else if (startElement.isToken(Token.IDENTIFIER))
    			{
        			//Function call 
            		if (elements.get(start+1).isToken(Token.OPEN_ROUND)&&elements.get(end-1).isToken(Token.CLOSE_ROUND))
            		{
        				//function call
            			return new Node(NodeType.FUNCTION,elements.get(start),null,parseExpression(start+2, end-1,elements));
            		}
            		/*
            		Lexeme lexeme=startElement.getLexeme();
            		if (lexeme.isTokenIdentifier("mutable"))
            		{
            			return new Node(NodeType.MODIFIER,elements.get(start),null,parseExpression(start+1, end,elements));
            		}
            		if (lexeme.isTokenIdentifier("public"))
            		{
            			return new Node(NodeType.MODIFIER,elements.get(start),null,parseExpression(start+1, end,elements));
            		}
            		*/
        			throw new ParseException(startElement);
    			}
    		}
    		/*
    		else if (startElement.isToken(Token.IDENTIFIER))
			{
    			//Function call or array lookup
        		if (elements.get(start+1).isToken(Token.OPEN_ROUND)&&elements.get(end-1).isToken(Token.CLOSE_ROUND))
        		{
    				//function call
        			return new Node(NodeType.FUNCTION,elements.get(start),null,parseExpression(start+2, end-1,elements));
        		}
        		if (elements.get(start+1).isToken(Token.OPEN_SQUARE)&&elements.get(end-1).isToken(Token.CLOSE_SQUARE))
        		{
            		//array lookup
        			return new Node(NodeType.LOOKUP,startElement,null,parseExpression(start+2, end-1,elements));
        		}
			}
			*/
			throw new ParseException(startElement);
		}
		/*
		if (startElement.isToken(Token.OPERATOR))
		{
			//Unary operator function
			return new Node(NodeType.FUNCTION,startElement,null,parseExpression(start+1, end,elements));
		}
		*/
		if (lowestPrecedence==Integer.MAX_VALUE)
		{
			throw new ParseException();
		}
		Element lowest=elements.get(lowestPrecedenceIndex);
		//Sub-expression
		return new Node(NodeType.FUNCTION,lowest,parseExpression(start, lowestPrecedenceIndex,elements),parseExpression(lowestPrecedenceIndex+1,end,elements));
	}	
}
