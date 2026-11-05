# Espelho Carro (etapa 1)

Menu de apps no Android Auto. Você marca no celular quais apps quer (iFood, inDrive...),
eles aparecem numa lista na tela do carro, e tocar em um abre o app no celular.
Ainda **não** espelha a imagem (etapas 3 e 4).

## Como gerar o APK

### Opção A: GitHub Actions (sem Android Studio, de graça)
1. Crie uma conta e um repositório novo no github.com.
2. Envie todos os arquivos desta pasta para o repositório (incluindo a pasta `.github`).
3. Abra a aba **Actions**, escolha **Build APK** e toque em **Run workflow**.
4. Quando terminar (uns 5 a 10 min), abra a execução e baixe **espelho-carro-apk** (é um .zip com o `app-debug.apk`).

### Opção B: Android Studio
1. Abra esta pasta no Android Studio e aguarde o Gradle sincronizar.
2. Menu **Build → Build APK(s)**. O arquivo fica em `app/build/outputs/apk/debug/app-debug.apk`.

## Como instalar e testar
1. Instale o APK no celular (permita instalar de fontes desconhecidas).
2. Abra **Espelho Carro**, toque em **1) Permitir abrir apps pelo carro** e ative a permissão.
3. Marque os apps que quer ver no carro.
4. No Android Auto do celular, ative **Fontes desconhecidas** nas configurações de desenvolvedor (você já fez).
5. Conecte na tela do carro. Abra a lista de apps do Android Auto e procure **Espelho Carro**.
6. Teste com o carro parado: toque em um app da lista e veja se ele abre no celular.

## Pontos de atenção
- Este projeto não foi compilado nem testado ainda. Se o build falhar, copie a mensagem de erro e me mande.
- O Android pode bloquear a abertura do app em segundo plano. Se tocar na lista não abrir nada, me diga a mensagem que aparece na tela do carro.
