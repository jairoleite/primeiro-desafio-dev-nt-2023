# Exemplos de requests
# curl -v -XPOST -H "content-type: application/json" -d '{"apelido" : "xpto", "nome" : "xpto xpto", "nascimento" : "2000-01-01", "stack": null}' "http://localhost:9999/pessoas"
# curl -v -XGET "http://localhost:9999/pessoas/1"
# curl -v -XGET "http://localhost:9999/pessoas?t=xpto"
# curl -v "http://localhost:9999/contagem-pessoas"

# altere o caminho para sua máquina
GATLING_BIN_DIR=$HOME/Documents/tools/gatling/bin

# altere o caminho para sua máquina
WORKSPACE=$HOME/Documents/desafios/primeiro-desafio-dev-nt-2023/stress-teste

sh $GATLING_BIN_DIR/gatling.sh -rm local -s GatlingSimulation \
    -rd "DESCRICAO" \
    -rf $WORKSPACE/arquivo-usuario/results \
    -sf $WORKSPACE/arquivo-usuario/simulations \
    -rsf $WORKSPACE/arquivo-usuario/resources \

sleep 3

curl -v "http://localhost:9999/contagem-pessoas"