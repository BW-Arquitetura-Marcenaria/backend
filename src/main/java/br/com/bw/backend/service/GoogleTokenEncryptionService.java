package br.com.bw.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class GoogleTokenEncryptionService {

    private static final String ALGORITMO = "AES/GCM/NoPadding";
    private static final int TAMANHO_IV = 12;
    private static final int TAMANHO_TAG_BITS = 128;
    private static final int TAMANHO_CHAVE_BYTES = 32;

    private final SecretKeySpec chave;
    private final SecureRandom secureRandom = new SecureRandom();

    public GoogleTokenEncryptionService(
            @Value("${integracoes.google.calendar.encryption-key}")
            String chaveBase64
    ) {
        byte[] bytesChave;

        try {
            bytesChave = Base64.getDecoder().decode(chaveBase64);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "A chave de criptografia não está em Base64 válido.", e
            );
        }

        if (bytesChave.length != TAMANHO_CHAVE_BYTES) {
            throw new IllegalStateException(
                    "A chave de criptografia deve conter exatamente 32 bytes."
            );
        }

        this.chave = new SecretKeySpec(bytesChave, "AES");
    }

    public String encrypt(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    "O token não pode ser vazio."
            );
        }

        try {
            byte[] iv = new byte[TAMANHO_IV];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    chave,
                    new GCMParameterSpec(TAMANHO_TAG_BITS, iv)
            );

            byte[] textoCriptografado = cipher.doFinal(
                    texto.getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );

            ByteBuffer buffer = ByteBuffer.allocate(
                    iv.length + textoCriptografado.length
            );

            buffer.put(iv);
            buffer.put(textoCriptografado);

            return Base64.getEncoder().encodeToString(buffer.array());

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Não foi possível criptografar o token do Google.", e
            );
        }
    }

    public String decrypt(String textoCriptografado) {
        try {
            byte[] dados = Base64.getDecoder().decode(textoCriptografado);

            if (dados.length <= TAMANHO_IV) {
                throw new IllegalArgumentException(
                        "Dados criptografados inválidos."
                );
            }

            ByteBuffer buffer = ByteBuffer.wrap(dados);

            byte[] iv = new byte[TAMANHO_IV];
            buffer.get(iv);

            byte[] conteudo = new byte[buffer.remaining()];
            buffer.get(conteudo);

            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    chave,
                    new GCMParameterSpec(TAMANHO_TAG_BITS, iv)
            );

            byte[] textoOriginal = cipher.doFinal(conteudo);

            return new String(
                    textoOriginal,
                    java.nio.charset.StandardCharsets.UTF_8
            );

        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new IllegalStateException(
                    "Não foi possível descriptografar o token do Google.", e
            );
        }
    }
}
