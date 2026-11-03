# Termina a reorganizacao do Deliverable1: move arquivos e apaga pastas antigas.
# Nao faz commit nem acessa o GitHub. Apaga a si mesmo no final.
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot

$estat = '02_Estatistica_Multivariada'
New-Item -ItemType Directory -Force -Path "$estat\dados", '05_Machine_Learning', '07_Pesquisa_Operacional_e_Calculo_Aplicado', '08_Analise_Preditiva' | Out-Null

foreach ($f in 'employees', 'resumo_hipoteses', 'security_events', 'tabela_analitica_final', 'training_records') {
    $a = "EstatisticaMulti\$f.csv"; $b = "CSVsNecessarios\$f.csv"
    if ((Get-FileHash -LiteralPath $a).Hash -ne (Get-FileHash -LiteralPath $b).Hash) { throw "Arquivos diferentes: $f.csv. Nada foi apagado." }
}
foreach ($f in 'employees', 'resumo_hipoteses', 'security_events', 'tabela_analitica_final', 'training_records') {
    Remove-Item -LiteralPath "EstatisticaMulti\$f.csv"
}

function Mover($origem, $destino) {
    Get-ChildItem -LiteralPath $origem -Force | Move-Item -Destination $destino
    Remove-Item -LiteralPath $origem
}
Mover 'EstatisticaMulti' $estat
Mover 'CSVsNecessarios' "$estat\dados"
Mover 'machine-learning-ia' '05_Machine_Learning'
Mover 'PesquisaOperacional' '07_Pesquisa_Operacional_e_Calculo_Aplicado'
Mover 'AnalisePreditiva' '08_Analise_Preditiva'

Write-Host 'Pronto. Confira com git status.'
Remove-Item -LiteralPath $PSCommandPath
