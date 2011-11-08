package com.likeness.maven.plugins.numbers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.likeness.maven.plugins.numbers.beans.NumberDefinition;
import org.apache.commons.lang3.StringUtils;

import static java.lang.String.format;

public class NumberField implements PropertyElement
{
    private static final Pattern MATCH_GROUPS = Pattern.compile("\\d+|[^\\d]+");

    private final NumberDefinition numberDefinition;
    private final ValueProvider valueProvider;

    private final List<String> elements = Lists.newArrayList();
    private final List<Integer> numberElements = Lists.newArrayList();

    public NumberField(final NumberDefinition numberDefinition, final ValueProvider valueProvider)
    {
        this.numberDefinition = numberDefinition;
        this.valueProvider = valueProvider;
    }


    @Override
    public String getPropertyName()
    {
        return numberDefinition.getPropertyName();
    }

    @Override
    public String getPropertyValue()
    {
        parse();
        return numberElements.isEmpty() ? null : elements.get(numberElements.get(numberDefinition.getFieldNumber()));
    }

    private void parse()
    {
        final String value = valueProvider.getValue();

        final Matcher m = MATCH_GROUPS.matcher(value);
        elements.clear();
        numberElements.clear();

        while (m.find()) {
            final String matchValue = m.group();
            elements.add(matchValue);
            if (isNumber(matchValue)) {
                numberElements.add(elements.size() - 1);
            }
        }

        Preconditions.checkState(numberElements.size() > numberDefinition.getFieldNumber(), format("Only %d fields in %s, field %d requested.", numberElements.size(), value, numberDefinition.getFieldNumber()));
    }

    private boolean isNumber(final CharSequence c) {
        for (int i = 0 ; i < c.length(); i++) {
            if (!Character.isDigit(c.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public void increment()
    {
        final Long value = getNumberValue();
        if (value != null) {
            setNumberValue(value + numberDefinition.getIncrement());
        }
    }

    public void setNumberValue(final Long value)
    {
        parse();
        if (!numberElements.isEmpty()) {
            elements.set(numberElements.get(numberDefinition.getFieldNumber()), value.toString());
            valueProvider.setValue(StringUtils.join(elements, null));
        }
    }

    public Long getNumberValue()
    {
        String fieldValue = getPropertyValue();
        return fieldValue == null ? null : new Long(fieldValue);
    }

    @Override
    public String toString()
    {
        parse();
        return StringUtils.join(elements, null);
    }
}
