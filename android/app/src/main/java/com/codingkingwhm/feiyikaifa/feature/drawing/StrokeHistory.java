package com.codingkingwhm.feiyikaifa.feature.drawing;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class StrokeHistory {

    private final List<Stroke> done = new ArrayList<>();
    private final Deque<Stroke> redo = new ArrayDeque<>();

    public void add(Stroke stroke) {
        done.add(stroke);
        redo.clear();
    }

    public boolean undo() {
        if (done.isEmpty()) {
            return false;
        }
        redo.push(done.remove(done.size() - 1));
        return true;
    }

    public boolean redo() {
        if (redo.isEmpty()) {
            return false;
        }
        done.add(redo.pop());
        return true;
    }

    public void clear() {
        done.clear();
        redo.clear();
    }

    public void replaceAll(List<Stroke> strokes) {
        clear();
        if (strokes != null) {
            for (Stroke stroke : strokes) {
                done.add(new Stroke(stroke));
            }
        }
    }

    public List<Stroke> snapshot() {
        List<Stroke> copy = new ArrayList<>();
        for (Stroke stroke : done) {
            copy.add(new Stroke(stroke));
        }
        return copy;
    }

    public List<Stroke> getDone() {
        return done;
    }

    public int size() {
        return done.size();
    }
}
