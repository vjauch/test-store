import {Component, OnInit, provide} from "@angular/core";
import {HTTP_PROVIDERS} from "@angular/http";
import {Router, RouteConfig, ROUTER_DIRECTIVES, ROUTER_PROVIDERS} from "@angular/router-deprecated";
import {TestResultListComponent} from "./test-result/list/test-result-list.component";
import {RunListComponent} from "./run/run-list.component";
import {TestResultService} from "./test-result/test-result.service";
import {RunService} from "./run/run.service";
import {TestSuiteService} from "./test-suite/test-suite.service";
import {SidebarComponent} from "./sidebar/sidebar.component";
import {HistoryComponent} from "./history/history.component";
import {AddTestSuiteComponent} from "./test-suite/add/add-test-suite.component";
import {TestSuitesChangedEvent} from "./test-suite/test-suites-changed-event";
import {TestSuiteOverviewComponent} from "./test-suite/overview/test-suite-overview.component";
import {Window} from "./window";
import {HistoryService} from "./history/history.service";
import {TestResultDiffComponent} from "./test-result/diff/test-result-diff.component";
import {TestStatisticsComponent} from "./statistics/test-statistics.component";
import {TestStatisticsService} from "./statistics/test-statistics.service";
import {RevisionService} from "./revision/revision.service";
import {TestDetailsComponent} from "./test/test-details.component";
import {ExecutionOrderComponent} from "./test-result/execution-order/execution-order.component";

@Component({
    selector: 'app',
    templateUrl: 'app/app.html',
    styleUrls: ['app/app.css'],
    directives: [ROUTER_DIRECTIVES, SidebarComponent],
    providers: [
        ROUTER_PROVIDERS,
        HTTP_PROVIDERS,
        TestResultService,
        RunService,
        TestSuiteService,
        TestSuitesChangedEvent,
        HistoryService,
        TestStatisticsService,
        RevisionService,
        provide(Window, {useValue: window})
    ]
})
@RouteConfig([
    {
        path: '/testsuites/:testsuite_id/runs/:run_id/results',
        name: 'Results',
        component: TestResultListComponent,
    },
    {
        path: '/testsuites/:testsuite_id/runs',
        name: 'Runs',
        component: RunListComponent
    },
    {
        path: '/testsuites/:testsuite_id/history',
        name: 'History',
        component: HistoryComponent
    },
    {
        path: 'testsuites/new',
        name: 'New TestSuite',
        component: AddTestSuiteComponent
    },
    {
        path: 'testsuites/:testsuite_id',
        name: 'TestSuite',
        component: TestSuiteOverviewComponent
    },
    {
        path: 'testsuites/:testsuite_id/runs/:run_id/diff',
        name: 'Results Diff',
        component: TestResultDiffComponent
    },
    {
        path: 'testsuites/:testsuite_id/runs/:run_id/results/execution_order',
        name: 'Execution Order',
        component: ExecutionOrderComponent
    },
    {
        path: 'testsuites/:testsuite_id/statistics',
        name: 'Statistics',
        component: TestStatisticsComponent
    },
    {
        path: 'testsuites/:testsuite_id/tests/:test_name',
        name: 'Test Details',
        component: TestDetailsComponent
    }
])
export class AppComponent implements OnInit {

    constructor(private _router:Router) {

    }
    currentTestSuiteId:string;
    sidebarVisible = true;

    ngOnInit():any {
        this.onNewUrl();
        return this._router.subscribe(
            currentUrl => this.onNewUrl(),
            error => console.error(error)
        );
    }

    private onNewUrl() {
        var instruction = this._router.currentInstruction;
        if(instruction == null) return;
        var component = instruction.component;
        if(component == null) return;
        var params = component.params;
        for(var name in params) {
            if(!params.hasOwnProperty(name)) continue;
            if(name == 'testsuite_id'){
                this.currentTestSuiteId = params[name];
            }
        }
    }

    hideSidebar() {
        this.sidebarVisible = false;
    }

    showSidebar() {
        this.sidebarVisible = true;
    }
}