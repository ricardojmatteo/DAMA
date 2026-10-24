# Evidencias de TDD

## Objetivo e escopo

Este documento registra o que o historico Git permite afirmar sobre desenvolvimento orientado por testes (TDD) no repositorio DAMA. A verificacao foi feita em 23 de outubro de 2026, considerando todas as referencias locais (`git log --all`), com foco em `alert-service` e `frontend`.

Para caracterizar uma evidencia de TDD, deve existir um commit que introduza ou altere um teste **antes** do commit que introduz ou altera a implementacao correspondente. A simples existencia de testes nao prova, por si so, a ordem Red-Green-Refactor.

## Resultado da analise

| Modulo | Evidencia encontrada | Conclusao sobre TDD |
| --- | --- | --- |
| `alert-service` | Os arquivos `src/regras.js`, `src/server.js`, `test/regras.test.js` e `test/server.test.js` foram adicionados juntos no commit `3156837` (`Node e afins`, 02/10/2026). A linha paralela contem a mesma inclusao conjunta no commit `c318ca5` (`Integracao Node e Correcao EstatMult`, 02/10/2026). | Ha testes automatizados de regras e HTTP, mas nao ha um commit de teste anterior ao codigo que comprove TDD. |
| `frontend` | O historico registra implementacao e alteracoes da interface, mas nao ha arquivos de teste nem script `test` no `package.json` atual. | Nao ha evidencia de TDD nem testes automatizados do frontend neste recorte. |

Os dois commits do `alert-service` pertencem a ramos com ancestrais distintos. Eles nao devem ser apresentados como uma sequencia teste-primeiro: em ambos, teste e implementacao entram no mesmo commit.

## Evidencias reproduziveis

Na raiz do repositorio, os comandos abaixo permitem conferir os resultados:

```bash
git log --all --date=iso-strict --name-status -- alert-service/src alert-service/test
git show --format='%H%n%P%n%aI%n%s' --name-status 3156837 -- alert-service/src alert-service/test
git show --format='%H%n%P%n%aI%n%s' --name-status c318ca5 -- alert-service/src alert-service/test
git log --all --name-status -- frontend
```

## Testes existentes

O `alert-service` usa o executor nativo do Node.js e pode ser validado sem instalar dependencias externas:

```bash
cd alert-service
npm test
```

Os testes cobrem regras de negocio, limites, validacao de entrada, rota de saude e o endpoint HTTP `/avaliar`. O `frontend` ainda precisa ganhar testes antes que sua etapa de qualidade possa ser considerada completa.

## Proximo ciclo de TDD

Para que uma proxima funcionalidade gere evidencia verificavel de TDD, o time deve: criar um teste que falhe e fazer um commit somente desse teste; implementar o minimo para o teste passar em um segundo commit; e, se necessario, refatorar em um terceiro commit. Os tres commits devem referenciar a mesma historia ou regra de negocio.
