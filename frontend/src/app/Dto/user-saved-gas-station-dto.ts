import { PlaceSelection } from "./place-selection";

export interface UserSavedGasStationDto {
  alias: string;
  idEstacion: number;
  nombreEstacion: string;
  marca: string;
  direccion: string;
  placeSelection: PlaceSelection;
}