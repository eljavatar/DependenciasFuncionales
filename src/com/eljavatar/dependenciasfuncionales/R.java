package com.eljavatar.dependenciasfuncionales;

import java.util.List;

/**
 *
 * @author Andres
 */
public class R {
    
    private String[] t;
    private List<DF> l;

    public R(String[] t, List<DF> l) {
        this.t = t;
        this.l = l;
    }

    public String[] getT() {
        return t;
    }

    public void setT(String[] t) {
        this.t = t;
    }

    public List<DF> getL() {
        return l;
    }

    public void setL(List<DF> l) {
        this.l = l;
    }
    
}
