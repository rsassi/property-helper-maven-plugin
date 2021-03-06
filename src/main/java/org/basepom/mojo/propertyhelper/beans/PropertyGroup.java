/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.basepom.mojo.propertyhelper.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.basepom.mojo.propertyhelper.beans.PropertyDefinition.getNameFunction;
import static org.basepom.mojo.propertyhelper.beans.PropertyDefinition.getValueFunction;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.basepom.mojo.propertyhelper.InterpolatorFactory;
import org.basepom.mojo.propertyhelper.TransformerRegistry;
import org.codehaus.plexus.interpolation.InterpolationException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class PropertyGroup
{
    /** Property group id. */
    private String id;

    /** Activate the group if the current project version does not contain SNAPSHOT. Field injected by Maven. */
    private boolean activeOnRelease = true;

    /** Activate the group if the current project version contains SNAPSHOT. Field injected by Maven. */
    private boolean activeOnSnapshot = true;

    /** Action if this property group defines a duplicate property. Field injected by Maven. */
    private String onDuplicateProperty = "fail";

    /** Action if any property from that group could not be defined. Field injected by Maven. */
    private String onMissingProperty = "fail";

    /** Property definitions in this group. Field injected by Maven. */
    private PropertyDefinition [] properties = new PropertyDefinition[0];

    // Must be noargs c'tor for maven property injection */
    public PropertyGroup()
    {
    }

    public String getId()
    {
        return id;
    }

    public PropertyGroup setId(String id)
    {
        this.id = id;
        return this;
    }

    public boolean isActiveOnRelease()
    {
        return activeOnRelease;
    }

    public PropertyGroup setActiveOnRelease(final boolean activeOnRelease)
    {
        this.activeOnRelease = activeOnRelease;
        return this;
    }

    public boolean isActiveOnSnapshot()
    {
        return activeOnSnapshot;
    }

    public PropertyGroup setActiveOnSnapshot(final boolean activeOnSnapshot)
    {
        this.activeOnSnapshot = activeOnSnapshot;
        return this;
    }

    public IgnoreWarnFail getOnDuplicateProperty()
    {
        return IgnoreWarnFail.forString(onDuplicateProperty);
    }

    public PropertyGroup setOnDuplicateProperty(final String onDuplicateProperty)
    {
        IgnoreWarnFail.forString(onDuplicateProperty);
        this.onDuplicateProperty = onDuplicateProperty;
        return this;
    }

    public IgnoreWarnFail getOnMissingProperty()
    {
        return IgnoreWarnFail.forString(onMissingProperty);
    }

    public PropertyGroup setOnMissingProperty(final String onMissingProperty)
    {
        IgnoreWarnFail.forString(onMissingProperty);
        this.onMissingProperty = onMissingProperty;
        return this;
    }

    public Map<String, String> getProperties()
    {
        return ImmutableMap.copyOf(Maps.transformValues(Maps.uniqueIndex(Arrays.asList(properties), getNameFunction()), getValueFunction()));
    }

    public PropertyGroup setProperties(final Map<String, String> properties)
    {
        checkNotNull(properties, "properties is null");
        this.properties = new PropertyDefinition[properties.size()];

        int i = 0;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            this.properties[i] = new PropertyDefinition(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public Set<String> getPropertyNames()
    {
        return ImmutableSet.copyOf(Iterables.transform(Arrays.asList(properties), getNameFunction()));
    }

    public String getPropertyValue(final InterpolatorFactory interpolatorFactory, final String propertyName, final Map<String, String> propElements) throws IOException, InterpolationException
    {
        final Map<String, PropertyDefinition> propertyMap = Maps.uniqueIndex(Arrays.asList(properties), getNameFunction());

        if (!propertyMap.keySet().contains(propertyName)) {
            return "";
        }

        final PropertyDefinition propertyDefinition = propertyMap.get(propertyName);

        final String result = interpolatorFactory.interpolate(propertyDefinition.getValue(), getOnMissingProperty(), propElements);
        return TransformerRegistry.applyTransformers(propertyDefinition.getTransformers(), result);
    }
}
