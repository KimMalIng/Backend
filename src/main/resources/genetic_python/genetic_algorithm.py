#!/usr/bin/env python
# coding: utf-8

# In[69]:


import json
import datetime
import random
import copy

#유전 알고리즘을 통해 입력받은 json 형식의 스케줄을 자동으로 할당해주는 클래스 Schedule
class Schedule:
    
    #시간 문자열("**:**"형태)을 입력받으면 ver이 0이면 계획의 실행 시간이 list의 time_unit에 따라 몇칸을 차지하는 지 반환하고  
    #ver이 1이면 시작 시간의 list index를 반환해준다.
    def make_time(self, time_str, ver):
        
        #문자열을 :를 기준으로 나눠 hour과 minute으로 time의 변수에 할당
        time = time_str.split(":")
        time = datetime.time(int(time[0]), int(time[1]))
        if ver == 0:
            
            #시간과 분을 실수 형태의 시간으로 만들어서 60을 시간 구분 단위인 time_unit으로 나눈 값을 곱해준다.
            time_slot = int((float(time.hour)+float(time.minute/60))/(self.time_unit/60))
            
            return time_slot
        
        elif ver == 1:
            
            #입력받은 시간의 hour를 (60/시간 구분 단위)만큼 곱하고 minute을 시간 구분 단위로 나눠주어 리스트에서 그 시간의 위치를 반환한다.
            time_index = int(int(time.hour) * int(60/self.time_unit) + int(time.minute/self.time_unit))
            
            return time_index
    
    #json에 존재하는 마감 날짜와 시간을 읽어서 계획 리스트의 어느 위치인 지 튜플로 반환해준느 함수
    def make_deadline(self, deadline):
        deadline = deadline.split()
        
        #마감 시간이 자정이라면 전날 즉 week_schedule의 1 작은 row의 마지막 index까지 이므로 해당 과정을 수행
        if deadline[1] == "00:00":
            row = self.week_dic[deadline[0]] - 1
            column = int(24*(60/self.time_unit) - 1)
            
        #마감 시간이 자정이 아니라면 make_time를 통해 week_schedule의 시간 index로 바꾼다
        else:
            row = self.week_dic[deadline[0]]
            #make_time의 ver=1은 start_time의 index를 반환하기 때문에 반환값에서 1을 빼주어야 마감 시간을 구할 수 있다.
            column = int(self.make_time(deadline, 1) - 1)
        
        return (row, column)
    
    #최초 Schedule 객체를 만들 때 호출되는 초기화 함수(json_file을 읽어들여 load해주고, week_dic에는 json의 Week_day 리스트를,
    #sch_dic에는 사용자가 입력한 배정 해주길 바라는 일정들의 {고유번호:(이름, 실행시간, 마감일자)}의 dictionary형태로 저장하며
    #week_schedule에는 사용자의 시간이 고정된 계획을 1로 입력한 (주차 남은 일자)x(24*60/시간 구분 단위)의 리스트가 저장된다.
    def __init__(self, json_path):
        with open(json_path, 'r', encoding='UTF-8') as f:
            self.json_str = json.load(f)
        self.week_dUnicodeDecodeErroric = {}
        self.sch_dic = {}
        
        #민경 추가
        self.week_dic={}
        
        #week_schedule의 행 크기를 위해 json파일에 며칠의 일자가 있는지 Week_day의 크기를 구해 week_len에 저장
        self.week_len = len(self.json_str["week_day"]) #민경 동적으로수정
        
        #시간 구분 단위(60이면 리스트의 한 칸이 1시간이고 10이면 한 칸이 10분)
        self.time_unit = 10
        
        #sch_dic의 배정할 일정들의 고유 번호를 저장하는 변수 count
        self.count = 1
        
        #(주차 남은 일자)x(24*60/시간 구분 단위)크기의 리스트를 값이 0인 ndarray로 초기화
        self.week_schedule = [[0 for i in range(24*int(60/self.time_unit))] for j in range(self.week_len)]
        
        self.final_solution = [[0 for i in range(24*int(60/self.time_unit))] for j in range(self.week_len)]
        
        plan_startTime = self.make_time(self.json_str["schedule_startTime"], 1)
        
        for i in range(plan_startTime):
            self.week_schedule[0][i] = 1
        
        #수면, 식사 시간에 일정을 배정하는 걸 막기 위해 12~08, 11~13, 17~19시에 1을 저장
        for i in range(self.week_len):
            sleep_idx = self.make_time("00:00", 1)
            sleep_slot = self.make_time("08:00", 0)
            for j in range(sleep_slot):  
                self.week_schedule[i][sleep_idx+ j] = 1
            lunch_idx = self.make_time("11:00", 1)
            lunch_slot = self.make_time("02:00", 0)
            for j in range(lunch_slot):
                self.week_schedule[i][lunch_idx+j] = 1
            dinner_idx = self.make_time("17:00", 1)
            dinner_slot = self.make_time("02:00", 0)
            for j in range(dinner_slot):
                self.week_schedule[i][dinner_idx+j] = 1
            
        #week_schedul의 row값을 날짜로 접근하기 위해 json파일의 Week_day안에 날짜를 리스트의 row값에 대한 key값을 가지도록 dictionary week_dic를 만듦
        for i in range(self.week_len):
            self.week_dic[self.json_str["week_day"][i]] = i
            
        
        for i in range(len(self.json_str["schedule"])):
            schedule = self.json_str["schedule"][i]
            
            #json에서 읽어온 실행 시간을 week_schedule에 배정해야하는 시간 크기로 바꿔 run_time에 저장
            run_time = self.make_time(schedule["estimated_time"], 0)
            
            #label이 0이면 시간이 고정된 일정이라는 뜻이므로 해당 시간을 읽어들여 make_time를 통해 week_schedule의 index로 바꾸고 
            #해당 index에 1값을 저장
            if schedule["label"] == 0:
                start_time = self.make_time(schedule["startTime"], 1)
                
                #week_schedule의 startTime index에 run_time만큼 1을 저장
                for j in range(run_time):
                    self.week_schedule[self.week_dic[schedule["day"]]][start_time+j] = 1
            
            #label이 0이 아닌 다른 값은 배정을 해주어야 하는 일정이기 때문에 일정들의 {고유번호:(이름, 실행시간, 마감일자)}형태로
            #sch_dic에 저장
            else:
                self.count += 1
                if schedule["deadline"] != None:
                    (row, column) = self.make_deadline(schedule["deadline"])
                    self.sch_dic[self.count] = (schedule["name"], run_time, (row, column))
                else:
                    self.sch_dic[self.count] = (schedule["name"], run_time, None)
                    
    #초기해를 생성하는 함수
    def generate_plan(self):
        #sch_dic의 key값을 저장하여 배정할 일정에 접근하도록 하는 변수 dic_idx
        dic_idx = 2
        
        #week_schedule을 얉은 복사
        sch_list = copy.deepcopy(self.week_schedule)
        
        #dic_idx로 shc_dic의 마지막 고유키 값인 count까지 반복문을 통해 접근
        while dic_idx <= self.count:
            #실행 시간만큼 sch_list에 배정했는지 count하는 변수 time_count
            time_count = 0          
            
            #배정하고 남은 실행 시간을 저장하는 변수 remain_time
            remain_time = self.sch_dic[dic_idx][1]    
            
            #실행 시간보다 적게 배정했을 때 계속 반복문을 돎
            while time_count < self.sch_dic[dic_idx][1]:
                #sch_list의 행값을 난수로 받음
                row = random.randint(0, self.week_len-1) 
                
                #sch_list의 열값을 난수로 받음
                column = random.randint(0, 24*(60/self.time_unit)-1)
                
                #run_time을 1~remain_time사이의 난수로 받음
                if remain_time >= int(60/self.time_unit):
                    run_time = random.randint(int(60/self.time_unit),remain_time)
                else:
                    run_time = remain_time   
                    
                #column+run_time의 값이 열의 최대 index보다 크면 위의 난수들 다시 받음
                if column + run_time > (24*(60/self.time_unit)-1):
                    continue
                    
                #계획을 배정하려는 부분이 0인지 확인하는 부분(0이면 배정, 0이 아니면 while문 처음부터 다시 돎)
                i = 0
                while i < run_time:
                    if sch_list[row][column+i] != 0:
                        break
                    else: 
                        i += 1
                if i == run_time:
                    for j in range(i):
                        sch_list[row][column+j] = dic_idx
                else:
                    continue
                #run_time만큼 배정을 완료했으므로 time_count에 run_time만큼 추가
                time_count += run_time
                
                #remain_time에서 배정을 완료한 시간만큼 빼준다.
                remain_time -= run_time         
                
            #sch_dic의 다음 원소로 넘어가기 위해 dic_idx에 1을 더함
            dic_idx += 1
            
        return sch_list
    
    #size의 크기만큼 generate_plan을 실행하고 결과를 저장하여 반환하는 함수
    def generate_population(self, size):
        population = []
        for i in range(size):
            population.append(self.generate_plan())
            
        return population
    
    #적합도 점수를 구하는 함수
    def fitness(self, schedule):
        #입력받은 schedule의 적합도를 저장하는 변수
        fitness = 0
        
        #전체 총점을 저장하는 변수
        score = len(self.sch_dic) * 100
        
        #배정한 일정의 개수만큼 반복문을 돌며 각 일정에 대한 적합도를 계산
        for i in range(len(self.sch_dic)):
            dic_ = self.sch_dic[i+2]
            run_time = dic_[1]
            
            #마감 시간과 상관없이 전체 list에 저장된 해당 일정의 실행 횟수를 저장할 변수
            total_count = 0
            
            #마감시간이 없을 경우
            if dic_[2] == None:
                score += run_time / (60/self.time_unit)
                #schedule의 행과 열을 돌며 고유 번호 값의 수만큼 total_count에 저장
                for j in range(self.week_len):
                    k = 0
                    while k < int(24*60/self.time_unit):
                        if schedule[j][k] == i+2:
                            total_count += idx
                           
                            if idx >= int(60/self.time_unit):
                                fitness += 1
                                if idx > int(60/self.time_unit) * 3:
                                    fitness -= 3
                            k += idx
                        else:
                            k += 1
                
                #전체 계획 실행 횟수와 입력받은 실행 횟수가 같으면 적합도에 값을 더함
                if total_count == run_time:
                    fitness += 100
                else:
                    fitness -= 100
            
            #마감 기한이 존재할 때
            else:
                deadline_count = 0
                score += run_time / (60/self.time_unit)
                for j in range(self.week_len):
                    k = 0
                    while k < int(24*60/self.time_unit):
                        if schedule[j][k] == i+2:
                            idx = 1
                            while True:
                                if schedule[j][k+idx] == i+2:
                                    idx += 1
                                else:
                                    break
                            total_count += idx
                            
                            #마감 기한 안에 index가 존재하면 deadline_count에도 1을 더함
                            if j < dic_[2][0]:
                                deadline_count += idx
                                #연속되게 배정되어 1시간 이상 3시간이하로 배정된 계획이면 적합도에 1점 추가
                                if idx >= int(60/self.time_unit):
                                    fitness += 1
                                    
                                    #3시간 초과로 배정된 경우는 적합도에 -3점 추가
                                    if idx > int(60/self.time_unit) * 3:
                                        fitness -= 2
                            elif j == dic_[2][0] and k <= dic_[2][1]:
                                deadline_count += idx
                                if idx >= int(60/self.time_unit):
                                    fitness += 1
                                    if idx > int(60/self.time_unit) * 3:
                                        fitness -= 2
                            #마감 기한 밖에 index가 존재하면 적합도 점수를 빼줌
                            elif j == dic_[2][0] and k > dic_[2][1]:
                                fitness -= idx
                            elif j >= dic_[2][0]:
                                fitness -= idx               
                            k += idx
                        else:
                            k += 1
                #total_count가 deadline_count와도 같고 total_count가 run_time과도 같으면 적합도에 값을 더함
                if total_count == deadline_count:
                    fitness += 50
                    if total_count == run_time:
                        fitness += 50
                else:
                    fitness -= 100
        
        #총점에서 퍼센트로 값을 반환
        return fitness/score * 100
    
    #입력받은 population들의 적합도를 점수로 매겨서 내림차순으로 정렬한 population과 적합도 점수를 묶어서 리스트로 반환하는 함수
    def compute_performance(self, population):
        #적합도 점수와 population을 묶을 리스트
        performance_list = []
        
        #population에 있는 각 개체에 대해 적합도 점수를 매기고 점수를 performance_list에 추가한다
        for individual in population:
            score = self.fitness(individual)
            performance_list.append([individual, score])
            
        #performance_list를 내림차순을 정렬
        population_sorted = sorted(performance_list, key = lambda x: x[1], reverse = True)
        
        return population_sorted
    
    #compute_performance를 수행하여 적합도 점수에 따라 내림차순으로 정렬된 리스트에서 입력받은 수만큼 개체를 남기는 함수
    def select_survivors(self, population_sorted, best_sample, lucky_few):
        #다음 세대로 남길 개체들을 저장할 리스트
        next_generation = []
        
        #best_sample만큼 next_generation에 저장
        for i in range(best_sample):
            next_generation.append(population_sorted[i][0])
        
        #적합도 점수가 아닌 임의로 추출해서 다양성을 보존할 개체를 뽑는 작업
        lucky_survivors = random.sample(population_sorted, k = lucky_few)
        
        #임의로 추출한 개체들을 next_generation에 추가
        for j in lucky_survivors:
            next_generation.append(j[0])
            
        #next_generation을 무작위로 섞음
        random.shuffle(next_generation)
        
        return next_generation
    
    #입력받은 부모들을 한 점 교차 연산을 시켜 자손 한 개체를 만드는 함수
    def create_child(self, parent1, parent2):
        #자손을 저장할 리스트 child 생성
        child = [[0 for i in range(24*int(60/self.time_unit))] for j in range(self.week_len)]
        
        #돌연변이가 일어날 index의 행값을 난수로 받음
        row = random.randint(0, self.week_len-1) 

        #돌연변이가 일어날 index의 열값을 난수로 받음
        column = random.randint(0, 24*(60/self.time_unit)-1)
        
        #1/2의 확률로 교차 지점 왼쪽은 parent1로부터 염색체를 받고, 오른쪽은 parent2로부터 염색체를 받음
        if (int(100*random.random()) < 50):
            for i in range(row):
                child[i][:] = parent1[i][:]
            child[row][:column] = parent1[row][:column]
            child[row][column:] = parent2[row][column:]
            for j in range(row+1,self.week_len):
                child[j][:] = parent2[j][:]
        #1/2의 확률로 교차 지점 왼쪽은 parent2로부터 염색체를 받고, 오른쪽은 parent1로부터 염색체를 받음
        else:
            for i in range(row):
                child[i][:] = parent2[i][:]
            child[row][:column] = parent2[row][:column]
            child[row][column:] = parent1[row][column:]
            for j in range(row+1,self.week_len):
                child[j][:] = parent1[j][:]            

        return child
    
    #create_child를 이용하여 한쌍의 부모에서 n_child만큼 자손을 만들고, 모든 부모쌍들에 대해서 create_child를 수행하는 함수
    def create_children(self,parents, n_child):
        next_population = []
        #모든 부모쌍에 대해
        for i in range(int(len(parents)/2)):
            #각 부모쌍마다 n_child만큼 자손 생성
            for j in range(n_child):
                next_population.append(self.create_child(parents[i], parents[len(parents)- 1 - i]))
        return next_population
    
    #돌연변이 연산 중 exchange를 수행하는 함수
    def mutate_plan(self, individual):
        #돌연변이 크기를 난수로 생성
        mutate_size = random.randint(1, 60/self.time_unit)
        while True:
             #교환 지점의 첫 번째 행값과 열값을 난수로 생성
            row1 = random.randint(0, self.week_len-1) 
            column1 = random.randint(0, 24*(60/self.time_unit)-1)
            
             #교환 지점의 두 번째 행값과 열값을 난수로 생성
            row2 = random.randint(0, self.week_len-1) 
            column2 = random.randint(0, 24*(60/self.time_unit)-1)
            
            #열값과 돌연변이 크기가 열의 index size보다 클 때 continue로 다시 난수 생성
            if column1 + mutate_size > 24*(60/self.time_unit) - 1 or column2 + mutate_size > 24*(60/self.time_unit) -1:
                continue
                
            #열값의 index를 mutate size만큼 돌며 교환 지점의 각 값이 1이 아닌지 확인
            i = 0
            while i < mutate_size:
                if individual[row1][column1+i] != 1 and individual[row2][column2+i] != 1:
                    i += 1
                    continue
                else:
                    break
            #column1, column2에서 모두 mutate size만큼 모두 1이 아니라면 두 부분을 교환 후 while문 탈출
            if i == mutate_size:
                for i in range(mutate_size):
                    individual[row1][column1+i], individual[row2][column2+i] = individual[row2][column2+i], individual[row1][column1+i]
                break
            #중간에 1인 부분이 존재하면 continue문을 통해 while을 다시 돌기
            else:
                continue
        
        return individual
    
    #돌연변이 연산 중 insert를 수행하는 함수(지역최적해를 통해 마감기한에 임박한 계획이 통째로 사라지는 방향으로 유전되는 상황을 피하기 위한)
    def mutate_insert(self, individual):
        #배정해야 하는 계획의 크기만큼 for문을 돌며
        for i in range(len(self.sch_dic)):
            #각 계획에 대한 튜플을 dic_에 저장
            dic_=self.sch_dic[i+2]
            #계획 별 전체 실행 시간을 run_time으로 저장
            run_time = dic_[1]
            #마감 기한까지 실행 횟수를 저장하는 변수 deadline_count
            deadline_count = 0
            
            #마감기한이 없는 경우
            if dic_[2] == None:
                #계획의 전체 부분을 모두 돌아서 계획의 실행 횟수를 count
                for j in range(self.week_len):
                    for k in range(int(24*60/self.time_unit)):
                        if individual[j][k] == i+2:
                            deadline_count += 1
                            
                #마감기한까지 실행시간을 못 채운 경우
                if deadline_count < run_time:
                    while True:
                        #1부터 못 채운 부분까지의 난수를 생성
                        mutate_size = random.randint(1,run_time-deadline_count)
                        
                        #돌연변이를 일으킬 부분의 행값과 열값을 생성
                        row = random.randint(0, self.week_len-1) 
                        column = random.randint(0, 24*(60/self.time_unit)-1)
                        
                        #열값과 생성된 난수의 크기가 전체 열 Index보다 크면 다시 난수 생성
                        if column + mutate_size > 24*(60/self.time_unit) - 1:
                            continue
                        
                        #p를 통해 column부터 mutate_size만큼 돌며 해당 값이 1인지 아닌지 확인
                        p = 0
                        while p < mutate_size:
                            if individual[row][column+p] != 1:
                                p += 1
                                continue
                            else:
                                break
                                
                        #mutate_size만큼 모든 값이 1이 아닌 경우
                        if p == mutate_size:
                            #mutate_size만큼 모두 i+2전환 후 while문 탈출
                            for q in range(mutate_size):
                                individual[row][column+q] = i+2
                            break
                            
                        #mutate_size안에 1인 값이 포함되어 있는 경우 continue를 통해 while문 다시 돌기
                        else:
                            continue
            
            #마감기한이 있는 경우
            else:
                #마감 기한 전의 행까지 전체 열을 모두 돌며 계획의 실행 횟수 count
                for j in range(dic_[2][0]):
                    for k in range(int(24*60/self.time_unit)):
                        if individual[j][k] == i+2:
                            deadline_count += 1
                
                #마감 기한 행에서만 마감 기한 열까지 돌며 계획의 실행 횟수 count
                for k in range(dic_[2][1]+1):
                    if individual[dic_[2][0]][k] == i+2:
                        deadline_count += 1
                        
                #마감기한까지 실행 횟수를 못 채웠다면
                if deadline_count < run_time:
                    while True:
                        #못 채운 횟수까지 난수를 생성
                        mutate_size = random.randint(1,run_time-deadline_count)
                        
                        #난수로 돌연변이를 일으킬 행값 생성
                        row = random.randint(0, dic_[2][0])
                        #생성된 행값이 마감기한 행값과 같다면
                        if row == dic_[2][0]:
                            #열값은 마감기한 열까지만 생성
                            column = random.randint(0, dic_[2][1])
                            
                        #생성된 행값이 마감기한 행보다 작은 경우 전체 열값 index까지의 난수 생성
                        else:
                            column = random.randint(0, 24*(60/self.time_unit)-1)
                            
                        #생성된 열값과 돌연변이 부분의 크기 합이 전체 열 index범위를 넘는다면 다시 while문 돌기
                        if column + mutate_size > 24*(60/self.time_unit) - 1:
                            continue
                            
                        #돌연변이 부분 돌면서 1이 아닌지 확인
                        p = 0
                        while p < mutate_size:
                            if individual[row][column+p] != 1:
                                p += 1
                                continue
                            else:
                                break
                        
                        #돌연변이 부분이 모두 1이 아니면 돌연변이 크기만큼 바꿔주기
                        if p == mutate_size:
                            for q in range(mutate_size):
                                individual[row][column+q] = i+2
                            break
                        
                        #1인 부분이 중간에 포함 되어있는 경우 while문 다시 돌기
                        else:
                            continue
                            
        return individual
                        
    #위에서 선언된 2개의 돌연변이 연산을 확률에 따라 일으켜서 변화를 저장해주는 함수
    def mutate_population(self, population, mutation_exchange, mutation_insert):
        for i in range(len(population)):
            #mutation_exchange/100의 확률만큼 mutate_plan을 일으켜준다 
            if random.random() * 100 < mutation_exchange:
                population[i] = self.mutate_plan(population[i])
                
            #mutation_insert/100의 확률만큼 mutate_insert를 일으켜준다.
            if random.random() * 100 < mutation_insert:
                population[i] = self.mutate_insert(population[i])
        
        return population
    
    #위에서 선언한 함수들을 통해 genetic_algorithm을 실행해주는 함수
    def execute_algorithm(self):
        #전체 세대수를 저장
        n_generation = 800
        #각 세대별 개체수를 저장
        population = 100
        #각 세대에서 적합도 점수에 따라 추출할 개체의 수
        best_sample = 20
        #각 세대에서 운 좋게 살아남아 추출할 개체의 수
        lucky_few = 20
        #한 부모쌍에서 태어날 자식의 수
        n_child = 5
        #한 세대에서 mutate_plan을 일으킬 개체수
        mutation_exchange = 5
        #한 세대에서 mutate_insert를 일으킬 개체수
        mutation_insert = 5
        
        #generate_population을 통해 초기해, 즉 부모집합을 population만큼 생성
        pop = self.generate_population(population)
        #전체 세대 수만큼 반복문 실행
        for g in range(n_generation):
            #한 세대의 개체에 대한 적합도 점수를 매겨서 점수별로 내림차순 정렬한 값을 pop_sorted에 저장
            pop_sorted = self.compute_performance(pop)

            #95점의 적합도를 넘는 개체가 있으면 반복문 탈출하고 해당 값 저장
            if int(pop_sorted[0][1]) >= 95:
                self.final_solution = pop_sorted[0][0]
#                 print("SUCCESS! The plan is============================================= \n", (pop_sorted[0]))
                break 
            
            #pop_sorted에서 best_sample수와 lucky_few의 수 만큼 다음 세대로 유전자를 전달할 개체를 추출하여 next_generation에 저장
            next_generation = self.select_survivors(pop_sorted, best_sample, lucky_few)
            
            #추출한 next_generation에서 n_child의 수만큼 교차 연산을 통해 자식 집합 생성
            next_population = self.create_children(next_generation, n_child)
            
            #mutate_population을 통해 돌연변이 연산 수행
            new_child = self.mutate_population(next_population, mutation_exchange, mutation_insert)
            
            #pop에 만들어진 다음 세대 자식 집합 저장
            pop = new_child
            
            #알고리즘 작동 과정을 보여주는 코드
#             print("=============================%sth generation============================" %(g+1))
#             print(pop_sorted[0])

        return self.final_solution

    #동적으로 배정된 계획을 json파일로 저장하는 함수
    def write_json(self, path):
        #json파일에 쓸 값을 저장하는 dictionary 값을 저장하는 변수
        json_dic = {}
        
        #고정된 계획의 json 정보를 가지고 있을 리스트 변수 fix_schedule을 생성
        fix_schedule = []
        
        #배정할 계획의 json 정보를 가지고 있을 리스트 변수 assign_shedule을 생성
        assign_schedule = []
        
        #json_dic에 처음 읽어들인 json파일의 Week_day값과 Schedule_startTime을 저장
        json_dic["week_day"] = self.json_str["week_day"]
        json_dic["schedule_startTime"] = self.json_str["schedule_startTime"]
        
        #처음 읽어들인 json 파일의 Schedule에 존재하는 계획 중 고정된 일정을 가진 것들에 대해 fix_schedule에 추가해주는 과정
        for i in range(len(self.json_str["schedule"])):
            #label이 0이면 시간이 정해진 일정들이므로 fix_schedule에 추가
            if self.json_str["schedule"][i]["label"] == 0:
                fix_schedule.append(self.json_str["schedule"][i])
                
            #label이 0이 아니면 시간이 정해지지 않은 유동적인 일정들이기에 assign_schedule에 추가
            else:
                assign_schedule.append(self.json_str["schedule"][i])
                
        #json_dic의 Schedule란에 시간이 고정된 계획들의 정보를 저장하고 있는 fix_schedule값을 추가
        json_dic["schedule"] = fix_schedule
        
        #만들어진 최종 계획의 값을 하나씩 읽으면 1과 0이 아닌 부분에 대해서 고정된 실행 시간을 json_dic에 기록
        for i in range(self.week_len):
            idx = 0
            while idx < int(24*60/self.time_unit):
                
                #해당 index의 값이 0과 1이 아닌경우 얼마나 연속된 값을 가지는 지 확인하기 위한 과정
                if self.final_solution[i][idx] != 0 and self.final_solution[i][idx] != 1:
                    #해당 값을 plan_label에 저장
                    plan_label = self.final_solution[i][idx]
                    count = 1
                    
                    #해당 idx+count가 열의 전체 index보다 작으면 반복
                    while count + idx < int(24*60/self.time_unit):
                        #연속된 값이 반복되면 count에 1추가
                        if self.final_solution[i][idx+count] == plan_label:
                            count += 1
                            
                        #연속된 값이 나오지 않는 경우 while문 탈출
                        else:
                            break
                    
                    #시작 시간은 열의 index를 통해 구함
                    start_hour = int(idx//(60/self.time_unit))
                    start_minute = int((idx%(60/self.time_unit))*self.time_unit)
                    start_time = str(str(start_hour).zfill(2))+":"+str(str(start_minute).zfill(2))
                    
                    #위의 반복문을 열의 끝까지 돌았을 경우 종료시간은 "24:00"
                    if idx + count == int(24*60/self.time_unit):
                        end_time = "24:00"
                    
                    #위의 반복문을 도중에 빠져 나왔다면 종료 시간은 위의 반복문에서 구한 count를 idx에 더해서 구함
                    else:
                        end_hour = int((idx+count)//(60/self.time_unit))
                        end_minute = int(((idx+count)%(60/self.time_unit))*self.time_unit)
                        end_time = str(str(end_hour).zfill(2))+":"+str(str(end_minute).zfill(2))
                        
                    #idx에 count만큼 더해줌
                    idx += count
                    
                    #assign_list의 어느 index에 위의 plan_label에 대한 Json 정보가 있는 지 구하는 과정
                    list_idx = -1
                    for j in range(len(assign_schedule)):
                        #"name"이 같다면 list_idx에 j를 저장하고 for문 탈출
                        if assign_schedule[j]["name"] == self.sch_dic[plan_label][0]:
                            list_idx = j
                            break
                            
                    #실행 시간을 count를 통해 구해서 run_time에 저장
                    run_hour = int(count//(60/self.time_unit))
                    run_minute = int(count%(60/self.time_unit)) * self.time_unit
                    run_time = str(str(run_hour).zfill(2))+":"+str(str(run_minute).zfill(2))
                    
                    #assign_schedule_copy를 깊은 복사를 통해 만들어서 json 정보값을 바꿔도 assign_schedule의 값이 바뀌지 않도록 함
                    assign_schedule_copy = copy.deepcopy(assign_schedule)
                    
                    #시작 시간, 종료시간, 실행시간을 assign_schedule에 입력
                    assign_schedule_copy[list_idx]["startTime"] = start_time
                    assign_schedule_copy[list_idx]["endTime"] = end_time
                    assign_schedule_copy[list_idx]["estimated_time"] = run_time
                    # assign_schedule_copy[list_idx]["user_id"] = user_id
                    
                    #실행 일자는 현재 반복문의 row데이터에 대해서 입력
                    assign_schedule_copy[list_idx]["day"] = sch.json_str["week_day"][i]
                    
                    #위의 입력된 assign_schedule_copy의 값을 json_dic에 저장
                    json_dic["schedule"].append(assign_schedule_copy[list_idx])
                
                #해당 값이 0이나 1인 경우 idx에 1 더함
                else:
                    idx += 1
        
        #json_dic에 저장된 값을 json파일로 저장
        with open(path, 'w', encoding = 'UTF-8') as fw:
            json.dump(json_dic, fw, indent = '\t')
            
        return 0

if __name__== "__main__":
    read_path = "src/main/resources/genetic_python/data.json"
    write_path = "src/main/resources/genetic_python/data.json"
    sch = Schedule(read_path)
    sch.execute_algorithm()
    sch.write_json(write_path)


# In[ ]:




