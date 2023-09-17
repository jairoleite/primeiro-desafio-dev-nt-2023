# Exemplos de requests
# curl -v -XPOST -H "content-type: application/json" -d '{"apelido" : "xpto", "nome" : "xpto xpto", "nascimento" : "2000-01-01", "stack": null}' "http://localhost:9999/pessoas"
# curl -v -XGET "http://localhost:9999/pessoas/1"
# curl -v -XGET "http://localhost:9999/pessoas?t=xpto"
# curl -v "http://localhost:9999/contagem-pessoas"

# altere o caminho para sua máquina
GATLING_BIN_DIR=$HOME/Documents/tools/gatling/bin

echo "${GATLING_BIN_DIR}"

WORKSPACE=$(pwd)

sh $GATLING_BIN_DIR/gatling.sh -rm local -s DesafioDevNtSimulacao \
    -rd "Desafio dev's NT" \
    -rf ${WORKSPACE}/arquivo-usuario/resultado \
    -sf ${WORKSPACE}/arquivo-usuario/simulacao \
    -rsf ${WORKSPACE}/arquivo-usuario/dados \

sleep 3

COUNT=$(curl -fsSL "http://localhost:9999/contagem-pessoas")
echo "Quantidade de pessoas cadastradas ==> ${COUNT}"