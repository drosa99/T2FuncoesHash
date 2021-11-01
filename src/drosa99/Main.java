package drosa99;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Autora: Daniela Amaral
 * Trabalho 2 da cadeira de Segurança de Sistemas, dado um video passado por parametro, este é dividido em arrays de 1024 bytes,
 * é calculado o Hash em SHA-256 do ultimo bloco, este hash é concatenado ao final do penultimo bloco, é gerado hash deste novo bloco e concatenado no antepenultimo bloco
 * este processo é repetido até chegar ao primeiro bloco do array de bytes do video.
 * <p>
 * Para execução: compilar esta classe com Java 8+ e executar, seguir orientações no console
 * Video com demonstração: https://youtu.be/6CpmcyzVxwY
 */
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
        //os videos a serem lidos encontram-se na pasta "files"
        //se nao for escolhido o numero 2, sera executado o video 'FuncoesResumo - SHA1.mp4'
        Scanner scanner = new Scanner(System.in);
        String nomeVideo = "FuncoesResumo - SHA1.mp4";

        System.out.println("\n Digite o numero do video que deseja gerar hash ou digite 'fim' para encerrar: \n 1- FuncoesResumo - SHA1.mp4 \n 2- FuncoesResumo - Hash Functions.mp4");

        String mens = scanner.nextLine();
        if (mens.equals("fim")) {
            System.exit(1);
        } else if (mens.equals("2")) nomeVideo = "FuncoesResumo - Hash Functions.mp4";

        byte[] video = leituraArquivo(nomeVideo);
        byte[][] blocos = preencherBlocos(video);
        buscarH0(blocos, video.length);

    }

    //metodo que printa o H0, parte do ultimo bloco do video, gera o hash, concatena esse hash no final do bloco anterior,
    // gera o hash, concatena.. até chegar no primeiro bloco de bytes
    private static void buscarH0(byte[][] blocos, int length) {
        int qtdBlocos = blocos.length;
        byte[][] blocosHash = new byte[qtdBlocos][];

        byte[] ultimoBloco = Arrays.copyOfRange(blocos[qtdBlocos - 1], 0, length % 1024);
        blocosHash[qtdBlocos - 1] = gerarHash(ultimoBloco); //gera hash do ultimo bloco

        //parte do penultimo bloco ate o primeiro, gerando hash e concatenando no bloco anterior
        for (int i = qtdBlocos - 2; i >= 0; i--) {
            byte[] blocoAHashear = concatenarArrays(blocos[i], blocosHash[i + 1]);
            blocosHash[i] = gerarHash(blocoAHashear);
        }
        StringBuilder sb = new StringBuilder();

        //transforma o hash de array de bytes para hexadecimal
        for (int i = 0; i < blocosHash[0].length; i++) {
            sb.append(Integer.toString((blocosHash[0][i] & 0xff) + 0x100, 16).substring(1));

        }
        System.out.println("Digest(in hex format):: " + sb.toString());


    }

    //metodo auxiliar para concatenar 2 arrays de bytes
    private static byte[] concatenarArrays(byte[] a, byte[] b) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(a);
            outputStream.write(b);
        } catch (IOException e) {
            System.out.println("Erro ao concatenar arrays");
            System.exit(1);
        }
        return outputStream.toByteArray();
    }

    //metodo para gerar hash de um array de bytes utilizando a biblioteca MessageDigest
    private static byte[] gerarHash(byte[] bloco) {
        return md.digest(bloco);
    }

    //separa o video em uma matriz de bytes, cada bloco tem 1024 bytes, o ultimo bloco pode ser menor se o tamanho do video nao for multiplo de 1024
    private static byte[][] preencherBlocos(byte[] video) {
        int tamBloco = 1024;
        int ultimoIndex = 0;

        int qtdBlocos = video.length % tamBloco == 0 ? video.length / tamBloco : (video.length / tamBloco + 1);
        byte[][] blocos = new byte[qtdBlocos][1024];

        for (int i = 0; i < qtdBlocos; i++) {
            for (int j = 0; j < tamBloco && ultimoIndex < video.length; j++) {
                blocos[i][j] = video[ultimoIndex];
                ultimoIndex++;
            }
        }
        return blocos;
    }

    //metodo auxiliar para ler o arquivo e colocar em array de bytes
    private static byte[] leituraArquivo(String filename) {
        File f = new File("files/" + filename);
        try {
            return Files.readAllBytes(f.toPath());
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo " + filename);
            System.exit(1);
        }
        return new byte[0];
    }
}
