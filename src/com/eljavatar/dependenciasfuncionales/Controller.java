package com.eljavatar.dependenciasfuncionales;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Andres
 */
public class Controller {
    
    private final Main view;
    private final R r;
    private final Reglas reglas;
    private final List<DF> listDF;
    private final List<String> listCamino;
    
    public Controller(Main view) {
        this.view = view;
        this.r = new R(new String[]{}, new ArrayList<>());
        this.reglas = new Reglas();
        this.listDF = new CopyOnWriteArrayList<>();
        this.listCamino = new ArrayList<>();
    }
    
    public void addDependenciaL() {
        if (view.getjTFdfL1().getText().trim().isEmpty() || view.getjTFdfL2().getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Debe ingresar la dependencia funcional", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        DF l = new DF(trimData(view.getjTFdfL1().getText().trim()), trimData(view.getjTFdfL2().getText().trim()));
        
        this.r.getL().add(l);
        
        this.view.getTableModel().addRow(new Object[]{String.join(",", l.getX()), String.join(",", l.getY())});
        this.view.getTableModel().fireTableDataChanged();
        
        this.view.getjTFdfL1().setText("");
        this.view.getjTFdfL2().setText("");
    }
    
    private String[] trimData(String df) {
        String[] dfArray = df.split(",");
        for (int i = 0; i < dfArray.length; i++) {
            dfArray[i] = dfArray[i].trim();
        }
        return reglas.orderArray(dfArray);
    }
    
    public void validar() {
        if (view.getjTFdatosT().getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Debe ingresar un conjunto de datos (T)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (view.getjTFdfA1().getText().trim().isEmpty() || view.getjTFdfA2().getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Debe ingresar la dependencia funcional a validar (A)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (this.r.getL().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Debe ingresar al menos una dependencia funcional (L)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String[] t = trimData(view.getjTFdatosT().getText().trim());
        String[] dfA1 = trimData(view.getjTFdfA1().getText().trim());
        String[] dfA2 = trimData(view.getjTFdfA2().getText().trim());
        
        if (!reglas.containsAll(t, dfA1) || !reglas.containsAll(t, dfA2)) {
            JOptionPane.showMessageDialog(view, "La dependencia funcional a validar (A), no pertenece al conjunto de datos (T)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (DF df : r.getL()) {
            if (!reglas.containsAll(t, df.getX()) || !reglas.containsAll(t, df.getY())) {
                JOptionPane.showMessageDialog(view, "Una de las dependencias funcionales (L) declaradas, no pertenece al conjunto de datos (T)", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        
        this.r.setT(t);
        
        listDF.clear();
        listDF.addAll(r.getL());
        
        List<DF> listCierreConjunto = reglas.getCierreConjunto(listDF, Arrays.asList(t));
        
        listCamino.clear();
        StringBuilder sbResult = new StringBuilder("");
        
        if (reglas.validarExistenciaDependencia(listDF, dfA1)) {
            boolean encontrado = reglas.validarDependencia(listDF, new CopyOnWriteArrayList<>(t), dfA1, dfA2, listCamino, listCierreConjunto);
            
//            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n");
//            listDF.forEach((df) -> {
//                System.out.println(String.join(",", df.getX()) + " -> " + String.join(",", df.getY()));
//            });
//            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n");
            
            listCamino.forEach((linea) -> {
                sbResult.append(linea).append("\n");
            });
            if (!encontrado) {
                sbResult.append("\n\n").append("No se ha podido derivar la dependencia funcional (A)").append("\n\n");
            }
        } else {
            sbResult.append("No se ha podido derivar la dependencia funcional (A)").append("\n\n");
        }
        
        this.view.getjTAresultados().setText(sbResult.toString());
    }
    
    
    public void limpiar() {
        this.view.getjTFdfL1().setText("");
        this.view.getjTFdfL2().setText("");
        
        this.view.getjTFdfA1().setText("");
        this.view.getjTFdfA2().setText("");
        
        this.view.getjTAresultados().setText("");
        
        this.view.getTableModel().getDataVector().clear();
        this.view.getTableModel().fireTableDataChanged();
        
        listDF.clear();
        this.r.setT(new String[]{});
        this.r.getL().clear();
    }
    
}
