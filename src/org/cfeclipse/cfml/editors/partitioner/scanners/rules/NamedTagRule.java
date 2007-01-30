/*
 * Created on Nov 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.cfeclipse.cfml.editors.partitioner.scanners.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cfeclipse.cfml.editors.partitioner.TagData;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;


/**
 * @author Stephen Milligan
 *
 * This rule is used to find tags
 */
public class NamedTagRule implements IPredicateRule {

	/** Internal setting for the uninitialized column constraint */
	protected static final int UNDEFINED= -1;

	/** The partition type for the start and end tags */
	protected String fPartitionType;
	/** The partition type for the contents of the start tag */
	protected String fMidPartitionType;
	/** The pattern's start sequence */
	protected char[] fStartSequence;
	/** The pattern's end sequence */
	protected char[] fEndSequence;
	/** The pattern's column constrain */
	protected int fColumn= UNDEFINED;

	/** Are we inside double quotes open */
	private boolean fDblQuotesOpen = false;
	/** Are we inside single quotes open */
	private boolean fSnglQuotesOpen = false;
	/**
	 * Indicates whether the escape character continues a line
	 * @since 3.0
	 */
	protected boolean fEscapeContinuesLine;
	/** Indicates whether end of line terminates the pattern */
	protected boolean fBreaksOnEOL = false;
	/** Indicates whether end of file terminates the pattern */
	protected boolean fBreaksOnEOF = true;

	/** Pattern to make sure that the character after the end of the start sequence is valid */
	private static Pattern p = Pattern.compile("[\\s/>]");
	
	/**
	 * Creates a rule for the given starting and ending sequence.
	 * When these sequences are detected the rule will return the specified token.
	 * Alternatively, the sequence can also be ended by the end of the line.
	 * Any character which follows the given escapeCharacter will be ignored.
	 *
	 * @param startSequence the pattern's start sequence
	 * @param endSequence the pattern's end sequence, <code>null</code> is a legal value
	 * @param token the token which will be returned on success
	 * @param escapeCharacter any character following this one will be ignored
	 * @param breaksOnEOL indicates whether the end of the line also terminates the pattern
	 */
	public NamedTagRule(String startSequence, String endSequence, String partitionType, String midPartitionType) {
		Assert.isTrue(startSequence != null && startSequence.length() > 0);
		Assert.isNotNull(partitionType);
		fStartSequence= startSequence.toCharArray();
		fEndSequence= (endSequence == null ? new char[0] : endSequence.toCharArray());
		fPartitionType= partitionType;
		fMidPartitionType = midPartitionType;
	}
	

	
	/**
	 * Sets a column constraint for this rule. If set, the rule's token
	 * will only be returned if the pattern is detected starting at the 
	 * specified column. If the column is smaller then 0, the column
	 * constraint is considered removed.
	 *
	 * @param column the column in which the pattern starts
	 */
	public void setColumnConstraint(int column) {
		if (column < 0)
			column= UNDEFINED;
		fColumn= column;
	}
	
	
	/**
	 * Evaluates this rules without considering any column constraints.
	 *
	 * @param scanner the character scanner to be used
	 * @return the token resulting from this evaluation
	 */
	protected IToken doEvaluate(ICharacterScanner scanner) {
		return doEvaluate(scanner, false);
	}
	
	/**
	 * Evaluates this rule without considering any column constraints. Resumes
	 * detection, i.e. look sonly for the end sequence required by this rule if the
	 * <code>resume</code> flag is set.
	 *
	 * @param scanner the character scanner to be used
	 * @param resume <code>true</code> if detection should be resumed, <code>false</code> otherwise
	 * @return the token resulting from this evaluation
	 * @since 2.0
	 */
	protected IToken doEvaluate(ICharacterScanner scanner, boolean resume) {
		StringBuffer tagString = new StringBuffer();
		fDblQuotesOpen = false;
		fSnglQuotesOpen = false;
		if (resume) {
			
			if (endSequenceDetected(scanner, tagString)) {
			    TagData data = null;
			    if (fStartSequence[1] != '/') {
			        data = new TagData(fPartitionType + "_begin",tagString.toString(), fMidPartitionType,fPartitionType +"_end", new String(fStartSequence).substring(1) );
			    } else {
			        data = new TagData(fPartitionType,tagString.toString(), fMidPartitionType,fPartitionType, new String(fStartSequence).substring(2));
			    }
				return new Token(data);
			}
		
		} else {
			
			int c= scanner.read();
			if (c == fStartSequence[0]) {
			    tagString.append((char)c);
				if (sequenceDetected(scanner, fStartSequence, false, tagString)) {
					if (endSequenceDetected(scanner, tagString)) {
					    TagData data = null;
					    if (fStartSequence[1] != '/') {
					        data = new TagData(fPartitionType + "_begin",tagString.toString(), fMidPartitionType,fPartitionType +"_end", new String(fStartSequence).substring(1) );
					    } else {
					        data = new TagData(fPartitionType,tagString.toString(), fMidPartitionType,fPartitionType, new String(fStartSequence).substring(2));
					    }
						return new Token(data);
					}
				}
			}
		}
		
		scanner.unread();
		return Token.UNDEFINED;
	}	
	
	/*
	 * @see IRule#evaluate(ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}	
	
	/**
	 * Returns whether the end sequence was detected. As the pattern can be considered 
	 * ended by a line delimiter, the result of this method is <code>true</code> if the 
	 * rule breaks on the end  of the line, or if the EOF character is read.
	 *
	 * @param scanner the character scanner to be used
	 * @return <code>true</code> if the end sequence has been detected
	 */
	protected boolean endSequenceDetected(ICharacterScanner scanner, StringBuffer tagString) {
		int c;
		//char[][] delimiters= scanner.getLegalLineDelimiters();
		//System.out.println("Looking for end sequence for tagString " + tagString);
		while ((c = scanner.read()) != ICharacterScanner.EOF) {
			//boolean isEscapeChar = false;
			int uc = c;
			tagString.append((char)c);
			if (c > 96 && c <= 122) {
				uc = c-32;
			} else if(c>64 && c <= 90) {
				uc = c+32;
			}
			// Check if we're inside quotes
			if (c == '"' 
				|| c == '\'') {
				if (c == '"' && !fSnglQuotesOpen) { 
					fDblQuotesOpen = !fDblQuotesOpen;
				}
				if (c == '\'' && !fDblQuotesOpen) {
					fSnglQuotesOpen = !fSnglQuotesOpen;
				}
				// Skip the escaped character.
				//scanner.read();
			} else if (c == '<' 
			    && !fDblQuotesOpen
			    && !fSnglQuotesOpen) {
			    scanner.unread();
			    //tagString.deleteCharAt(tagString.length()-1);
			    return true;
			} else if (fEndSequence.length > 0 
								&& (c == fEndSequence[0] 
										|| uc == fEndSequence[0])) {
			    //System.out.println("Found matching first char " + (char)c + " for end sequence " + new String(fEndSequence) + " at offset " + ((CFPartitionScanner)scanner).getOffset());
				// Check if the specified end sequence has been found.
				if (!fDblQuotesOpen && !fSnglQuotesOpen) {
				    if (fEndSequence.length == 1) {
				        return true;
				    }
					if (sequenceDetected(scanner, fEndSequence, true, tagString)) {
						return true;
					}
				}
			}
		}
		if (fBreaksOnEOF) return true;
		scanner.unread();
		return false;
	}
	
	/**
	 * Returns whether the next characters to be read by the character scanner
	 * are an exact match with the given sequence. No escape characters are allowed 
	 * within the sequence. If specified the sequence is considered to be found
	 * when reading the EOF character.
	 *
	 * @param scanner the character scanner to be used
	 * @param sequence the sequence to be detected
	 * @param eofAllowed indicated whether EOF terminates the pattern
	 * @return <code>true</code> if the given sequence has been detected
	 */
	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed, StringBuffer tagString) {
		for (int i= 1; i < sequence.length; i++) {
			int c = scanner.read();
			int uc = c;
			tagString.append((char)c);
			if (c > 96 && c <= 122) {
				uc = c-32;
			} else if(c>64 && c <= 90) {
				uc = c+32;
			}
			if (c != sequence[i]
					&& uc != sequence[i]) {
				// Non-matching character detected, rewind the scanner back to the start.
				// Do not unread the first character.
				scanner.unread();
				for (int j= i-1; j > 0; j--)
					scanner.unread();
				return false;
			}
		}
		int tmp = scanner.read();
		scanner.unread();
		if (tmp == ICharacterScanner.EOF) {
		    return true;
		}
		char next = (char)tmp;
		Matcher m = p.matcher(String.valueOf(next));
		if (!m.matches()) {
			//System.out.println("Named tag found for " + new String(this.fStartSequence) + ", but next char " + next +" is invalid.");
		    for (int j = sequence.length-1; j > 0; j--)
				scanner.unread();
			return false;
		}
		else {
			//System.out.println("Found tag " + new String(sequence) + " with next char " + next);
		}
		return true;
	}
	
	/*
	 * @see IPredicateRule#evaluate(ICharacterScanner, boolean)
	 * @since 2.0
	 */
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		if (fColumn == UNDEFINED)
			return doEvaluate(scanner, resume);
		
		int c= scanner.read();
		scanner.unread();
		if (c == fStartSequence[0])
			return (fColumn == scanner.getColumn() ? doEvaluate(scanner, resume) : Token.UNDEFINED);
		else
			return Token.UNDEFINED;	
	}

	/*
	 * @see IPredicateRule#getSuccessToken()
	 * @since 2.0
	 */
	public IToken getSuccessToken() {
		return new Token(fPartitionType);
	}
}
