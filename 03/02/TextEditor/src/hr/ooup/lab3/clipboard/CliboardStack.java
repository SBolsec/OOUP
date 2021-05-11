package hr.ooup.lab3.clipboard;

import hr.ooup.lab3.observer.ClipboardObserver;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

public class CliboardStack {
    Stack<String> texts = new Stack<>();
    List<ClipboardObserver> clipboardObservers = new ArrayList<>();

    public CliboardStack() {
        super();
    }

    public void push(String text) {
        texts.push(text);
        notifyClipboardObservers();
    }

    public String pop() throws EmptyStackException {
        String value = texts.pop();
        notifyClipboardObservers();
        return value;
    }

    public String peek() throws EmptyStackException {
        return texts.peek();
    }

    public boolean isEmpty() {
        return texts.isEmpty();
    }

    public void clear() {
        texts.clear();
        notifyClipboardObservers();
    }

    public void attachClipboardObserver(ClipboardObserver observer) {
        clipboardObservers.add(observer);
    }

    public void dettachClipboardObserver(ClipboardObserver observer) {
        clipboardObservers.remove(observer);
    }

    public void notifyClipboardObservers() {
        for (ClipboardObserver o : clipboardObservers) {
            o.updateClipboard();
        }
    }
}
