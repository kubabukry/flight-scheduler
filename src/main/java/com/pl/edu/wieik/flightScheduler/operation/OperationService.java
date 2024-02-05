package com.pl.edu.wieik.flightScheduler.operation;

import com.pl.edu.wieik.flightScheduler.flight.Flight;
import com.pl.edu.wieik.flightScheduler.flight.FlightRepository;
import com.pl.edu.wieik.flightScheduler.person.AlreadyExistsException;
import com.pl.edu.wieik.flightScheduler.person.NoSuchContent;
import com.pl.edu.wieik.flightScheduler.resource.ResourceRepository;
import com.pl.edu.wieik.flightScheduler.task.Task;
import com.pl.edu.wieik.flightScheduler.task.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class OperationService {
    private final OperationRepository operationRepository;

    private final TaskService taskService;

    private final ResourceRepository resourceRepository;
    private final FlightRepository flightRepository;

    public OperationService(OperationRepository operationRepository, TaskService taskService, ResourceRepository resourceRepository, FlightRepository flightRepository) {
        this.operationRepository = operationRepository;
        this.taskService = taskService;
        this.resourceRepository = resourceRepository;
        this.flightRepository = flightRepository;
    }

    public void createOperation(OperationCreationDto operationCreationDto){
        boolean operationExists = operationRepository.existsByName(operationCreationDto.getName());
        if(operationExists){
            throw new AlreadyExistsException("Operation with name "+operationCreationDto.getName()+" already exists");
        }
        Operation operation = new Operation();
        operation.setName(operationCreationDto.getName());
        operation.setDuration(operationCreationDto.getDuration());
        operationRepository.save(operation);
    }

    public void performUpdate(Long id, OperationUpdateDto operationUpdateDto){
        Operation operation = operationRepository.findById(id)
                .orElseThrow(() -> new NoSuchContent("No operation exists with id: " + id));
        operation.setDuration(operationUpdateDto.getDuration());
        operationRepository.save(operation);
    }

    public void updateOperation(Long id, OperationUpdateDto operationUpdateDto) {
        performUpdate(id, operationUpdateDto);
        taskService.calculateNewDeadlines();
    }

//    @Transactional
//    public void calculateNewDeadlines(){
//        Operation landing = operationRepository.findByName("Landing");
//        Operation taxiIn = operationRepository.findByName("Taxi-In");
//        Operation deboarding = operationRepository.findByName("Deboarding");
//        Operation unloading = operationRepository.findByName("Unloading");
//        Operation fueling = operationRepository.findByName("Fueling");
//        Operation catering = operationRepository.findByName("Catering");
//        Operation cleaning = operationRepository.findByName("Cleaning");
//        Operation loading = operationRepository.findByName("Loading");
//        Operation boarding = operationRepository.findByName("Boarding");
//        Operation taxiOut = operationRepository.findByName("Taxi-Out");
//
//        Duration landingDueDep = Duration.ofMinutes(landing.getDuration());
//        Duration taxiInDueDep = landingDueDep.plus(Duration.ofMinutes(taxiIn.getDuration()));
//        Duration deboardingDueDep = taxiInDueDep.plus(Duration.ofMinutes(deboarding.getDuration()));
//        Duration unloadingDueDep = taxiInDueDep.plus(Duration.ofMinutes(unloading.getDuration()));
//
//        Duration taxiOutDueDep = Duration.ofMinutes(taxiOut.getDuration());
//        Duration boardingDueDep = taxiOutDueDep.plus(Duration.ofMinutes(boarding.getDuration()));
//        Duration loadingDueDep = taxiOutDueDep.plus(Duration.ofMinutes(loading.getDuration()));
//        Duration cleaningDueDep = boardingDueDep.plus(Duration.ofMinutes(cleaning.getDuration()));
//        Duration cateringDueDep = boardingDueDep.plus(Duration.ofMinutes(catering.getDuration()));
//        Duration fuelingDueDep = boardingDueDep.plus(Duration.ofMinutes(fueling.getDuration()));
//
//        List<Flight> flights = flightRepository.findAllFlightsPastNow(Instant.now());
//
//        for (Flight flight : flights) {
//            Task taxiInTask = flight.getTaskList().get(1);
//            taxiInTask.setDeadline(flight.getPlannedArrival().plus(taxiInDueDep));
//
//            Task deboardingTask = flight.getTaskList().get(2);
//            deboardingTask.setDeadline(flight.getPlannedArrival().plus(deboardingDueDep));
//
//            Task unloadingTask = flight.getTaskList().get(3);
//            unloadingTask.setDeadline(flight.getPlannedArrival().plus(unloadingDueDep));
//
//            Task fuelingTask = flight.getTaskList().get(4);
//            fuelingTask.setDeadline(flight.getPlannedArrival().plus(fuelingDueDep));
//
//            Task cateringTask = flight.getTaskList().get(5);
//            cateringTask.setDeadline(flight.getPlannedArrival().plus(cateringDueDep));
//
//            Task cleaningTask = flight.getTaskList().get(6);
//            cleaningTask.setDeadline(flight.getPlannedArrival().plus(cleaningDueDep));
//
//            Task loadingTask = flight.getTaskList().get(7);
//            loadingTask.setDeadline(flight.getPlannedArrival().plus(loadingDueDep));
//
//            Task boardingTask = flight.getTaskList().get(8);
//            boardingTask.setDeadline(flight.getPlannedArrival().plus(boardingDueDep));
//
//            Task taxiOutTask = flight.getTaskList().get(9);
//            taxiOutTask.setDeadline(flight.getPlannedArrival().plus(taxiOutDueDep));
//        }
//    }

    public List<OperationDto> getOperationList() {
        return OperationMapper.mapOperationListToOperationDtoList(operationRepository.findAll());
    }

    public void deleteOperation(Long id) {
        if (!operationRepository.existsById(id)) {
            throw new NoSuchContent("No operation exists with id: " + id);
        }
        operationRepository.deleteById(id);
    }
}
