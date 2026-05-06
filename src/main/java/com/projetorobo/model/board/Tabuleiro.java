package com.projetorobo.model.board;

import com.projetorobo.exception.*;
import com.projetorobo.model.enums.*;
import com.projetorobo.model.obstaculos.*;
import com.projetorobo.model.robo.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tabuleiro {
    private final int tamanho;
    private final List<Robo> robos;
    private int alimentoX;
    private int alimentoY;
    private final List<Obstaculo> obstaculos;

    public Tabuleiro(int tamanho, int alimentoX, int alimentoY) {
        this.tamanho = tamanho;
        this.robos = new ArrayList<>();
        setAlimento(alimentoX, alimentoY);
        this.obstaculos = new ArrayList<>();
    }

    public void moverRobo(Robo robo, Direcao dir) throws MovimentoInvalidoException, ColisaoComObstaculoException {
        robo.mover(dir);

        if(verificarAlimento(robo)) {
            robo.setAchouAlimento(true);
            robos.remove(robo);
        }

        if (foraDoLimite(robo.getNewX(), robo.getNewY())) {
            robo.desfazerMovimento();
            robo.incrementarInvalidos();
            robo.getEstrategiaMovimento().registrarDirecaoInvalida(dir);
            throw new MovimentoInvalidoException(dir.name());

        } else if (temOutroRobo(robo, robo.getNewX(), robo.getNewY())) {
            robo.desfazerMovimento();
            robo.incrementarInvalidos();
            robo.getEstrategiaMovimento().registrarDirecaoInvalida(dir);
            throw new ColisaoComObstaculoException(dir.name(), "Robo");

        } else {
            Obstaculo obs = getObstaculoNaPosicao(robo.getNewX(), robo.getNewY());
            if (obs != null) {
                ResultadoBater resultado = obs.bater(robo);
                switch (resultado) {
                    case EXPLODIU -> {
                        robos.remove(robo);
                        obstaculos.remove(obs);
                        //robo.incrementarInvalidos(); ?
                    }
                    case VOLTOU -> {
                        robo.incrementarInvalidos();
                        robo.getEstrategiaMovimento().registrarDirecaoInvalida(dir);
                        throw new ColisaoComObstaculoException(dir.name(), obs.getClass().getSimpleName());
                    }
                }
            }
        }

        robo.incrementarValidos();
        robo.getEstrategiaMovimento().confirmarMovimento();
    }

    private boolean foraDoLimite(int x, int y) {
        return x < 0 || x >= tamanho || y < 0 || y >= tamanho;
    }

    private boolean temOutroRobo(Robo atual, int x, int y) {
        for (Robo r : robos) {
            if (r != atual && r.getNewX() == x && r.getNewY() == y) return true;
        }
        return false;
    }
    private boolean verificarEspacoLivre(int posicaoX, int posicaoY){
        for (Obstaculo obstaculo : obstaculos){
            if (obstaculo.getPosicaoX() == posicaoX && obstaculo.getPosicaoY() == posicaoY){
                return false;
            }
        }
        for (Robo robo : robos){
            if (robo.getNewX() == posicaoX && robo.getNewY() == posicaoY){
                return false;
            }
        }
        if (alimentoX == posicaoX && alimentoY == posicaoY)
            return false;

        return true;
    }

    private Robo getRoboNaPosicao(int x, int y) {
        for (Robo r : robos) {
            if (r.getNewX() == x && r.getNewY() == y) return r;
        }
        return null;
    }
    private Obstaculo getObstaculoNaPosicao(int posicaoX, int posicaoY){
        for (Obstaculo obstaculo : obstaculos){
            if (obstaculo.getPosicaoX() == posicaoX && obstaculo.getPosicaoY() == posicaoY)
                return obstaculo;
        }
        return null;
    }

    public void renderizar() {
        for (int y = tamanho - 1; y >= 0; y--){
            for (int x = 0; x < tamanho; x++) {
                Robo r = getRoboNaPosicao(x, y);
                Obstaculo obstaculo = getObstaculoNaPosicao(x, y);
                if (r != null) System.out.print(r.getCor().getAnsi() + " R " + Cor.RESET.getAnsi());
                else if (obstaculo != null){
                    if(obstaculo.getId() == 1) {
                        System.out.print(Cor.AMARELO.getAnsi() + " " + obstaculo.getInicial() + " " + Cor.RESET.getAnsi());
                    }else {
                        System.out.print(Cor.VERMELHO.getAnsi() + " " + obstaculo.getInicial() + " " + Cor.RESET.getAnsi());
                    }
                }
                else if (x == alimentoX && y == alimentoY) System.out.print(" A ");
                else System.out.print(" . ");
            }
            System.out.println();
        }
    }

    public void adicionarObstaculo(Obstaculo o){
        obstaculos.add(o);
    }

    public void colocarObstaculos(Dificuldade dificuldade){
       int quantidadeObstaculos = 0;
       Random escolha = new Random();
       switch (dificuldade){
           case FACIL ->
               quantidadeObstaculos = (int) (tamanho * tamanho * 0.05);
           case MEDIO ->
               quantidadeObstaculos = (int) (tamanho * tamanho * 0.10);
           case DIFICIL ->
               quantidadeObstaculos = (int) (tamanho * tamanho * 0.15);
       }

       int contador = 0;
       for (int i = 0; i < quantidadeObstaculos; i++){
           boolean espacoLivre = false;
           int posicaoX = 0;
           int posicaoY = 0;
           while(!espacoLivre) {
               posicaoX = escolha.nextInt(tamanho);
               posicaoY = escolha.nextInt(tamanho);
               espacoLivre = verificarEspacoLivre(posicaoX, posicaoY);
           }
           if(escolha.nextBoolean()){
               obstaculos.add(new Bomba(contador, posicaoX, posicaoY));
           }else{
              obstaculos.add(new Pedra(contador, posicaoX, posicaoY));
           }
           contador++;
       }
    }

    public boolean verificarAlimento(Robo robo) {
        return robo.getNewX() == alimentoX && robo.getNewY() == alimentoY;
    }

    public void adicionarRobo(Robo robo) { robos.add(robo); }
    public void setAlimento(int x, int y) { this.alimentoX = x; this.alimentoY = y; }
    public int getTamanho()    { return tamanho; }
    public List<Robo> getRobos() { return robos; }
    public List<Obstaculo> getObstaculos() { return obstaculos;}
    public int getAlimentoX()  { return alimentoX; }
    public int getAlimentoY()  { return alimentoY; }
}