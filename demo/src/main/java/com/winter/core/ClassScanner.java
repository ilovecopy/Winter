package com.winter.core;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author tangjincheng
 * @date 2026/10/25
 */
public class ClassScanner {
    /**
     * 扫描指定包名下的所有类
     *
     * @param basePackage
     * @return {@link Set }<{@link Class }<{@link ? }>>
     */
    public Set<Class<?>> scan(String basePackage) throws ClassNotFoundException, URISyntaxException, IOException {
        String path = basePackage.replace('.', '/');
        Enumeration<URL> systemResources = ClassLoader.getSystemResources(path);
        Set<Class<?>> result = new LinkedHashSet<>();
        while (systemResources.hasMoreElements()) {
            URL resource = systemResources.nextElement();
            File file = new File(resource.toURI());
            scanDirectory(file, basePackage, result);
        }
        return result;
    }

    /**
     * 扫描指定目录下的所有类
     *
     * @param dir
     * @param packageName
     * @param out
     */
    private void scanDirectory(File dir, String packageName, Set<Class<?>> out) throws ClassNotFoundException {
        if (dir.isDirectory()) {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file.isDirectory()) {
                    scanDirectory(file, packageName + "." + file.getName(), out);
                } else {
                    scanDirectory(file, packageName, out);
                }
            }
        } else {
            if (dir.getName().endsWith(".class")) {//如果扫描到文件，将文件名加到out
                out.add(Class.forName(packageName + "." + dir.getName().replace(".class", "")));
            }
        }
    }

}
