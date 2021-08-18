package com.example.mynotes;

import java.io.Serializable;

public class TodoListClass implements Serializable {
    String todo;
    boolean isCancelled;

    public TodoListClass(String todo, boolean isCancelled) {
        this.todo = todo;
        this.isCancelled = isCancelled;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }
}
