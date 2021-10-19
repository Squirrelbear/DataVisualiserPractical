package CoreApplication;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Data Visualiser
 *
 * ClassCollectorUtility class:
 * This utility provides a reflection utility to collect relevant classes available to be used for
 * either searching or sorting algorithms by calling either getAllSortingAlgorithms() or the
 * getAllSearchingAlgorithms() method.
 *
 * Code in this file is based on the solution seen in source below:
 * https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class ClassCollectorUtility {

    /**
     * Searches the SortingAlgorithms package to get all relevant classes.
     *
     * @return A list of class names for all classes in the SortingAlgorithms package.
     */
    public static String[] getAllSortingAlgorithms() {
        return getAllFromPackage("SortingAlgorithms");
    }

    /**
     * Searches the SortingAlgorithms package to get all relevant classes.
     *
     * @return A list of class names for all classes in the SortingAlgorithms package.
     */
    public static String[] getAllSearchingAlgorithms() {
        return getAllFromPackage("SearchingAlgorithms");
    }

    /**
     * Searches the package name and strips out just the class name by taking the content
     * only appearing after the last . character. Exceptions will force a JOptionPage
     * to appear displaying a message about the reason for failure.
     *
     * @param packageName Loads all classes from the specified package name.
     * @return A list of strings of all the class files in the specified package.
     */
    private static String[] getAllFromPackage(String packageName) {
        List<String> result = new ArrayList<>();
        try {
            var classes = getClasses(packageName);
            for(var c : classes) {
                String className = c.getName();
                if(!className.isEmpty()) {
                    String[] splitClassPath = className.split("\\.");
                    if(splitClassPath.length > 0) {
                        result.add(splitClassPath[splitClassPath.length - 1]);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "Failed to load "+packageName+" algorithms. Class Not Found: " + e.getMessage());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Failed to load "+packageName+" algorithms. IOException: " + e.getMessage());
        }
        return result.toArray(new String[0]);
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package to search inside of.
     * @return The classes found inside the package.
     * @throws ClassNotFoundException Caused by invalid files that can't be loaded as a Class.
     * @throws IOException Caused by an invalid or impossible resource request.
     */
    private static Class<?>[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        var classes = new ArrayList<Class<?>>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[0]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirectories.
     *
     * @param directory   The base directory to search inside of.
     * @param packageName The package name for classes found inside the base directory.
     * @return The classes found inside the package.
     * @throws ClassNotFoundException Caused by failure to load a reference to a class from the filename.
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        if(files == null) {
            return classes;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
