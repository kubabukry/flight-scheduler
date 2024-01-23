package com.pl.edu.wieik.flightScheduler.task;

import com.pl.edu.wieik.flightScheduler.flight.Flight;
import com.pl.edu.wieik.flightScheduler.flight.FlightRepository;
import com.pl.edu.wieik.flightScheduler.operation.Operation;
import com.pl.edu.wieik.flightScheduler.operation.OperationRepository;
import com.pl.edu.wieik.flightScheduler.resource.ResourceRepository;
import org.springframework.stereotype.Service;
import com.pl.edu.wieik.flightScheduler.resource.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ResourceRepository resourceRepository;
    private final FlightRepository flightRepository;
    private final OperationRepository operationRepository;



    private List<Task> tasks = new ArrayList<>();
    private List<Task> tasksRunway = new ArrayList<>();
    private List<Task> tasksTaxiIn = new ArrayList<>();
    private List<Task> tasksDeboarding = new ArrayList<>();
    private List<Task> tasksUnloading = new ArrayList<>();
    private List<Task> tasksFueling = new ArrayList<>();
    private List<Task> tasksCatering = new ArrayList<>();
    private List<Task> tasksCleaning = new ArrayList<>();
    private List<Task> tasksLoading = new ArrayList<>();
    private List<Task> tasksBoarding = new ArrayList<>();
    private List<Task> tasksTaxiOut = new ArrayList<>();
    private List<Task> tasksTakeOff = new ArrayList<>();
    
    private Instant landingCompleted = null;
    private Task currentRunway;
    private Task currentTaxiIn;
    private Task currentDeboarding;
    private Task currentUnloading;
    private Task currentFueling;
    private Task currentCatering;
    private Task currentCleaning;
    private Task currentLoading;
    private Task currentBoarding;
    private Task currentTaxiOut;
    private Task currentTakeOff;

    public TaskService(TaskRepository taskRepository, ResourceRepository resourceRepository, FlightRepository flightRepository, OperationRepository operationRepository) {
        this.taskRepository = taskRepository;
        this.resourceRepository = resourceRepository;
        this.flightRepository = flightRepository;
        this.operationRepository = operationRepository;
    }

    @Transactional
    public void createTasks() {
        Resource runway = resourceRepository.findByName("Runway");
        Resource pilotCar = resourceRepository.findByName("Pilot Car");
        Resource gate = resourceRepository.findByName("Gate");

        Operation landing = operationRepository.findByName("Landing");
        Operation taxiIn = operationRepository.findByName("Taxi-In");
        Operation deboarding = operationRepository.findByName("Deboarding");
        Operation unloading = operationRepository.findByName("Unloading");
        Operation fueling = operationRepository.findByName("Fueling");
        Operation catering = operationRepository.findByName("Catering");
        Operation cleaning = operationRepository.findByName("Cleaning");
        Operation loading = operationRepository.findByName("Loading");
        Operation boarding = operationRepository.findByName("Boarding");
        Operation taxiOut = operationRepository.findByName("Taxi-Out");
        Operation takeOff = operationRepository.findByName("Take-Off");

        //dependent tasks dueDate times combined
        int landingDueDep = landing.getDuration();
        int taxiInDueDep = landingDueDep + taxiIn.getDuration();
        int deboardingDueDep = taxiInDueDep + deboarding.getDuration();
        int unloadingDueDep = taxiInDueDep + unloading.getDuration();
        int fuelingDueDep = taxiInDueDep + fueling.getDuration();
        int cateringDueDep = taxiInDueDep + catering.getDuration();
        int cleaningDueDep = taxiInDueDep + cleaning.getDuration();
        int loadingDueDep = unloadingDueDep + loading.getDuration();
        int boardingDueDep = deboardingDueDep + boarding.getDuration();
        int taxiOutDueDep = boardingDueDep + taxiOut.getDuration();
        int takeOffDueDep = taxiOutDueDep + takeOff.getDuration();

        List<Flight> flights = flightRepository.findAll();
        for (Flight flight : flights) {
            List<Task> tasks = new ArrayList<>();
            // Create tasks for each landing
            tasks.add(createTask(landing, runway,
                    flight.getFirstSeen().plus(Duration.ofMinutes(landing.getDuration())),
                    flight.getPlannedArrival(), 0, null, null, false));

            // Save tasks to the database
            for (Task task : tasks) {
                task.setFlight(flight);
                taskRepository.save(task);
            }
        }
    }

    private Task createTask(Operation operation, Resource resource, Instant dueDate, Instant deadline, int priority, Instant started, Instant completed, Boolean isScheduled) {
        Task task = new Task();
        task.setOperation(operation);
        task.setResource(resource);
        task.setDueDate(dueDate);
        task.setDeadline(deadline);
        task.setPriority(priority);
        task.setStarted(started);
        task.setCompleted(completed);
        task.setIsScheduled(isScheduled);
        return task;
    }

    public void deleteAllTasks() {
        taskRepository.deleteAll();
    }

    public List<Task> getRunwayTasks() {
        return taskRepository.getRunwayTasks();
    }


    @Transactional
    public void scheduleTasks(){

        Resource runway = resourceRepository.findByName("Runway");
        Resource pilotCar = resourceRepository.findByName("Pilot Car");
        Resource gate = resourceRepository.findByName("Gate");

        Operation landing = operationRepository.findByName("Landing");
        Operation taxiIn = operationRepository.findByName("Taxi-In");
        Operation deboarding = operationRepository.findByName("Deboarding");
        Operation unloading = operationRepository.findByName("Unloading");
        Operation fueling = operationRepository.findByName("Fueling");
        Operation catering = operationRepository.findByName("Catering");
        Operation cleaning = operationRepository.findByName("Cleaning");
        Operation loading = operationRepository.findByName("Loading");
        Operation boarding = operationRepository.findByName("Boarding");
        Operation taxiOut = operationRepository.findByName("Taxi-Out");
        Operation takeOff = operationRepository.findByName("Take-Off");

        tasksRunway = getRunwayTasks().stream()
        .filter(task -> task.getFlight().getFirstSeen().isBefore(Instant.now()) && !task.getIsScheduled())
        .collect(Collectors.toList());
        if(currentRunway == null && !tasksRunway.isEmpty()){
            initializeRunway();
            initializeTasks();
            System.out.println("Initializer runned");
            return;
        }

        
        Task firstUncompletedTask = tasksRunway.stream()
        .filter(task -> !task.getIsScheduled())
        .findFirst()
        .orElse(null);

        if (firstUncompletedTask != null) {
            if(Instant.now().isBefore(currentRunway.getCompleted())) {
                return;
            }
            if (firstUncompletedTask.getOperation().getName().equals("Take-Off")) {
                // Find the taxiOut task for the same flight
                Task taxiOutTask = tasksRunway.stream()
                    .filter(task -> task.getFlight().equals(firstUncompletedTask.getFlight()) && task.getOperation().getName().equals("Taxi-Out"))
                    .findFirst()
                    .orElse(null);

                // If the taxiOut task is not completed, don't process the takeOff task
                if (taxiOutTask == null || currentRunway.getCompleted().isBefore(taxiOutTask.getCompleted())) {
                    return;
                }
            }
            // Set the started and completed times for the first uncompleted task
            if(firstUncompletedTask.getFlight().getFirstSeen().isAfter(currentRunway.getCompleted())){
                firstUncompletedTask.setStarted(firstUncompletedTask.getFlight().getFirstSeen());
                firstUncompletedTask.setCompleted(firstUncompletedTask.getFlight().getFirstSeen().plus(Duration.ofMinutes(landing.getDuration())));
                firstUncompletedTask.setIsScheduled(true);
            } else {
                firstUncompletedTask.setStarted(currentRunway.getCompleted());
                firstUncompletedTask.setCompleted(currentRunway.getCompleted().plus(Duration.ofMinutes(landing.getDuration())));
                firstUncompletedTask.setIsScheduled(true);
            }
        
            // Update currentRunway task
            currentRunway = firstUncompletedTask;
            landingCompleted = firstUncompletedTask.getCompleted();
            initializeTasks();
        
            // Save the updated task
            taskRepository.save(firstUncompletedTask);
        }
        
    }

    private void initializeRunway(){
        Resource runway = resourceRepository.findByName("Runway");
        Resource pilotCar = resourceRepository.findByName("Pilot Car");
        Resource gate = resourceRepository.findByName("Gate");

        Operation landing = operationRepository.findByName("Landing");
        Operation taxiIn = operationRepository.findByName("Taxi-In");
        Operation deboarding = operationRepository.findByName("Deboarding");
        Operation unloading = operationRepository.findByName("Unloading");
        Operation fueling = operationRepository.findByName("Fueling");
        Operation catering = operationRepository.findByName("Catering");
        Operation cleaning = operationRepository.findByName("Cleaning");
        Operation loading = operationRepository.findByName("Loading");
        Operation boarding = operationRepository.findByName("Boarding");
        Operation taxiOut = operationRepository.findByName("Taxi-Out");
        Operation takeOff = operationRepository.findByName("Take-Off");

        tasksRunway.get(0).setStarted(tasksRunway.get(0).getDueDate().minus(Duration.ofMinutes(landing.getDuration())));
        currentRunway = tasksRunway.get(0);
        tasksRunway.get(0).setCompleted(tasksRunway.get(0).getFlight().getFirstSeen().plus(Duration.ofMinutes(landing.getDuration())));
        landingCompleted = tasksRunway.get(0).getCompleted();
        tasksRunway.get(0).setIsScheduled(true);

//        Instant taxiInCompleted = landingCompleted.plus(Duration.ofMinutes(taxiIn.getDuration()));
//        currentTaxiIn = createTask(taxiIn, pilotCar, taxiInCompleted, tasksRunway.get(0).getDeadline(), 0, landingCompleted, taxiInCompleted, true);
//        currentTaxiIn.setFlight(tasksRunway.get(0).getFlight());
//        tasksTaxiIn.add(currentTaxiIn);
//        taskRepository.save(currentTaxiIn);
//
//        Instant deboardingCompleted = taxiInCompleted.plus(Duration.ofMinutes(deboarding.getDuration()));
//        currentDeboarding = createTask(deboarding, gate, deboardingCompleted, deboardingCompleted, 0, taxiInCompleted, deboardingCompleted, true);
//        currentDeboarding.setFlight(tasksRunway.get(0).getFlight());
//        tasksDeboarding.add(currentDeboarding);
//        taskRepository.save(currentDeboarding);
//
//        Instant unloadingCompleted = taxiInCompleted.plus(Duration.ofMinutes(unloading.getDuration()));
//        currentUnloading = createTask(unloading, gate, unloadingCompleted, unloadingCompleted, 0 , taxiInCompleted, unloadingCompleted, true);
//        currentUnloading.setFlight(tasksRunway.get(0).getFlight());
//        tasksUnloading.add(currentUnloading);
//        taskRepository.save(currentUnloading);
//
//        Instant fuelingCompleted = taxiInCompleted.plus(Duration.ofMinutes(fueling.getDuration()));
//        currentFueling = createTask(fueling, gate, fuelingCompleted, fuelingCompleted, 0, deboardingCompleted, fuelingCompleted, true);
//        currentFueling.setFlight(tasksRunway.get(0).getFlight());;
//        tasksFueling.add(currentFueling);
//        taskRepository.save(currentFueling);
//
//        Instant cateringCompleted = taxiInCompleted.plus(Duration.ofMinutes(catering.getDuration()));
//        currentCatering = createTask(catering, gate, cateringCompleted, cateringCompleted, 0, deboardingCompleted, cateringCompleted, true);
//        currentCatering.setFlight(tasksRunway.get(0).getFlight());
//        tasksCatering.add(currentCatering);
//        taskRepository.save(currentCatering);
//
//        Instant cleaningCompleted = taxiInCompleted.plus(Duration.ofMinutes(cleaning.getDuration()));
//        currentCleaning = createTask(cleaning, gate, cleaningCompleted, cleaningCompleted, 0, deboardingCompleted, cleaningCompleted, true);
//        currentCleaning.setFlight(tasksRunway.get(0).getFlight());
//        tasksCleaning.add(currentCleaning);
//        taskRepository.save(currentCleaning);
//
//        Instant loadingCompleted = cleaningCompleted.plus(Duration.ofMinutes(loading.getDuration()));
//        currentLoading = createTask(loading, gate, loadingCompleted, loadingCompleted, 0, unloadingCompleted, loadingCompleted, true);
//        currentLoading.setFlight(tasksRunway.get(0).getFlight());
//        tasksLoading.add(currentLoading);
//        taskRepository.save(currentLoading);
//
//        Instant boardingCompleted =  loadingCompleted.plus(Duration.ofMinutes(boarding.getDuration()));
//        currentBoarding = createTask(boarding, gate, boardingCompleted, boardingCompleted, 0, cateringCompleted, boardingCompleted, true);
//        currentBoarding.setFlight(tasksRunway.get(0).getFlight());
//        tasksBoarding.add(currentBoarding);
//        taskRepository.save(currentBoarding);
//
//        Instant taxiOutCompleted = boardingCompleted.plus(Duration.ofMinutes(taxiOut.getDuration()));
//        currentTaxiOut = createTask(taxiOut, pilotCar, taxiOutCompleted, taxiOutCompleted, 0, boardingCompleted, taxiOutCompleted, false);
//        currentTaxiOut.setFlight(tasksRunway.get(0).getFlight());
//        tasksTaxiOut.add(currentTaxiOut);
//        taskRepository.save(currentTaxiOut);
//
//        Instant takeOffCompleted = taxiOutCompleted.plus(Duration.ofMinutes(takeOff.getDuration()));
//        currentTakeOff = createTask(takeOff, runway, takeOffCompleted, takeOffCompleted, 0, null, null, false);
//        currentTakeOff.setFlight(tasksRunway.get(0).getFlight());
//        tasksRunway.add(currentTakeOff);
//        taskRepository.save(currentTakeOff);
    }

    private void initializeTasks(){
        Resource runway = resourceRepository.findByName("Runway");
        Resource pilotCar = resourceRepository.findByName("Pilot Car");
        Resource gate = resourceRepository.findByName("Gate");

        Operation landing = operationRepository.findByName("Landing");
        Operation taxiIn = operationRepository.findByName("Taxi-In");
        Operation deboarding = operationRepository.findByName("Deboarding");
        Operation unloading = operationRepository.findByName("Unloading");
        Operation fueling = operationRepository.findByName("Fueling");
        Operation catering = operationRepository.findByName("Catering");
        Operation cleaning = operationRepository.findByName("Cleaning");
        Operation loading = operationRepository.findByName("Loading");
        Operation boarding = operationRepository.findByName("Boarding");
        Operation taxiOut = operationRepository.findByName("Taxi-Out");
        Operation takeOff = operationRepository.findByName("Take-Off");

        Instant taxiInCompleted = landingCompleted.plus(Duration.ofMinutes(taxiIn.getDuration()));
        currentTaxiIn = createTask(taxiIn, pilotCar, taxiInCompleted, tasksRunway.get(0).getDeadline(), 0, null, null, false);
        currentTaxiIn.setFlight(currentRunway.getFlight());
        tasksTaxiIn.add(currentTaxiIn);
        taskRepository.save(currentTaxiIn);

        Instant deboardingCompleted = taxiInCompleted.plus(Duration.ofMinutes(deboarding.getDuration()));
        currentDeboarding = createTask(deboarding, gate, deboardingCompleted, deboardingCompleted, 0, null, null, false);
        currentDeboarding.setFlight(currentRunway.getFlight());
        tasksDeboarding.add(currentDeboarding);
        taskRepository.save(currentDeboarding);

        Instant unloadingCompleted = taxiInCompleted.plus(Duration.ofMinutes(unloading.getDuration()));
        currentUnloading = createTask(unloading, gate, unloadingCompleted, unloadingCompleted, 0 , null, null, false);
        currentUnloading.setFlight(currentRunway.getFlight());
        tasksUnloading.add(currentUnloading);
        taskRepository.save(currentUnloading);

        Instant fuelingCompleted = taxiInCompleted.plus(Duration.ofMinutes(fueling.getDuration()));
        currentFueling = createTask(fueling, gate, fuelingCompleted, fuelingCompleted, 0, null, null, false);
        currentFueling.setFlight(currentRunway.getFlight());;
        tasksFueling.add(currentFueling);
        taskRepository.save(currentFueling);

        Instant cateringCompleted = taxiInCompleted.plus(Duration.ofMinutes(catering.getDuration()));
        currentCatering = createTask(catering, gate, cateringCompleted, cateringCompleted, 0, null, null, false);
        currentCatering.setFlight(currentRunway.getFlight());
        tasksCatering.add(currentCatering);
        taskRepository.save(currentCatering);

        Instant cleaningCompleted = taxiInCompleted.plus(Duration.ofMinutes(cleaning.getDuration()));
        currentCleaning = createTask(cleaning, gate, cleaningCompleted, cleaningCompleted, 0, null, null, false);
        currentCleaning.setFlight(currentRunway.getFlight());
        tasksCleaning.add(currentCleaning);
        taskRepository.save(currentCleaning);

        Instant loadingCompleted = cleaningCompleted.plus(Duration.ofMinutes(loading.getDuration()));
        currentLoading = createTask(loading, gate, loadingCompleted, loadingCompleted, 0, null, null, false);
        currentLoading.setFlight(currentRunway.getFlight());
        tasksLoading.add(currentLoading);
        taskRepository.save(currentLoading);

        Instant boardingCompleted =  loadingCompleted.plus(Duration.ofMinutes(boarding.getDuration()));
        currentBoarding = createTask(boarding, gate, boardingCompleted, boardingCompleted, 0, null, null, false);
        currentBoarding.setFlight(currentRunway.getFlight());
        tasksBoarding.add(currentBoarding);
        taskRepository.save(currentBoarding);

        Instant taxiOutCompleted = boardingCompleted.plus(Duration.ofMinutes(taxiOut.getDuration()));
        currentTaxiOut = createTask(taxiOut, pilotCar, taxiOutCompleted, taxiOutCompleted, 0, null, null, false);
        currentTaxiOut.setFlight(currentRunway.getFlight());
        tasksTaxiOut.add(currentTaxiOut);
        taskRepository.save(currentTaxiOut);

        Instant takeOffCompleted = taxiOutCompleted.plus(Duration.ofMinutes(takeOff.getDuration()));
        currentTakeOff = createTask(takeOff, runway, takeOffCompleted, takeOffCompleted, 0, null, null, false);
        currentTakeOff.setFlight(currentRunway.getFlight());
        tasksRunway.add(currentTakeOff);
        taskRepository.save(currentTakeOff);
    }

//    @Transactional
//    public void scheduleTasks(){
//        List<Task> taskList = taskRepository.findAllByResourceName("Runway")
//                .stream()
//                .filter(task -> task.getOperation().getName().equals("Landing")).collect(Collectors.toList());
//
//        Duration duration = Duration.ofMinutes(operationRepository.findByName("Landing").getDuration());
//        taskList.get(0).setStarted(taskList.get(0).getDueDate().minus(duration));
//        Instant currentCompleted = taskList.get(0).getFlight().getFirstSeen().plus(duration);
//        taskList.get(0).setCompleted(currentCompleted);
//        taskList.remove(0);
//
//        while(!taskList.isEmpty()){
//            Task candidate = null;
//
//            for(int i=0; i<taskList.size(); i++){
//                if(taskList.get(i).getDueDate().isAfter(taskList.get(i).getDeadline())){
//                    taskList.get(i).addPriority();
//                }
//                if(candidate == null || taskList.get(i).getPriority() > 0){
//                    if((candidate == null && currentCompleted.isAfter(taskList.get(i).getDueDate()))
//                    ||(candidate.getPriority() == 0 && currentCompleted.isAfter(taskList.get(i).getDueDate()))){
//                        candidate = taskList.get(i);
//                    }
//                }
//            }
//
//            candidate.setStarted(currentCompleted);
//            currentCompleted = currentCompleted.plus(duration);
//            candidate.setCompleted(currentCompleted);
//            taskList.remove(candidate);
//        }
//    }
}
