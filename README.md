# Espelho Carro (etapa 1)

App de mídia "de mentira" no Android Auto (versão 0.2): a lista de apps aparece como itens de música. Você marca no celular quais apps quer (iFood, inDrive...),
eles aparecem numa lista na tela do carro, e tocar em um abre o app no celular.
Ainda **não** espelha a imagem (etapas 3 e 4).

## Como gerar o APK

### Opção A: GitHub Actions (sem Android Studio, de graça)
1. Crie uma conta e um repositório novo no github.com.
2. Extraia o zip e envie o **conteúdo** da pasta (não a pasta por fora) para o repositório:
   `app`, `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, `.gitignore`, `README.md`.
3. A pasta `.github` (nome começa com ponto) costuma ficar de fora no upload. Se não aparecer no repositório,
   use **Add file → Create new file**, digite `.github/workflows/build.yml` no nome e cole o conteúdo
   do arquivo `.github/workflows/build.yml` deste zip.
4. Abra a aba **Actions**, escolha **Build APK** e toque em **Run workflow** (ou faça qualquer commit).
5. Quando terminar (5 a 10 min) e ficar verde, abra a execução e baixe **espelho-carro-apk**
   (um .zip com o `app-debug.apk`) em **Artifacts**.

### Opção B: Android Studio
1. Abra esta pasta no Android Studio e aguarde o Gradle sincronizar.
2. Menu **Build → Build APK(s)**. O arquivo fica em `app/build/outputs/apk/debug/app-debug.apk`.

## Como instalar e testar
1. Instale o APK no celular (permita instalar de fontes desconhecidas).
2. Abra **Espelho Carro**, toque em **1) Permitir abrir apps pelo carro** e ative a permissão.
3. Marque os apps que quer ver no carro.
4. No Android Auto do celular, ative **Fontes desconhecidas** nas configurações de desenvolvedor.
5. Conecte na tela do carro. Abra a lista de apps do Android Auto e procure **Espelho Carro** (aparece junto dos apps de música, como o Fermata).
6. Teste com o carro parado: toque em um app da lista e veja se ele abre no celular.

## Pontos de atenção
- Este projeto ainda não foi compilado nem testado. Se o build falhar, copie a mensagem de erro e envie.
- O Android pode bloquear a abertura do app em segundo plano. Se tocar na lista não abrir nada,
  anote a mensagem que aparece na tela do carro.
- O workflow não usa `setup-android`: o runner do GitHub já traz o Android SDK, e esse passo falhava
  ao tentar instalar o pacote `tools`.
