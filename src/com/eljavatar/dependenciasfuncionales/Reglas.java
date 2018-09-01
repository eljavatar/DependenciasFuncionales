package com.eljavatar.dependenciasfuncionales;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Andres
 */
public class Reglas {
    
    public List<DF> getCierreConjunto(List<DF> listDF, List<String> dataT) {
        List<String[]> listX = new CopyOnWriteArrayList<>();
        listDF.forEach((df) -> {
            listX.add(orderArray(df.getX()));
        });
        
        dataT.forEach((t) -> {
            listX.stream().filter((x) -> (!Arrays.asList(x).contains(t))).map((x) -> Stream.of(x, new String[]{t}).flatMap(Stream::of).sorted().toArray(String[]::new)).filter((newX) -> (!listX.contains(newX))).map((newX) -> {
                listX.add(newX);
                return newX;
            }).forEachOrdered((newX) -> {
                //System.out.println(String.join(",", newX));
            });
        });
        
        List<DF> listCierreConjunto = new ArrayList<>();
        List<String[]> newListX = new ArrayList<>(new HashSet<>(listX));
        

        for (String[] x : newListX) {
            
            Set<String> resultado = Stream.of(x).collect(Collectors.toSet());
            boolean cambios = true;
            while (cambios) {
                boolean hasCambio = false;
                for (DF df : listDF) {
                    if (resultado.containsAll(Arrays.asList(df.getX()))) {
                        if (!resultado.containsAll(Arrays.asList(df.getY()))) {
                            hasCambio = true;
                        }
                        resultado.addAll(Arrays.asList(df.getY()));
                    }
                }
                cambios = hasCambio;
            }
            
            //List<String> resultadoOrder = resultado.stream().sorted().collect(Collectors.toList());
            
            for (String y : resultado) {
                if (!Arrays.asList(x).contains(y)) {
                    addDependencia(listCierreConjunto, x, new String[]{y});
                }
                
            }

        }
        
//        listCierreConjunto.forEach((df) -> {
//            System.out.println(String.join(",", df.getX()) + " -> " + String.join(",", df.getY()));
//        });
//        
//        
//        System.out.println("\n\n\n\n\n\n");
        
        return listCierreConjunto;
    }
    
    public String[] orderArray(String[] array) {
        return Stream.of(array).sorted().toArray(String[]::new);
    }
    
    public boolean containsAll(String[] arr1, String[] arr2) {
        for (String a1 : arr2) {
            if (!Arrays.asList(arr1).contains(a1)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean containsSome(String[] arr1, String[] arr2) {
        for (String a1 : arr1) {
            for (String a2 : arr2) {
                if (a1.equals(a2)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    
    public boolean reflexividad(List<DF> listDF, String[] dfImplicante, String[] dfImplicado, List<String> listCamino) {
        if (containsAll(dfImplicante, dfImplicado)) {
            listCamino.add("Por Reflexividad: '" + String.join(",", dfImplicante) + " -> " + String.join(",", dfImplicado) + "'");
            return true;
        }
        
        return false;
    }
    
    
    public boolean aumento(List<DF> listDF, CopyOnWriteArrayList<String> dataT, String[] dfImplicante, String[] dfImplicado, List<String> listCamino) {
        boolean aumentada = false;
        for (String t : dataT) {
            for (DF df : listDF) {
                if (!Arrays.asList(df.getX()).contains(t) && !Arrays.asList(df.getY()).contains(t)) {
                    String[] x = Stream.of(df.getX(), new String[]{t}).flatMap(Stream::of).sorted().toArray(String[]::new);
                    String[] y = Stream.of(df.getY(), new String[]{t}).flatMap(Stream::of).sorted().toArray(String[]::new);
                    if (addDependencia(listDF, x, y)) {
                        //System.out.println("Por Aumento de: '" + String.join(",", df.getX()) + " -> " + String.join(",", df.getY()) + "' con '" + t + "'  ==>  '" + String.join(",", x) + " -> " + String.join(",", y) + "'");
                        listCamino.add("Por Aumento de: '" + String.join(",", df.getX()) + " -> " + String.join(",", df.getY()) + "' con '" + t + "'  ==>  '" + String.join(",", x) + " -> " + String.join(",", y) + "'");
                    }
                    if (Arrays.equals(x, dfImplicante) && Arrays.equals(y, dfImplicado)) {
                        return true;
                    }
                    aumentada = true;
                }
            }
            if (aumentada) {
                dataT.remove(t);
                break;
            }
        }
        
        return false;
    }
    
    
    public boolean transitividad(List<DF> listDF, String[] dfImplicante, String[] dfImplicado, List<String> listCamino) {
        for (DF df : listDF) {
            for (DF df2 : listDF) {
                if (Arrays.equals(df.getY(), df2.getX())) {
                    if (addDependencia(listDF, orderArray(df.getX()), orderArray(df2.getY()))) {
                        //System.out.println("Por Transitividad de: '" + String.join(",", df.getX()) + " -> " + String.join(",", df.getY()) + "' y '" + String.join(",", df2.getX()) + " -> " + String.join(",", df2.getY()) + "'  ==>  '" + String.join(",", df.getX()) + " -> " + String.join(",", df2.getY()) + "'");
                        listCamino.add("Por Transitividad de: '" + String.join(",", df.getX()) + " -> " + String.join(",", df.getY()) + "' y '" + String.join(",", df2.getX()) + " -> " + String.join(",", df2.getY()) + "'  ==>  '" + String.join(",", df.getX()) + " -> " + String.join(",", df2.getY()) + "'");
                    }
                    if (Arrays.equals(df.getX(), dfImplicante) && Arrays.equals(df2.getY(), dfImplicado)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    
    public boolean descomposicion(List<DF> listDF, String[] dfImplicante, String[] dfImplicado, List<String> listCamino) {
        for (DF df : listDF) {
            if (df.getY().length > 1) {
                for (String e : df.getY()) {
                    if (addDependencia(listDF, orderArray(df.getX()), new String[]{e})) {
                        //System.out.println("Por Descomposición de: '" + String.join(",", df.getX()) + " -> " + String.join(",", df.getY()) + "'  ==>  '" + String.join(",", df.getX()) + " -> " + e + "'");
                        listCamino.add("Por Descomposición de: '" + String.join(",", df.getX()) + " -> " + String.join(",", df.getY()) + "'  ==>  '" + String.join(",", df.getX()) + " -> " + e + "'");
                    }
                    if (Arrays.equals(df.getX(), dfImplicante) && Arrays.equals(new String[]{e}, dfImplicado)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    
    public boolean union(List<DF> listDF, String[] dfImplicante, String[] dfImplicado, List<String> listCamino) {
        for (DF df : listDF) {
            for (DF df2 : listDF) {
                if (Arrays.equals(df.getX(), df2.getX()) && !containsSome(df.getY(), df2.getY())) {
                    if (addDependencia(listDF, df.getX(), Stream.of(df.getY(), df2.getY()).flatMap(Stream::of).sorted().toArray(String[]::new))) {
                        //System.out.println("Por Unión de: '" + String.join(",", df.getX()) + " -> " + String.join(",", df.getY()) + "' y '" + String.join(",", df.getX()) + " -> " + String.join(",", df2.getY()) + "'  ==>  '" + String.join(",", df.getX()) + " -> " + String.join(",", Stream.of(df.getY(), df2.getY()).flatMap(Stream::of).sorted().toArray(String[]::new)) + "'");
                        listCamino.add("Por Unión de: '" + String.join(",", df.getX()) + " -> " + String.join(",", df.getY()) + "' y '" + String.join(",", df.getX()) + " -> " + String.join(",", df2.getY()) + "'  ==>  '" + String.join(",", df.getX()) + " -> " + String.join(",", Stream.of(df.getY(), df2.getY()).flatMap(Stream::of).sorted().toArray(String[]::new)) + "'");
                    }
                    if (Arrays.equals(df.getX(), dfImplicante) && Arrays.equals(Stream.of(df.getY(), df2.getY()).flatMap(Stream::of).sorted().toArray(String[]::new), dfImplicado)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    
    public boolean pseudotransitividad(List<DF> listDF, String[] dfImplicante, String[] dfImplicado, List<String> listCamino) {
        for (DF df : listDF) {
            for (DF df2 : listDF) {
                if (!Arrays.equals(df.getY(), df2.getX()) && containsAll(df2.getX(), df.getY()) && (!containsSome(df.getX(), df2.getX()) || !containsSome(df2.getX(), df.getX()))) {
                    Set<String> existeVar = Stream.of(df.getY()).collect(Collectors.toSet());
                    List<String> listNew = Stream.of(df2.getX())
                            .filter(e -> !existeVar.contains(e))
                            .collect(Collectors.toList());
                    
                    String[] x = Stream.of(df.getX(), listNew.toArray(new String[listNew.size()])).flatMap(Stream::of).sorted().toArray(String[]::new);
                    
                    // df.getX() + (df2.getX() - df.getY())
                    if (addDependencia(listDF, x, orderArray(df2.getY()))) {
                        //System.out.println("02 - Por PseudoTransitividad de: '" + String.join(",", df.getX()) + " -> " + String.join(",", df.getY()) + "' y '" + String.join(",", df2.getX()) + " -> " + String.join(",", df2.getY()) + "'  ==>  '" + String.join(",", x) + " -> " + String.join(",", df2.getY()) + "'");
                        listCamino.add("Por PseudoTransitividad de: '" + String.join(",", df.getX()) + " -> " + String.join(",", df.getY()) + "' y '" + String.join(",", df2.getX()) + " -> " + String.join(",", df2.getY()) + "'  ==>  '" + String.join(",", x) + " -> " + String.join(",", df2.getY()) + "'");
                    }
                    if (Arrays.equals(x, dfImplicante) && Arrays.equals(df2.getY(), dfImplicado)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    
    private boolean addDependencia(List<DF> listDF, String[] dfImplicante, String[] dfImplicado) {
        for (DF df : listDF) {
            if (Arrays.equals(df.getX(), dfImplicante) && Arrays.equals(df.getY(), dfImplicado)) {
                return false;
            }
        }
        listDF.add(new DF(dfImplicante, dfImplicado));
        return true;
    }
    
    
    public boolean validarDependencia(List<DF> listDF, CopyOnWriteArrayList<String> dataT, String[] dfImplicante, String[] dfImplicado, List<String> listCamino, List<DF> listCierreConjunto) {
        if (reflexividad(listDF, dfImplicante, dfImplicado, listCamino)
                || pseudotransitividad(listDF, dfImplicante, dfImplicado, listCamino)
                || transitividad(listDF, dfImplicante, dfImplicado, listCamino)
                || descomposicion(listDF, dfImplicante, dfImplicado, listCamino)
                || union(listDF, dfImplicante, dfImplicado, listCamino)
                || aumento(listDF, dataT, dfImplicante, dfImplicado, listCamino)) {
            return true;
        } else {
            if (listDF.containsAll(listCierreConjunto)) {
                return false;
            }
            return validarDependencia(listDF, dataT, dfImplicante, dfImplicado, listCamino, listCierreConjunto);
        }
    }
    
    
    public boolean validarExistenciaDependencia(List<DF> listDF, String[] dfImplicante) {
        return listDF.stream().anyMatch((df) -> (containsAll(dfImplicante, df.getX())));
    }
    
}
