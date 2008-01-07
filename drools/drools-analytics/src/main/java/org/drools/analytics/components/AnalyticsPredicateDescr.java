package org.drools.analytics.components;

import org.drools.analytics.report.components.Cause;
import org.drools.analytics.report.components.CauseType;

/**
 *
 * @author Toni Rikkola
 */
public class AnalyticsPredicateDescr extends AnalyticsComponent implements
		Cause {

	private static int index = 0;

	private String content;
	private String classMethodName;

	public AnalyticsPredicateDescr() {
		super(index++);
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.PREDICATE;
	}

	public CauseType getCauseType() {
		return CauseType.PREDICATE;
	}

	public String getClassMethodName() {
		return classMethodName;
	}

	public void setClassMethodName(String classMethodName) {
		this.classMethodName = classMethodName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Predicate id: " + id + " content: " + content;
	}
}
