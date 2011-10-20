package com.likeness.maven.plugins.numbers;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class TestLoadDefines
{
    @Test
    public void testOneTransientNumber() throws Exception
    {
        final TestMojo testMojo = new TestMojo(ImmutableList.of(new NumberDefinition("hello", null, 1, 1, false, false, null)));
        testMojo.loadDefines();
        final Map<File, Properties> propertiesFiles = testMojo.getPropertiesFiles();
        Assert.assertNotNull(propertiesFiles);
        Assert.assertTrue(propertiesFiles.isEmpty());
    }

    @Test
    public void testTwoTransientNumbers() throws Exception
    {
        final TestMojo testMojo = new TestMojo(ImmutableList.of(new NumberDefinition("hello", null, 1, 1, false, false, null), new NumberDefinition("world", null, 1, 1, false, false, null)));
        testMojo.loadDefines();
        final Map<File, Properties> propertiesFiles = testMojo.getPropertiesFiles();
        Assert.assertNotNull(propertiesFiles);
        Assert.assertTrue(propertiesFiles.isEmpty());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testErrorNameTwice() throws Exception
    {
        final TestMojo testMojo = new TestMojo(ImmutableList.of(new NumberDefinition("hello", null, 1, 1, false, false, null), new NumberDefinition("hello", null, 1, 1, false, false, null)));
        testMojo.loadDefines();
    }

    @Test
    public void testCreateOnePersistentNumber() throws Exception
    {
        final File numberFile = File.createTempFile("test", null);
        numberFile.deleteOnExit();

        final TestMojo testMojo = new TestMojo(ImmutableList.of(new NumberDefinition("hello", numberFile, 1, 1, true, true, null)));
        testMojo.loadDefines();
        final Map<File, Properties> propertiesFiles = testMojo.getPropertiesFiles();
        Assert.assertNotNull(propertiesFiles);
        Assert.assertEquals(1, propertiesFiles.size());
        Assert.assertTrue(propertiesFiles.containsKey(numberFile));
    }

    @Test
    public void testCreateTwoPersistentNumbers() throws Exception
    {
        final File numberFile = File.createTempFile("test", null);
        final File numberFile2 = File.createTempFile("test", null);
        numberFile.deleteOnExit();
        numberFile2.deleteOnExit();

        final TestMojo testMojo = new TestMojo(ImmutableList.of(new NumberDefinition("hello", numberFile, 1, 1, true, true, null),
                                                                new NumberDefinition("world", numberFile2, 1, 1, true, true, null)));
        testMojo.loadDefines();
        final Map<File, Properties> propertiesFiles = testMojo.getPropertiesFiles();
        Assert.assertNotNull(propertiesFiles);
        Assert.assertEquals(2, propertiesFiles.size());
        Assert.assertTrue(propertiesFiles.containsKey(numberFile));
        Assert.assertTrue(propertiesFiles.containsKey(numberFile2));
    }

    @Test
    public void testCreateTwoPersistentNumbersSameFile() throws Exception
    {
        final File numberFile = File.createTempFile("test", null);
        numberFile.deleteOnExit();

        final TestMojo testMojo = new TestMojo(ImmutableList.of(new NumberDefinition("hello", numberFile, 1, 1, true, true, null),
                                                                new NumberDefinition("world", numberFile, 1, 1, true, true, null)));
        testMojo.loadDefines();
        final Map<File, Properties> propertiesFiles = testMojo.getPropertiesFiles();
        Assert.assertNotNull(propertiesFiles);
        Assert.assertEquals(1, propertiesFiles.size());
        Assert.assertTrue(propertiesFiles.containsKey(numberFile));
    }

    @Test
    public void testOnePersistentNumberNoFile() throws Exception
    {
        final File numberFile = File.createTempFile("test", null);
        numberFile.delete();

        final TestMojo testMojo = new TestMojo(ImmutableList.of(new NumberDefinition("hello", numberFile, 1, 1, true, true, null)));
        testMojo.loadDefines();
        final Map<File, Properties> propertiesFiles = testMojo.getPropertiesFiles();
        Assert.assertNotNull(propertiesFiles);
        Assert.assertEquals(0, propertiesFiles.size());
    }

    @Test
    public void testCreateOnePersistentNumberNoCreation() throws Exception
    {
        final File numberFile = File.createTempFile("test", null);
        numberFile.deleteOnExit();

        final TestMojo testMojo = new TestMojo(ImmutableList.of(new NumberDefinition("hello", numberFile, 1, 1, true, false, null)));
        testMojo.loadDefines();
        final Map<File, Properties> propertiesFiles = testMojo.getPropertiesFiles();
        Assert.assertNotNull(propertiesFiles);
        Assert.assertEquals(1, propertiesFiles.size());
        Assert.assertTrue(propertiesFiles.containsKey(numberFile));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOnePersistentNumberNoPropertyCreation() throws Exception
    {
        final File numberFile = File.createTempFile("test", null);
        numberFile.deleteOnExit();

        final TestMojo testMojo = new TestMojo(ImmutableList.of(new NumberDefinition("hello", numberFile, 1, 1, false, true, null)));
        testMojo.loadDefines();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOnePersistentNumberNoFileNoCreation() throws Exception
    {
        final File numberFile = File.createTempFile("test", null);
        numberFile.delete();

        final TestMojo testMojo = new TestMojo(ImmutableList.of(new NumberDefinition("hello", numberFile, 1, 1, true, false, null)));
        testMojo.loadDefines();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOnePersistentNumberNoFileNoPropertyCreation() throws Exception
    {
        final File numberFile = File.createTempFile("test", null);
        numberFile.delete();

        final TestMojo testMojo = new TestMojo(ImmutableList.of(new NumberDefinition("hello", numberFile, 1, 1, false, true, null)));
        testMojo.loadDefines();
    }



    public static class TestMojo extends AbstractNumbersMojo
    {
        TestMojo(final List<NumberDefinition> numberDefines)
        {
            final NumberGroup numberGroup = new NumberGroup();
            numberGroup.setName("test");
            numberGroup.setNumbers(numberDefines);
            super.numberGroups = ImmutableList.of(numberGroup);
            super.activation = "test";
        }

        @Override
        protected void doExecute() throws Exception
        {
        }

        private Map<File, Properties> getPropertiesFiles()
        {
            return super.propertiesFiles;
        }

        public void loadDefines()
            throws Exception
        {
            final List<NumberDefinition> numberDefines = getNumberDefines();
            super.loadDefines(numberDefines);
        }

    }
}
