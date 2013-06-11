#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>
#include <fcntl.h>
#include <signal.h>


int socket_fd;
FILE* file_fd;

void closeAll(int signal){
	if(socket_fd > 0){
		close(socket_fd);
	}
	if(file_fd > 0){
		//moza by jeszcze sprzatanie dorobić...
		fclose(file_fd);
	}
}


int error(char* reason){
	printf("error %s", reason);
	closeAll(0);
	return -1;
}



int main (int argc, char *argv[])
{
	int buffersize = 1024;
	
	signal(SIGABRT,closeAll);
    signal(SIGTERM,closeAll);
	
	int port, cl_fd, cl_len;
	
	int count = 0;
	struct sockaddr_in serv_addr, cl_addr;
	
	char buffer[buffersize+1];
	
	
	if(argc < 2){
		printf("Usage: <portnumber>\n");
		return -1;
	}
	
	port = atoi(argv[1]);
	
	socket_fd = socket(AF_INET, SOCK_STREAM,0);		
	if(socket_fd < 0){
		return error("Error when opening socket!\n");
	}
	
	printf("listing on port: %d\n", port);
	
	bzero((char *) &serv_addr, sizeof(serv_addr));
	serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(port);
	
	if (bind(socket_fd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) 
              return error("ERROR on binding\n");
   
    listen(socket_fd,5);
   
	
	while(1){
		cl_fd = accept(socket_fd, (struct sockaddr*)&cl_addr, &cl_len);
		
		if(cl_fd < 0){
			return error("Error on accepting connection\n");
		}
		
		
		char* name = malloc(128);
		
		sprintf(name, "tmpFile%d", count);
		
		printf("%s\n",name);
		
		file_fd = fopen(name, "w");	

		if(file_fd < 0){
			return error("error when opening file\n");
		}
		int n;		
		while((n  = read(cl_fd,buffer,buffersize) ) != 0){
		
			if(n < 0){
				return error("Error when recieving data\n");
			}
	
			int w = fwrite(buffer,1, n,file_fd);
			
			if(w != n){
				return error("error when writing files\n");
			}
			
			printf("writing %d bytes.\n", n);
			
		}
		fclose(file_fd);
		close(cl_fd);
		
		printf("File accepted: %s\n", name);
		count++;
	}
	closeAll(0);	
	
	return 0;
}
