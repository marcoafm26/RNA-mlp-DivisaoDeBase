package app;

import domain.MLP;

import java.io.*;
import java.util.*;

import static java.util.Collections.shuffle;

public class MlpRunner {

    private static class Amostra{
       double[] in;
       double[] out;
    }


    public static List<Amostra> list_8 = new LinkedList<>();
    public static List<Amostra> list_9 = new LinkedList<>();
    public static List<Amostra> list_10 = new LinkedList<>();



    //base and
//    private static double[][][] base = new double[][][]{
//            {{0,0},{0}},
//            {{0,1},{0}},
//            {{1,0},{0}},
//            {{1,1},{1}},
//    };

    // base Xor
    private static final double[][][] base = {
            { {0,0}, {0} },
            { {0,1}, {1} },
            { {1,0}, {1} },
            { {1,1}, {0} }
    };

    // base Or
//    private static final double[][][] base = {
//            { { 0, 0}, {0} },
//            { { 0, 1}, { 1} },
//            { { 1, 0 }, { 1} },
//            { { 1, 1 }, { 1} }
//    };

    //Robo
//    private static final double[][][] base = {
//            { { 0, 0, 0 }, { 1, 1 } },
//            { { 0, 0, 1 }, { 0, 1 } },
//            { { 0, 1, 0 }, { 1, 0 } },
//            { { 0, 1, 1},  { 0, 1 } },
//            { { 1, 0, 0 }, { 1, 0 } },
//            { { 1, 0, 1 }, { 1, 0 } },
//            { { 1, 1, 0 }, { 1, 0 } },
//            { { 1, 1, 1 }, { 1, 0 } }
//    };

    public static void main(String[] args) throws FileNotFoundException {
        
        final int QTD_IN = 7;
        final int QTD_OUT= 3;
        final int QTD_INTER= 5;
        final double U = 0.01;
        final int EPOCA = 20000;

        MLP p = new MLP(QTD_IN,QTD_OUT,QTD_INTER,U);
        
        double erroEpLearn = 0;
        double erroAmLearn = 0;

        double erroEpTest = 0;
        double erroAmTest = 0;

        dataReader();
        double base[][][][] = joinBase();

        for (int e = 0; e < EPOCA; e++) {


            erroEpLearn = 0;

            // utiliza a base para aprendizado
            for (int a = 0; a < base[0].length; a++) {
                double[] x = base[0][a][0];
                double[] y = base[0][a][1];
                double[] out = p.learn(x,y);
                erroAmLearn = somaErroAmostra(y,out);
                erroEpLearn += erroAmLearn;
            }
            erroEpTest = 0;
            //  testa a base de teste
            for (int a = 0; a < base[1].length; a++) {
                double[] x = base[1][a][0];
                double[] y = base[1][a][1];
                double[] out = p.test(x);
                erroAmTest = somaErroAmostra(y,out);
                erroEpTest += erroAmTest;
            }
               imprimir(erroEpLearn,e);
        }
    }

    public static double somaErroAmostra(double[] y, double[] out){
        double soma=0;
        for (int i = 0; i < y.length; i++) {
            soma+=Math.abs(y[i]-out[i]);
        }
        return soma;
    }

    public static double[][][][] joinBase(){
        double baseDivididaTotal[][][][]= new double[2][][][];
        double aux[][][][];

        // base[0][][][] = base de aprendizado
        // base[1][][][] = base de teste

        //junta as bases divididas da lista 8
        aux = divideBase(list_8);
        baseDivididaTotal[0] = aux[0];
        baseDivididaTotal[1] = aux[1];

        //junta as bases divididas da lista 9
        aux = divideBase(list_9);
        baseDivididaTotal[0] = concatenar(baseDivididaTotal[0],aux[0]);
        baseDivididaTotal[1] = concatenar(baseDivididaTotal[1],aux[1]);

        //junta as bases divididas da lista 10
        aux = divideBase(list_10);
        baseDivididaTotal[0] = concatenar(baseDivididaTotal[0],aux[0]);
        baseDivididaTotal[1] = concatenar(baseDivididaTotal[1],aux[1]);

   //     baseDivididaTotal[0] = embaralhar(baseDivididaTotal[0]);
   //     baseDivididaTotal[1] = embaralhar(baseDivididaTotal[1]);
        return baseDivididaTotal;
    }
    public static double[][][][] divideBase(List<Amostra> base){
        List<Amostra> baseTeste = new LinkedList<>();
        baseTeste.addAll(base);
        double[][][][] basesDivididas;
        basesDivididas = new double[2][][][];

        shuffle(baseTeste);
        int learn = baseTeste.size()*3/4;
        int test = baseTeste.size() * 1/4;

        if ((learn+test)< baseTeste.size())
            learn++;

        // preenchendo a base referente ao aprendizado
        basesDivididas[0] = new double [learn][2][] ;
        for (int i = 0; i < learn ; i++) {
            Amostra amostra = baseTeste.remove(0);
            basesDivididas[0][i][0] = amostra.in;
            basesDivididas[0][i][1] = amostra.out;
        }
     //   basesDivididas[0] = embaralhar(basesDivididas[0]);

        // preenchendo a base referente ao teste
        basesDivididas[1] = new double [test][2][] ;
        for (int i = 0; i < test ; i++) {
            Amostra amostra = baseTeste.remove(0);
            basesDivididas[1][i][0] = amostra.in;
            basesDivididas[1][i][1] = amostra.out;
        }
     //  basesDivididas[1] = embaralhar(basesDivididas[1]);

        return basesDivididas;
    }
    public static double[][][] embaralhar(double[][][] v) {
        Random random = new Random();
        for (int i=0; i < (v.length - 1); i++) {
            int j = random.nextInt(v.length);
            double[][] temp = v[i];
            v[i] = v[j];
            v[j] = temp;
        }
        return v;
    }
    public static double[][][] concatenar(double[][][] Array1, double[][][] Array2){
        int lenArray1 = Array1.length;
        int lenArray2 = Array2.length;
        double[][][] concate = new double[lenArray1 + lenArray2][][];
        System.arraycopy(Array1, 0, concate, 0, lenArray1);
        System.arraycopy(Array2, 0, concate, lenArray1, lenArray2);

        return concate;
    }
    public static void imprimir(double erroEp, int epoca){
        System.out.println("Epoca "+(epoca+1)+"   erro: "+erroEp);
    }
    public static double[][][] dataReader() throws FileNotFoundException {
        double[][][] base = new double[1891][2][];
        double[] data = new double[8];
        File file = new File("abalone.data");
        Scanner ler = new Scanner(file);
        double out = 0;

        int cont = 0;
        for (int i = 0; i < 4177; i++) {
            String linha = ler.nextLine();

            data = inFill(linha);

            if (data[7] == 8 || data[7] == 9 || data[7] ==10){
                base[cont][0] = new double[7];
                for (int j = 0; j < 7; j++) {
                    base[cont][0][j] = data[j];
                }
                out = data[7];
                double[] x = base[cont][0];
                double[] y;
                if(out == 8){
                    base[cont][1] = new double[]{0, 0, 0};
                    y = base[cont][1];
                    list_8.add(preencheListaTeste(list_8,x,y));
                } else if (out == 9) {
                    base[cont][1] = new double[]{0, 0, 1};
                    y = base[cont][1];
                    list_9.add(preencheListaTeste(list_9,x,y)) ;
                } else if (out == 10) {
                    base[cont][1] = new double[]{0, 1, 0};
                    y = base[cont][1];
                    list_10.add(preencheListaTeste(list_10,x,y));
                }
                cont++;
            }

        }
        return base;
    }

    public static Amostra preencheListaTeste(List<Amostra> listaTeste,double[] x, double[] y){

        Amostra amostra = new Amostra();
        amostra.in = x;
        amostra.out = y;

        return amostra;
    }
    public static double[] inFill(String linha){
        double[] entradas;
        linha = linha.replace("F,","");
        linha = linha.replace("M,","");
        linha = linha.replace("I,","");
        entradas = Arrays.stream(linha.substring(1, linha.length()).split(",")).map(String::trim).mapToDouble(Double::parseDouble).toArray();
        return entradas;
    }
//    public void dataWriter(String fileName) throws IOException {
//        FileWriter fw = new FileWriter("arquivo.txt");
//        PrintWriter printWriter = new PrintWriter(fw);
//        arqInvertido.entrySet().forEach(key->{
//            key.getValue().stream().sorted();
//            printWriter.println(key.getKey() +" "+key.getValue().toString());
//        });
//        printWriter.close();
//    }
}
