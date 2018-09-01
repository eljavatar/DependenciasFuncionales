package com.eljavatar.dependenciasfuncionales;

import java.util.Arrays;

/**
 *
 * @author Andres
 */
public class DF {
    
    private String[] x;
    private String[] y;

    public DF(String[] x, String[] y) {
        this.x = x;
        this.y = y;
    }

    public String[] getX() {
        return x;
    }

    public void setX(String[] x) {
        this.x = x;
    }

    public String[] getY() {
        return y;
    }

    public void setY(String[] y) {
        this.y = y;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Arrays.deepHashCode(this.x);
        hash = 97 * hash + Arrays.deepHashCode(this.y);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DF other = (DF) obj;
        if (!Arrays.equals(this.x, other.x)) {
            return false;
        }
        if (!Arrays.equals(this.y, other.y)) {
            return false;
        }
        return true;
    }
    
}
