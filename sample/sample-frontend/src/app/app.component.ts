import { Component } from '@angular/core';
import { UserGroupDto } from './generated/models';
import { UserDto } from './generated/models/user-dto';
import { EnumPlaytestControllerService, FileDownloadControllerService, FileUploadControllerService, GenericControllerService, MapPlaytestControllerService, TimeControllerService, UserControllerService } from './generated/services';
import * as dayjs from 'dayjs';
import { Dayjs } from 'dayjs';
import * as customParseFormat from 'dayjs/plugin/customParseFormat';
import * as utc from 'dayjs/plugin/utc';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  private readonly NB_CHARACTERS = 150;

  title = 'sample-frontend';

  result: string[] = ["Awaiting webservice request"];

  compteur: number = 0;

  uploadMode = 'multiple';

  filesToUpload: File[] = [];

  constructor(private readonly userControllerService: UserControllerService, 
    private readonly enumPlaytestControllerService: EnumPlaytestControllerService,
    private readonly mapPlaytestControllerService: MapPlaytestControllerService,
    private readonly timeControllerService: TimeControllerService,
    private readonly fileUploadControllerService: FileUploadControllerService,
    private readonly fileDownloadControllerService: FileDownloadControllerService,
    private readonly genericControllerService: GenericControllerService) {
      dayjs.extend(utc);
  }

  public async callWebservice() {

    switch(this.compteur) {
      case 0: {
        this.displayResponse(await this.enumPlaytestControllerService.getAuthorityWrapper());
        break; 
      } 
      case 1: {   
        this.displayResponse(await this.enumPlaytestControllerService.getAuthorities());
        break; 
      }
      case 2: {   
        this.displayResponse(await this.enumPlaytestControllerService.getAuthority());
        break; 
      }
      case 3: {   
        this.displayResponse(await this.enumPlaytestControllerService.setAuthorityWrapper(
          {
            body: {
              authority: 'ACCESS_APP',
              authorityList: ['READ_USER', 'UPDATE_USER']
            }
          }));
        break; 
      }
      case 4: {   
        this.displayResponse(await this.enumPlaytestControllerService.setAuthorityList({body: ['READ_USER', 'UPDATE_USER']}));
        break; 
      }
      case 5: {  
        this.displayResponse(await this.enumPlaytestControllerService.setAuthorityAsPathParam({authority: 'UPDATE_USER'})); 
        break; 
      }
      case 6: { 
        this.displayResponse(await this.enumPlaytestControllerService.setAuthorityAsQueryParam({authority: 'READ_USER'}));  
        break; 
      }
      case 7: {   
        this.displayResponse(await this.userControllerService.updateUser({body: this.createUser()}));  
        break; 
      }
      case 8: {   
        this.displayResponse(await this.userControllerService.getAllUsernames());  
        break; 
      }
      case 9: {   
        this.displayResponse(await this.userControllerService.getNbUsers()); 
        break; 
      }
      case 10: {   
        this.displayResponse(await this.userControllerService.getNumberList()); 
        break; 
      }
      case 11: {   
        this.displayResponse(await this.mapPlaytestControllerService.getMapString()); 
        break; 
      }
      case 12: { 
        this.displayResponse(await this.mapPlaytestControllerService.getMapUsers());   
        break; 
      }
      case 13: {   
        this.displayResponse(await this.mapPlaytestControllerService.postMapInt({
          body: {
            22:'Okidoki',
            23: 'alright'
          }
        }));   
        break; 
      }
      case 14: {   
        this.displayResponse(await this.mapPlaytestControllerService.postMapAccount({
          body: {
            '22': this.createUser(),
            '23': this.createUser()
          }
        })); 
        break; 
      }
      case 15: {   
        this.displayResponse(await this.userControllerService.getUsergroupById({id: 1}));
        break; 
      }
      case 16: {   
        const userGroup: UserGroupDto = {};
        const subGroup: UserGroupDto = {};
        subGroup.members = [this.createUser(), this.createUser()];
        userGroup.leader = this.createUser();
        userGroup.subgroups = [subGroup];
        this.displayResponse(await this.userControllerService.setUsergroup({body: userGroup}));
        break; 
      }
      case 17: {   
        this.displayResponse(await this.timeControllerService.getTimeDto());
        break; 
      }
      case 18: {   
        this.displayResponse(await this.timeControllerService.getCurrentDate());
        break; 
      }
      case 19: {   
        this.displayResponse(await this.timeControllerService.getCurrentDateTime());
        break; 
      }
      case 20: {   
        this.displayResponse(await this.timeControllerService.getCurrentInstant());
        break; 
      }
      case 21: {  
        console.info(dayjs.utc());
        console.info(dayjs.utc().format('DD/MM/YYYY'));
        this.displayResponse(await this.timeControllerService.setCurrentDate({date: dayjs.utc().format('DD/MM/YYYY')})); 
        break; 
      }
      case 22: {   
        this.displayResponse(await this.timeControllerService.setCurrentDateTime({dateTime: dayjs.utc().format('DD/MM/YYYY-HH:mm:ss')})); 
        break; 
      }
      case 23: {  
        this.displayResponse(await this.timeControllerService.setCurrentInstant({instant: dayjs.utc().toISOString()})); 
        break; 
      }
      case 24: {   
        this.displayResponse(await this.genericControllerService.setAccountPage({
          body: {
            content: [this.createUser(), this.createUser()],
            hasNext: false,
            totalElements: 2,
            totalPages: 1
          }
        })); 
        break; 
      } case 25: {   
        this.displayResponse(await this.genericControllerService.setTimePage({
          body: {
            content: [ {
              date: dayjs.utc().format('YYYY-MM-DD')
            }, {
              dateTime: dayjs.utc().toISOString()
            }],
            hasNext: false,
            totalElements: 2,
            totalPages: 1
          }
        })); 
        break; 
      } case 26: {   
        this.displayResponse(await this.userControllerService.getUserDtoPage()); 
        break; 
      } case 27: {   
        this.displayResponse(await this.userControllerService.getInterfaceTestDto()); 
        break; 
      } case 28: {   
        this.displayResponse(await this.userControllerService.getOptionalUser({empty: false})); 
        break; 
      } case 29: {   
        this.displayResponse(await this.userControllerService.getOptionalUser({empty: true})); 
        break; 
      } case 30: {   
        this.displayResponse(await this.userControllerService.getWrappedUser()); 
        break; 
      } case 31: {   
        this.displayResponse(await this.userControllerService.getResponseUser()); 
        break; 
      }

    } 
    this.compteur++;
  }

  public loadFiles(eventTarget: EventTarget | null) {
    this.filesToUpload = [];
    if(eventTarget) {
      const fileList: FileList | null = (eventTarget as HTMLInputElement).files;
      if(fileList && fileList.length > 0) {
        const files: File[] = [];
        for(let i = 0; i < fileList.length; i++) {
          const file = fileList.item(i);
          if(file !== null) {
            files.push(file);
            
          } 
        }
        this.filesToUpload = files;
      }
     
    }
  } 

  public async sendMultipleFiles() {
    this.displayResponse(await this.fileUploadControllerService.uploadFiles({myId: 1053, body: { files: this.filesToUpload}})); 
  }

  public async sendSingleFile() {
    this.displayResponse(await this.fileUploadControllerService.uploadSingleFile({myId: 1053, body: { file: this.filesToUpload[0]}})); 
  }

  public async sendNonRequiredFiles() {
    this.displayResponse(await this.fileUploadControllerService.uploadNonRequiredFiles({myId: 1053, body: { files: this.filesToUpload}})); 
  }

  public async sendNonRequiredFilesWithoutFile() {
    this.displayResponse(await this.fileUploadControllerService.uploadNonRequiredFiles({myId: 1053})); 
  }

  private createUser(): UserDto {
    const user: UserDto = {
      email: 'john@bob.com'
    }
    return user;
  }

  private displayResponse(response: any) {
    this.result = [];
    if(response) {
      const r = JSON.stringify(response);
      const nbSlice = r.length / this.NB_CHARACTERS;
      let firstIndex = 0;
      for (let i = 0; i < nbSlice; i++) {
        this.result[i] = r.slice(firstIndex, firstIndex + this.NB_CHARACTERS);
        firstIndex += this.NB_CHARACTERS;
      }
      if(firstIndex < r.length - 1) {
        this.result[nbSlice] = r.slice(firstIndex);
      }
    }
   
  }

  public downloadFile() {
    const url = FileDownloadControllerService.FileDownloadControllerGetResourceFilePath;
    const fileName = "mon-image.jpg";
    const link = document.createElement('a');
    link.href = url;
    if (fileName) {
      link.download = fileName;
    }
    // ici on utilise link.dispatchEvent plutot que link.click() car ce dernier n'est pas géré par la version 60.9 de firefox
    // à modifier si la version du navigateur du client vient à être mise à jour
    link.dispatchEvent(new MouseEvent(`click`));
    // https://stackoverflow.com/questions/49209756/do-i-always-need-to-call-url-revokeobjecturl-explicitly
    // https://stackoverflow.com/questions/54156588/revokeobjecturl-not-work-in-mozilla-firefox-and-microsoft-edge
    // Pour s'assurer qu'on ne révoque par trop tot l'URL pour le téléchargement
    setTimeout(() => {
      window.URL.revokeObjectURL(url);
    }, 3000);
  }
}
