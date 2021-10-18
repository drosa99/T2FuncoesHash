package drosa99;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Main {

    private static MessageDigest md;

    static {
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        byte[] video = leituraArquivo("FuncoesResumo - SHA1.mp4");
        byte[][] blocos = preencherBlocos(video);
        buscarH0(blocos, video.length);

    }

    private static void buscarH0(byte[][] blocos, int length){
        int qtdBlocos = blocos.length;
        byte[][] blocosHash = new byte[qtdBlocos][];

        byte[] ultimoBloco = Arrays.copyOfRange(blocos[qtdBlocos - 1], 0, length % 1024);
        blocosHash[qtdBlocos-1] = gerarHash(ultimoBloco);

        for (int i = qtdBlocos - 2; i >= 0 ; i--) {
            byte[] blocoAHashear = concatenarArrays(blocos[i], blocosHash[i + 1]);
            blocosHash[i] = gerarHash(blocoAHashear);
        }
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < blocosHash[0].length; i++) {
            sb.append(Integer.toString((blocosHash[0][i] & 0xff) + 0x100, 16).substring(1));

        }
        System.out.println("Digest(in hex format):: " + sb.toString());


    }

    private static byte[] concatenarArrays(byte[] a, byte[] b){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try {
            outputStream.write( a );
            outputStream.write( b );
        } catch (IOException e) {
            System.out.println("Erro ao concatenar arrays");
            System.exit(1);
        }
        return outputStream.toByteArray();
    }


    private static byte[] gerarHash(byte[] bloco) {
        return md.digest(bloco);
    }

    private static byte[][] preencherBlocos(byte[] video){
        int tamBloco = 1024;
        int ultimoIndex = 0;

        int qtdBlocos = video.length % tamBloco == 0 ? video.length / tamBloco : (video.length / tamBloco + 1);
        byte[][] blocos = new byte[qtdBlocos][1024];

        for (int i = 0; i < qtdBlocos; i++) {
            for (int j = 0; j < tamBloco && ultimoIndex < video.length; j++) {
              blocos[i][j] = video[ultimoIndex];
              ultimoIndex ++;
            }
        }
        return blocos;
    }

    private static byte[] leituraArquivo(String filename) {
        File f = new File("files/"  + filename);
        try {
            return Files.readAllBytes(f.toPath());
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo");
            System.exit(1);
        }
        return new byte[0];
    }
}
