#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>
#include <signal.h>
#define _BSD_SOURCE             /* See feature_test_macros(7) */
#include <endian.h>



int socket_fd, cl_fd;

void closeAll(int signal){
	if(socket_fd > 0){
		close(socket_fd);
	}
	if(cl_fd > 0){
		close(cl_fd);
	}
}


int error(char* reason){
	printf("error %s", reason);
	closeAll(0);
	return -1;
}

void swapBytes(char* data, int count){
	int i = 0;
	for(i = 0; i < count /2 ; i++){
		char tmp = data[i];
		data[i] = data[count - i - 1];
		data[count - i - 1] = tmp;
	}
}



int main (int argc, char *argv[])
{
	int buffersize = 255;
	
	signal(SIGABRT,closeAll);
    signal(SIGTERM,closeAll);
	
	int lenght, port,  cl_len;
	struct sockaddr_in serv_addr, cl_addr;
	
	char buffer[buffersize];
	
	
	if(argc < 3){
		printf("Usage: <portnumber>");
		return -1;
	}
	
	port = atoi(argv[1]);
	lenght= atoi(argv[2]);
	
	
	//otwieranie socketu
	socket_fd = socket(AF_INET, SOCK_STREAM,0);		
	if(socket_fd < 0){
		return error("Error when opening socket!");
	}
	
	printf("listing on port: %d\n", port);
	
	bzero((char *) &serv_addr, sizeof(serv_addr));
	serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(port);
	
	//bindowanie
	if (bind(socket_fd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) 
              return error("ERROR on binding");
   
   	//nasluchiwanie
   	//tylko jeden kilient moze sie na raz podpiac
    listen(socket_fd,1);
   
	
	while(1){
		//akceptujemy
		cl_fd = accept(socket_fd, (struct sockaddr*)&cl_addr, &cl_len);
		if(cl_fd < 0){
			return error("Error on accepting connection");
		}
		
		//czytamy tyle ile potrzebujemy
		char* tmpBuff = buffer;
		int n;
		while((n = read(cl_fd,tmpBuff,buffersize + buffer - tmpBuff)) > 0){
			tmpBuff = tmpBuff + n;	
			if((tmpBuff - buffer) >= lenght){
				break;
			}
		}
		n = tmpBuff - buffer;
		
		if(n < 0){
			return error("Error when recieving data");
		}
	
		//zamina endianow
		swapBytes(buffer, lenght);
		
		switch(lenght){
			case 1:
				*((char*)buffer) += 1;
				printf("mam %d\n", *((char*)buffer));
				break;
			case 2:
				*((short*)buffer) += 1;
				printf("mam %d\n", *((short*)buffer));
				break;
			
			case 4:
				*((int*)buffer) += 1;
				printf("mam %d\n", *((int*)buffer));
				break;
			case 8:
				*((int64_t*)buffer) += 1;
				printf("mam %d\n", *((int64_t*)buffer));
				break;
			
			default:
				printf("bad lenght");
		}


		//zamina endianow		
		swapBytes(buffer, lenght);

		n = write(cl_fd, buffer, lenght);
		
		if(n < 0 && n != lenght){
			return error("Error when sending data");
		}
		
		close(cl_fd);
		cl_fd = -1;
	}
	closeAll(0);	
	
	return 0;
}
