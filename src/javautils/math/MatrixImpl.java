package javautils.math;

import javautils.Logger;
import javautils.parser.ArrayParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatrixImpl implements Matrix {

    private int numRows, numCols;
    private Vector[] rows;

    public MatrixImpl() {
        init(0, 0);
    }

    public MatrixImpl(int numRows, int numCols) {
        init(numRows, numCols);
    }

    public void init(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.rows = new Vector[numRows];
        for(int i = 0; i < numRows; i++) {
            rows[i] = new VectorImpl(numCols);
        }
        Logger.info("MatrixImpl init -> " + rows.length + " x " + numCols);
    }

    public Vector[] getElements() {
        return this.rows;
    }

    @Override
    public double get(int row, int col) {
        return getRow(row).get(col);
    }

    @Override
    public void set(int row, int col, double value) {
        final Vector rowVector = (Vector) getRow(row);
        rowVector.set(col, value);
    }

    @Override
    public int numRows() {
        return numRows;
    }

    @Override
    public int numCols() {
        return numCols;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Matrix) {
            return Matrix.isSame(this, (Matrix) other);
        } else {
            return false;
        }
    }

    @Override
    public Vector getRow(int row) {
        return (Vector) getElements()[row];
    }
}
