

echo '***************************************************************************************'

echo 'Inicio setup ambiente BTAGS. '

read -p 'Digite o ID da Impressora (Código numérico): ' id_impressora_var
read -p 'Chave a seguranca: ' chave_impressora_var
echo

ssh-keygen -R btags.local

rm ./btags_pack/runner.sh

rm ./btags_pack/update.sh

cat ./btags_pack/run_unpack | sed -e "s/'ZEBRA' 'ID_IMPRESSORA' 'CHAVE_SEGURANCA'/'ZEBRA' '$id_impressora_var' '$chave_impressora_var'/g" >> ./btags_pack/runner.sh

cat ./btags_pack/update_unpack | sed -e "s/'ID_IMPRESSORA'/'$id_impressora_var'/g" >> ./btags_pack/update.sh

zip -vr btags_pack.zip btags_pack/ -x "*.DS_Store"

# transferencias de chaves para autenticacao, transferencia de pack de setup e instalacao do java

chmod 400 ./btags_pack/ssh/btags_rsa

#transfere chave publica pra autenticacao
ssh-copy-id -i ./btags_pack/ssh/btags_rsa.pub btags@btags.local

#tools iniciais
ssh -i ./btags_pack/ssh/btags_rsa btags@btags.local 'sudo apt update; sudo apt install openjdk-8-jdk'
ssh -i ./btags_pack/ssh/btags_rsa btags@btags.local 'sudo apt install vim'

#envia pack de setup pro device
scp -i ./btags_pack/ssh/btags_rsa ./btags_pack.zip btags@btags.local:/home/btags/Desktop
ssh -i ./btags_pack/ssh/btags_rsa btags@btags.local 'cd /home/btags/Desktop ; unzip btags_pack.zip ;  rm btags_pack.zip'

#atualiza executaveis de impressao
echo 'Executando update.sh...'
ssh -i ./btags_pack/ssh/btags_rsa btags@btags.local 'cd /home/btags/Desktop/btags_pack ; sudo chmod +x update.sh; ./update.sh'
ssh -i ./btags_pack/ssh/btags_rsa btags@btags.local 'cd /home/btags/Desktop/btags_pack ; sudo chmod +x runner.sh'

# setups complementares

# instalacao e configuracao do cups
ssh -i ./btags_pack/ssh/btags_rsa btags@btags.local  'sudo apt-get install cups'
ssh -i ./btags_pack/ssh/btags_rsa btags@btags.local  'sudo apt install lpr'
ssh -i ./btags_pack/ssh/btags_rsa btags@btags.local  'sudo apt install cups cups-bsd system-config-printer'
ssh -i ./btags_pack/ssh/btags_rsa btags@btags.local  'sudo cupsctl --remote-any'
ssh -i ./btags_pack/ssh/btags_rsa btags@btags.local  'sudo usermod -a -G lpadmin btags ;'

echo 'Desabilita blank screen...'
ssh -i ./btags_pack/ssh/btags_rsa btags@btags.local  'export DISPLAY=:0.0 ; xset s off; xset s noblank;'


# acessar admin da impressora https:btags.local:631/admin

echo 'Schedulando rotina btags no CRONTAB......'
ssh -i ./btags_pack/ssh/btags_rsa btags@btags.local 'cd /home/btags/Desktop/btags_pack; sudo crontab -l > mycron ; sudo echo "@reboot sleep 8 ; cd /home/btags/Desktop/btags_pack ; sudo /sbin/iw wlan0 set power_save off; ./update.sh ; ./runner.sh " >> mycron; sudo crontab mycron; sudo rm mycron;'

# iwconfig para checar configuracoes de rede
# sudo raspi-config para acessar seyup da BIOS

echo 'fim execucao. Reiniciando Device...'
echo '***************************************************************************************'

ssh -i ./btags_pack/ssh/btags_rsa btags@btags.local  'sudo reboot'


