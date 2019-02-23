/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coresPkg;
/**
 *
 * @author joao
 */
public class Leitor {
    int tamanho;
    int inicioX;
    int inicioY;
    
    public Leitor(int posX, int posY ,int length){
        this.tamanho = length;
        this.inicioX = posX;
        this.inicioY = posY;
    }
}
