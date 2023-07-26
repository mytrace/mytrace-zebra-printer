

echo 'Iniciando config teamviewer...'

ssh -i ./btags_pack/ssh/btags_rsa btags@btags.local '
cd Desktop/
wget https://download.teamviewer.com/download/linux/teamviewer-host_armhf.deb;
sudo dpkg -i teamviewer-host_armhf.deb;
sudo apt --fix-broken install;
sudo teamviewer --passwd Btags@2020;
sudo teamviewer setup Configure headless modes;
sudo teamviewer --daemon restart;
rm teamviewer-host_armhf.deb;
sudo teamviewer info;
'

echo 'Fim da config. Enjoy it!'


#1976834103