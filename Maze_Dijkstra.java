#include <stdio.h>
#include <string.h>
#include "function.h"

#define max 9999
#define n 9 // 미로 크기 설정

char maze[n+1][n+1] =
   {"S00000000C",
   "1110111010",
   "0000000000",
   "0011101111",
   "000000000C",
   "1101110110",
   "0000000000",
   "0111110111",
   "0000100000",
   "110111011G"}; // 미로 제작

char maze_copy[n+1][n+1]; // 미로 복사용
int vertex_matrix[n+1][n+1]; // 정점 행렬
int V; // 정점 갯수

void count_vertex();
void vertex();
int line_weight(int i, int j, int* vertex);
void copy_maze(int B_i, int B_j, int* vertex);
void adjacency_matrix(int* vertex);
int dijkstra(int start, int end, int ad_matrix[V][V]);
void search_special(int* vertex, int ad_matrix[V][V]);
void print_maze(int* vertex);

void count_vertex(){ // 정점 갯수 구하기
   int i, j, vt;
   int total = 0;

   for(i = 0; i <= n; i++){
         for(j = 0; j <= n; j++){
            vt = 0;

            char up = maze[i-1][j]; // case문 조건
            char down = maze[i+1][j];
            char left = maze[i][j-1];
            char right = maze[i][j+1];

            if(maze[i][j] == '0'){ //정점을 길일 때로 제한
               if(i > 0){
                  switch(up){
                     case 'S' : vt++; break; // 시작점
                     case 'G' : vt++; break; // 도착점
                     case '0' : vt++; break; // 길
                  }
               }

               if(i < n){
                  switch(down){
                     case 'S' : vt++; break;
                     case 'G' : vt++; break;
                     case '0' : vt++; break;
                  }
               }

               if(j > 0){
                  switch(left){
                     case 'S' : vt++; break;
                     case 'G' : vt++; break;
                     case '0' : vt++; break;
                  }
               }

               if(j < n){
                  switch(right){
                     case 'S' : vt++; break;
                     case 'G' : vt++; break;
                     case '0' : vt++; break;
                  }
               }
            }
            else if(maze[i][j] == '1'){}
            else
               vt = vt + 3;

            vertex_matrix[i][j] = vt; // vertex_matrix 제작
            if(vt > 2) // 상하좌우 이동 기준 정점 최소 vt값은 3이 된다
               total++;
         }
   }

   V = total;
}

void vertex(){ // 정점 좌표 배열 생성
   int vertex[V*2];
   int a = 0;

   for(int i = 0; i <= n; i++){
      for(int j = 0; j <= n; j++){
         if(vertex_matrix[i][j] > 2){
            vertex[a] = i;
            a++;
            vertex[a] = j;
            a++;
         }
      }
   }

   print_maze(vertex);
   adjacency_matrix(vertex);
}

void adjacency_matrix(int* vertex){ // 인접 행렬 생성
   int ad_matrix[V][V];

   for(int i = 0; i < V; i++){
      for(int j = 0; j < V; j++){
         if(i == j)
            ad_matrix[i][j] = 0;
         else
            ad_matrix[i][j] = line_weight(i, j, vertex);
      }
   }

   search_special(vertex, ad_matrix);
}

void copy_maze(int B_i, int B_j, int* vertex){ // 탐색용 미로 복사

   for(int k = 0; k <= n; k++){
         for(int l = 0; l <=n; l++){
            if(maze[k][l] == '1') // 미로 벽은 1, 이동 가능한 곳은 0
               maze_copy[k][l] = '1';
            else
               maze_copy[k][l] = '0';
         }
      }

      for(int k = 0; k < V*2; k = k + 2)
         maze_copy[vertex[k]][vertex[k+1]] = '1'; // 노드를 벽으로
      maze_copy[B_i][B_j] = '0'; // B노드만 이동 가능하게
}

int line_weight(int i, int j, int* vertex){ // 간선 가중치 부여
   int A_i = vertex[i*2]; // 정점 A
   int A_j = vertex[i*2+1];
   int B_i = vertex[j*2]; // 정점 B
   int B_j = vertex[j*2+1];
   int weight = 0; // 가중치

   copy_maze(B_i, B_j, vertex);
   int cs = 0, mc = 0, count = 0; // cs에 따라 상하좌우 움직임
   int Ai = A_i; // mc는 cs가 변경될 때 증가하고 움직이면 초기화
   int Aj = A_j; // count는 원점으로 돌아간 횟수

   while(A_i != B_i || A_j != B_j){ // A와 B가 일치하지 않는 동안
      maze_copy[A_i][A_j] = '1'; //자기 위치 벽으로

      if(mc == 8){ // mc 8 이상일 때 원점으로 이동
         copy_maze(B_i, B_j, vertex);
         weight = 0;
         mc = 0;
         A_i = Ai;
         A_j = Aj;
         count++;

         if(count == 1) // 지난번 시작과는 다른 방향으로 시작하기 위해서
            cs = 1;
         else if(count == 2)
            cs = 2;
         else if(count == 3)
            cs = 3;
         else if(count == 4)
            return max; // 4방향 시작 모두 실패시 가중치 0
      }

      if(cs == 0){ // cs가 0이면 위
         if(A_i == 0){ // 배열 크기 초과 방지
            cs = 1;
            mc++;
         }else{
            if(maze_copy[A_i-1][A_j] == '0'){
               A_i--;
               weight++;
               mc = 0;
            }else{
               cs = 1;
               mc++;
            }
         }
      }else if(cs == 1){ // cs가 1이면 아래
         if(A_i == n){ // 배열 크기 초과 방지
            cs = 2;
            mc++;
         }else{
            if(maze_copy[A_i+1][A_j] == '0'){
               A_i++;
               weight++;
               mc = 0;
            }else{
               cs = 2;
               mc++;
            }
         }
      }else if(cs == 2){ // cs가 2면 왼쪽
         if(A_j == 0){ // 배열 크기 초과 방지
            cs = 3;
            mc++;
         }else{
            if(maze_copy[A_i][A_j-1] == '0'){
               A_j--;
               weight++;
               mc = 0;
            }else{
               cs = 3;
               mc++;
            }
         }
      }else if(cs == 3){ // cs가 3이면 오른쪽
         if(A_j == n){ // 배열 크기 초과 방지
            cs = 4;
            mc++;
         }else{
            if(maze_copy[A_i][A_j+1] == '0'){
               A_j++;
               weight++;
               mc = 0;
            }else{
               cs = 4;
               mc++;
            }
         }
      }else // cs 4면 처음 방향부터
         cs = 0;

   }

   return weight; // 가중치 반환
}

int dijkstra(int start, int end, int ad_matrix[V][V]){
   int visit[V]; // 방문 여부 배열 1이면 방문 0이면 미방문
   int dist[V]; // 시작점부터 도착점 까지 가중치 저장 배열
   int min; // 가중치 비교
   int pivot; // 분기점
   int index[V]; // 경로 저장 배열
   int temp[V];
   int k = 0;

   for(int i = 0; i < V; i++){
      visit[i] = 0;
      dist[i] = max;
      index[i] = 0;
      temp[i] = 0;
   }
   dist[start] = 0;  // 시작점 거리 0

   for(int i = 0; i < V; i++){
      min = max;
      for(int j = 0; j < V; j++){
         if(visit[j] == 0 && min > dist[j]){
            min = dist[j];
            pivot = j;
         }
      }

      visit[pivot] = 1;
      for(int j = 0; j < V; j++){
         if(dist[j] > dist[pivot] + ad_matrix[pivot][j]){
            dist[j] = dist[pivot] + ad_matrix[pivot][j];
            index[j] = pivot;
            }
         }

   }

   pivot = end;
   while(index[pivot]){
      //printf(" %c <- ", index[pivot] + 65);
      temp[k] = index[pivot];
      k++;
      pivot = index[pivot];
   }
   for(int i = k-1; i >= 0; i--)
      printf("%c -> ", temp[i] + 65);

   return dist[end];

}

void search_special(int* vertex, int ad_matrix[V][V]){
   int i, j;
   int start, end;
   int fcandy, scandy;
   int special[8]; // (시작점 + 도착점  + 캔디 총 갯수) *2
   int count = 0;

   for(i = 0; i <= n; i++){
      for(j = 0; j <= n; j++){
         if(maze[i][j] == 'S'){
            special[0] = i;
            special[1] = j;
         }else if(maze[i][j] == 'G'){
            special[6] = i;
            special[7] = j;
         }else if(maze[i][j] == 'C'){
            if(count == 0){
               special[2] = i;
               special[3] = j;
               count = 1;
            }else{
               special[4] = i;
               special[5] = j;
            }
         }
      }
   }

   for(int k = 1; k < V*2; k = k + 2){
      if(special[0] == vertex[k-1] && special[1] == vertex[k])
         start = (k-1)/2;
      if(special[6] == vertex[k-1] && special[7] == vertex[k])
         end = (k-1)/2;
      if(special[2] == vertex[k-1] && special[3] == vertex[k])
         fcandy = (k-1)/2;
      if(special[4] == vertex[k-1] && special[5] == vertex[k])
         scandy = (k-1)/2;
   }

   printf("%c -> ", start + 65);
   printf(" %c\n 첫번째 사탕 우선 탐색 소모 HP: %d\n\n", end + 65, dijkstra(start, fcandy, ad_matrix) + dijkstra(fcandy, scandy, ad_matrix) + dijkstra(scandy, end, ad_matrix));
   printf("%c -> ", start + 65);
   printf(" %c\n 두번째 사탕 우선 탐색 소모 HP: %d", end + 65, dijkstra(start, scandy, ad_matrix) + dijkstra(scandy, fcandy, ad_matrix) + dijkstra(fcandy, end, ad_matrix));

}

void print_maze(int* vertex){ // 미로 출력 함수
   int i, j;
   int k = 0;

   for(i = 0; i <= n; i++){
      for(j = 0; j <= n; j++){
         if(maze[i][j] == '1')
            printf(" ■ ");
         else if(vertex[k] == i && vertex[k+1] == j){
            if(maze[i][j] == 'C'){
               printf("'%c'", k/2 + 65);
               k = k + 2;
            }else{
               printf(" %c ", k/2 + 65);
               k = k + 2;
            }
         }else
            printf(" □ ");
      }printf("\n\n");
   }printf("\n");

}

int main(){
   count_vertex();
   vertex();

   return 0;
}