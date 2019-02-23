/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coresPkg;
import java.awt.image.*;
import java.awt.Color;
import java.util.*;
/**
 *
 * @author joao
 *
 */
public class Reconhecimento {
    
    BufferedImage imagem;
    Leitor localLeitura;
    
    /**
     *Instancia classe atraves de imagem com corte padrao, no meio como lado 4
     * 
     * @param img Imagem que será cortada
     */
    public Reconhecimento(BufferedImage img){
        this.localLeitura = new Leitor((img.getWidth()/2-2),(img.getHeight()/2-2),4);
        this.imagem = img;
    }
    
    /**
     *Instancia classe atraves de imagem com corte personalizado
     * 
     * @param img Imagem que será cortada
     * @param posX Posicao no eixo X de inicio do leitor
     * @param posY Posicao no eixo Y de inicio do leitor
     * @param tamanho Quantidade de pixels do lado do quadrado cortado
     */
    public Reconhecimento(BufferedImage img, int posX, int posY, int tamanho){
        this.localLeitura = new Leitor(posX,posY,tamanho);
        this.imagem = img;
    }
    
    public void setImagem(BufferedImage img){
        this.imagem = img;
    }
    
    public void setLeitor(int posX, int posY, int tamanho){
        this.localLeitura = new Leitor(posX,posY,tamanho);
    }
    
    /**
     * Copia pixels da imagem para arranjo, separando-os um por um
     * 
     * @param img Imagem para separacao dos pixels
     * @return int [][][] Arranjo com todos os pixels da imagem. Sendo suas dimensoes altura, largura e canais de cor.
     */
    private static int [][][] copyImage(BufferedImage img){
        int x,y,width,height;
        int imagem[][][]=null;
        width=img.getWidth();
        height=img.getHeight();
        imagem=new int[height][width][4];
        Color c=null;
        int temp;
        for(y=0;y<height;++y)
        {
            for(x=0;x<width;++x)
            {
                temp=img.getRGB(x, y);
                imagem[y][x][0]=(temp>>24) & 0xFF;
                c=new Color(temp);
                imagem[y][x][1]=c.getRed();
                imagem[y][x][2]=c.getGreen();
                imagem[y][x][3]=c.getBlue();
            }
        }
        return imagem;
    }
    
    /**
     * Le a Imagem na area definida pelo Leitor e agrupa a cor predominante
     * 
     * @return int O hash code da cor predominante em RGB
     */
    public int execLeitura(){
        /*//<editor-fold defaultstate="collapsed" desc="código anterior para obtenção de cores, funciona, porém é extremamente sucetível a ruídos">
        double a = 0;
        double r = 0;
        double g = 0;
        double b = 0;
        for (int i = 0; i < leitura.getHeight(); i++) {
            for (int j = 0; j < leitura.getWidth(); j++) {
                int rgb = leitura.getRGB(j, i);
                a += (rgb >> 24) & 0xFF;
                r += (rgb >> 16) & 0xFF;
                g += (rgb >> 8) & 0xFF;
                b += (rgb) & 0xFF;
            }
        }
        
        int alpha = (int)(a/(leitura.getWidth()*leitura.getHeight()));
        int red = (int)(r/(leitura.getWidth()*leitura.getHeight()));
        int green = (int)(g/(leitura.getWidth()*leitura.getHeight()));
        int blue = (int)(b/(leitura.getWidth()*leitura.getHeight()));
        Color resultado = new Color(red,green,blue,alpha);
        return resultado.getRGB();*/
        //</editor-fold>
            
        //<editor-fold defaultstate="collapsed" desc="Código de clusterização, originalmente, em maximização de expectativas.">
        
        /*int imagemArray[][][] = copyImage(leitura);
        int clusters[][]=null;
        double distance[][]=null;
        int arrParticao[][]=null;
        Random random = null;
        
        int i,j,k,l,m,c,w;
        int ccount = 1;
        double itr,num,den,sum=0.0,variance=0.0;
        itr= 50.0;
        //Temos 10 grupos, porém só há 1 disponível o algoritmo usa outros grupos de acordo com a variação dos seus itens.
        clusters=new int[10][4];
        distance=new double[imagemArray.length*imagemArray[0].length][10];
        arrParticao=new int[imagemArray.length*imagemArray[0].length][10];
        for(i=0;i<10;i++)
        {
            for(j=0;j<4;j++)
            {
                random=new Random();
                clusters[i][j]=random.nextInt(256);
            }
        }
        
        l=imagemArray.length*imagemArray[0].length;
        w=imagemArray[0].length;
        
        for(k=0;k<itr;k++)
        {
            if (variance==0.0) {
                for(i=0;i<ccount;i++)
                {
                    for(j=0;j<4;j++)
                    {
                        random=new Random();
                        clusters[i][j]=random.nextInt(256);
                    }
                }
            }
            for(i=0;i<l;i++)
            {
                for(j=0;j<ccount;j++)
                {   
                    distance[i][j]=     Math.sqrt(Math.pow(imagemArray[i/w][i%w][0]-clusters[j][0], 2)
                        +   Math.pow(imagemArray[i/w][i%w][1]-clusters[j][1], 2)
                        +   Math.pow(imagemArray[i/w][i%w][2]-clusters[j][2], 2)
                        +   Math.pow(imagemArray[i/w][i%w][3]-clusters[j][3], 2));
                }
            }
            
            //Iterações para determinar qual ponto pertênce a qual conjunto
            for(i=0;i<l;i++)
            {   
                int obj = 0;
                //problema de atribuição
                for(j=1;j<ccount;j++)
                {
                    if (distance[i][obj]>distance[i][j]) {
                        obj=j;
                    }
                }
                for(j=0;j<ccount;j++)
                {
                    arrParticao[i][j]=0;
                }
                arrParticao[i][obj]=1;
            }
            
            //Iterações para definir modelo do cluster
            for(j=0;j<ccount;j++)
            {
                for(m=0;m<4;m++)
                {
                    num=0.0;
                    den=0.0;
                    for(i=0;i<l;i++)
                    {
                        num+=imagemArray[i/w][i%w][m]*arrParticao[i][j];
                        den+=arrParticao[i][j];
                    }
                    clusters[j][m]=(int)Math.floor(num/den);
                }
            }
            
            //iteração para determinar variância do conjunto
            for(j=0;j<ccount;j++)
            {
                c = 0;
                sum = 0.0;
                for(i=0;i<l;i++)
                {   
                    c++;
                    sum +=   Math.sqrt(Math.pow(imagemArray[i/w][i%w][0]-clusters[j][0], 2)
                        +   Math.pow(imagemArray[i/w][i%w][1]-clusters[j][1], 2)
                        +   Math.pow(imagemArray[i/w][i%w][2]-clusters[j][2], 2)
                        +   Math.pow(imagemArray[i/w][i%w][3]-clusters[j][3], 2))*arrParticao[i][j];
                }
                if ((sum/c)>variance) {
                    variance = sum/c;
                }
            }
            if(variance>50.0 && ccount<10){
                ccount++;
                variance = 0.0;
            }
        }
        int maiorCluster = 0;
        int tamanhoMaiorCluster = 0;
        for (j = 0; j < ccount; j++) {
            c=0;
            for (i = 0; i < l; i++) {
                c += arrParticao[i][j];
            }
            if (c>tamanhoMaiorCluster) {
                maiorCluster = j;
                tamanhoMaiorCluster = c;
            }
        }*/
        //</editor-fold>
        
        BufferedImage leitura = imagem.getSubimage(localLeitura.inicioX, localLeitura.inicioY ,localLeitura.tamanho, localLeitura.tamanho);
        
        int[][][] arrImagem = Reconhecimento.copyImage(leitura);
        int[] alpha = new int[256];
        int[] red = new int[256];
        int[] green = new int[256];
        int[] blue = new int[256];
        
        for (int[][] imagem1 : arrImagem) {
            for (int[] item : imagem1) {
                alpha[item[0]]++;
                red[item[1]]++;
                green[item[2]]++;
                blue[item[3]]++;
            }
        }
        int[] corAbundante = new int[4];
        
        for (int i = 1; i < 256; i++) {
            if (alpha[i]>alpha[corAbundante[0]])
                corAbundante[0] = i;
            if (red[i]>red[corAbundante[1]])
                corAbundante[1] = i;
            if (green[i]>green[corAbundante[2]])
                corAbundante[2] = i;
            if (blue[i]>blue[corAbundante[3]])
                corAbundante[3] = i;
        }
        
        Color resultado = new Color(corAbundante[1],corAbundante[2],corAbundante[3],corAbundante[0]);
        
        return resultado.getRGB();
    }
}
