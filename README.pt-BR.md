<p align="right">
  🌎 <strong>Idioma:</strong> <a href="README.md">🇦🇺 English</a> | <a href="README.pt-BR.md">🇧🇷 Português (BR)</a>
</p>

# TorchEquip

**Um plugin leve e sem peso extra criado especificamente para servidores Minecraft Java 26.3 que rodam GeyserMC.**  
**Projetado para eliminar a limitação da mão secundária entre Java e Bedrock Edition, permitindo que jogadores de PC, Celular e Console equipem qualquer tocha ou lanterna no slot do escudo.**  
**Funciona direto ao ponto sem comandos, sem permissões e com suporte completo tanto pelo menu do inventário quanto pela troca rápida de mão.**

[Releases](../../releases) • [GeyserMC](https://geysermc.org) • [Reportar Problema](../../issues)

---

## O que é o TorchEquip?
O **TorchEquip** é um plugin direto e sem peso extra criado para servidores **Minecraft Java 26.3** que utilizam o **GeyserMC**.

Por padrão, o Minecraft Bedrock Edition impede que os jogadores coloquem tochas e lanternas no slot da mão secundária (slot do escudo) através do menu do inventário. O TorchEquip remove essa limitação de forma transparente, oferecendo aos jogadores de Celular, Console e Windows Bedrock a mesma liberdade da edição Java — sem adicionar comandos, permissões ou arquivos de configuração pesados.

---

## Suporte Atual

| Plataforma | Versões Suportadas | Status |
| :--- | :--- | :--- |
| **Minecraft: Java Edition** | `26.3` | ✅ Totalmente Suportado |
| **Minecraft: Bedrock Edition** (via Geyser) | Versões recentes do Bedrock | ✅ Totalmente Suportado |
| **Software do Servidor** | Paper, Purpur, Folia `26.3` | ✅ Testado e Verificado |

---

## Recursos e Mecânicas
- **Integração Nativa com GeyserMC:** Intercepta os cliques do inventário Bedrock de forma limpa para que jogadores no celular e console equipem e desequiperem tochas e lanternas como se fossem um escudo.
- **Equipar pelo Menu do Inventário (PC e Celular):** Abra o inventário, selecione qualquer tocha ou lanterna suportada e mova ou toque diretamente no slot do escudo ao lado do personagem.
- **Troca Rápida Instantânea:** Segure uma tocha ou lanterna na mão principal e aperte `F` (Java Edition) ou acione a troca de mão (Bedrock/Geyser) para equipar na hora.
- **Tochas e Lanternas Suportadas:**
  - **Tochas:** `TORCH`, `SOUL_TORCH`, `REDSTONE_TORCH` e `COPPER_TORCH`.
  - **Lanternas:** `LANTERN`, `SOUL_LANTERN` e `COPPER_LANTERN`.
- **Arquitetura Plug-and-Play:** Nenhum comando para decorar, nenhuma permissão para configurar e impacto praticamente zero no desempenho (TPS) do servidor.

---

## Instalação e Configuração
**1. Requisitos:** Certifique-se de que seu servidor está rodando **Paper**, **Purpur** ou **Folia** no **Minecraft 26.3** com **Java 25** ou superior, junto com o [GeyserMC](https://geysermc.org) para o crossplay Bedrock.  
**2. Download:** Acesse a aba [**Releases**](../../releases) deste repositório e baixe o arquivo `TorchEquip.jar`.  
**3. Instalação:** Coloque o `TorchEquip.jar` dentro da pasta `/plugins/` do seu servidor.  
**4. Inicialização:** Reinicie o servidor. O plugin é ativado automaticamente sem precisar configurar nada.

---

## Como Usar
**Pela Tela do Inventário (Java e Bedrock/Celular):**  
**1.** Abra o inventário do personagem.  
**2.** Selecione ou toque em uma tocha ou lanterna no seu inventário.  
**3.** Clique ou toque diretamente no **slot da mão secundária (escudo)** para equipar ou desequipar.

**Pela Troca Rápida:**  
**Java Edition:** Segure uma tocha ou lanterna na mão principal e aperte `F`.  
**Bedrock Edition:** Segure uma tocha ou lanterna e utilize o botão/emote configurado de troca de mão do Geyser.

---

## Compilando pelo Código-Fonte
Se você preferir compilar o `TorchEquip.jar` manualmente em vez de baixar pela aba Releases:

**1.** Clone este repositório:
```bash
git clone https://github.com
cd TorchEquip
```
**2.** Compile com o Maven (requer **JDK 25**):
```bash
mvn clean package
```
**3.** O arquivo compilado estará em `target/TorchEquip.jar`.

---

## Links e Créditos
**Autor:** Ycaro Rodrigues ([YcaroEngine](https://github.com))  
**Repositório:** [YcaroEngine/TorchEquip](https://github.com/TorchEquip)  
**Ponte Crossplay:** [Site Oficial do GeyserMC](https://geysermc.org)
