package com.example.das_proyectogrupal_anenaiaramaite;

public class Pregunta {
    // Atributos
    private String titulo;
    private boolean cerrada;
    private String enunciado;
    private String email_autor;
    private int cont1, cont2, cont3, cont4;
    private String op1, op2, op3, op4;


    public Pregunta(String titulo, boolean cerrada, String enunciado, String email_autor,
                    int cont1, int cont2, int cont3, int cont4,
                    String op1, String op2, String op3, String op4) {

        this.titulo = titulo;
        this.cerrada = cerrada;
        this.enunciado = enunciado;
        this.email_autor = email_autor;
        this.cont1 = cont1;
        this.cont2 = cont2;
        this.cont3 = cont3;
        this.cont4 = cont4;
        this.op1 = op1;
        this.op2 = op2;
        this.op3 = op3;
        this.op4 = op4;
    }


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isCerrada() {
        return cerrada;
    }

    public void setCerrada(boolean cerrada) {
        this.cerrada = cerrada;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getEmailAutor() {
        return email_autor;
    }

    public void setEmailAutor(String email_autor) {
        this.email_autor = email_autor;
    }

    public int getCont1() {
        return cont1;
    }

    public void setCont1(int cont1) {
        this.cont1 = cont1;
    }

    public int getCont2() {
        return cont2;
    }

    public void setCont2(int cont2) {
        this.cont2 = cont2;
    }

    public int getCont3() {
        return cont3;
    }

    public void setCont3(int cont3) {
        this.cont3 = cont3;
    }

    public int getCont4() {
        return cont4;
    }

    public void setCont4(int cont4) {
        this.cont4 = cont4;
    }

    public String getOp1() {
        return op1;
    }

    public void setOp1(String op1) {
        this.op1 = op1;
    }

    public String getOp2() {
        return op2;
    }

    public void setOp2(String op2) {
        this.op2 = op2;
    }

    public String getOp3() {
        return op3;
    }

    public void setOp3(String op3) {
        this.op3 = op3;
    }

    public String getOp4() {
        return op4;
    }

    public void setOp4(String op4) {
        this.op4 = op4;
    }
}
